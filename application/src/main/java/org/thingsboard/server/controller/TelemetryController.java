/**
 * Copyright © 2016-2021 The Thingsboard Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.thingsboard.server.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Function;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import org.thingsboard.common.util.ThingsBoardThreadFactory;
import org.thingsboard.rule.engine.api.msg.DeviceAttributesEventNotificationMsg;
import org.thingsboard.server.common.data.DataConstants;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.TenantProfile;
import org.thingsboard.server.common.data.audit.ActionType;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.EntityIdFactory;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UUIDBased;
import org.thingsboard.server.common.data.kv.Aggregation;
import org.thingsboard.server.common.data.kv.AttributeKey;
import org.thingsboard.server.common.data.kv.AttributeKvEntry;
import org.thingsboard.server.common.data.kv.BaseAttributeKvEntry;
import org.thingsboard.server.common.data.kv.BaseDeleteTsKvQuery;
import org.thingsboard.server.common.data.kv.BaseReadTsKvQuery;
import org.thingsboard.server.common.data.kv.BasicTsKvEntry;
import org.thingsboard.server.common.data.kv.BooleanDataEntry;
import org.thingsboard.server.common.data.kv.DataType;
import org.thingsboard.server.common.data.kv.DeleteTsKvQuery;
import org.thingsboard.server.common.data.kv.DoubleDataEntry;
import org.thingsboard.server.common.data.kv.JsonDataEntry;
import org.thingsboard.server.common.data.kv.KvEntry;
import org.thingsboard.server.common.data.kv.LongDataEntry;
import org.thingsboard.server.common.data.kv.ReadTsKvQuery;
import org.thingsboard.server.common.data.kv.StringDataEntry;
import org.thingsboard.server.common.data.kv.TsKvEntry;
import org.thingsboard.server.common.data.tenant.profile.DefaultTenantProfileConfiguration;
import org.thingsboard.server.common.transport.adaptor.JsonConverter;
import org.thingsboard.server.dao.timeseries.TimeseriesService;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.security.AccessValidator;
import org.thingsboard.server.service.security.model.SecurityUser;
import org.thingsboard.server.service.security.permission.Operation;
import org.thingsboard.server.service.telemetry.AttributeData;
import org.thingsboard.server.service.telemetry.TsData;
import org.thingsboard.server.service.telemetry.exception.InvalidParametersException;
import org.thingsboard.server.service.telemetry.exception.UncheckedApiException;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.thingsboard.server.controller.ControllerConstants.DEVICE_ID_PARAM_DESCRIPTION;
import static org.thingsboard.server.controller.ControllerConstants.ENTITY_ID_PARAM_DESCRIPTION;
import static org.thingsboard.server.controller.ControllerConstants.ENTITY_TYPE_PARAM_DESCRIPTION;
import static org.thingsboard.server.controller.ControllerConstants.SORT_ORDER_ALLOWABLE_VALUES;
import static org.thingsboard.server.controller.ControllerConstants.SORT_ORDER_DESCRIPTION;
import static org.thingsboard.server.controller.ControllerConstants.TENANT_OR_CUSTOMER_AUTHORITY_PARAGRAPH;


/**
 * Created by ashvayka on 22.03.18.
 */
@RestController
@TbCoreComponent
@RequestMapping(TbUrlConstants.TELEMETRY_URL_PREFIX)
@Slf4j
public class TelemetryController extends BaseController {

    private static final String ATTRIBUTES_SCOPE_DESCRIPTION = "A string value representing the attributes scope. For example, 'SERVER_SCOPE'.";
    private static final String ATTRIBUTES_KEYS_DESCRIPTION = "A string value representing the comma-separated list of attributes keys. For example, 'active,inactivityAlarmTime'.";
    private static final String ATTRIBUTES_SCOPE_ALLOWED_VALUES = "SERVER_SCOPE, CLIENT_SCOPE, SHARED_SCOPE";
    private static final String ATTRIBUTES_JSON_REQUEST_DESCRIPTION = "A string value representing the json object. For example, '{\"key\":\"value\"}'";
    private static final String ATTRIBUTE_DATA_CLASS_DESCRIPTION = "AttributeData class represents information regarding a particular attribute and includes the next parameters: 'lastUpdatesTs' - a long value representing the timestamp of the last attribute modification in milliseconds. 'key' - attribute key name, and 'value' - attribute value.";
    private static final String GET_ALL_ATTRIBUTES_BASE_DESCRIPTION = "Returns a JSON structure that represents a list of AttributeData class objects for the selected entity based on the specified comma-separated list of attribute key names. " + ATTRIBUTE_DATA_CLASS_DESCRIPTION;
    private static final String GET_ALL_ATTRIBUTES_BY_SCOPE_BASE_DESCRIPTION = "Returns a JSON structure that represents a list of AttributeData class objects for the selected entity based on the attributes scope selected and a comma-separated list of attribute key names. " + ATTRIBUTE_DATA_CLASS_DESCRIPTION;

    private static final String TS_DATA_CLASS_DESCRIPTION = "TsData class is a timeseries data point for specific telemetry key that includes 'value' - object value, and 'ts' - a long value representing timestamp in milliseconds for this value. ";

    private static final String TELEMETRY_KEYS_BASE_DESCRIPTION = "A string value representing the comma-separated list of telemetry keys.";
    private static final String TELEMETRY_KEYS_DESCRIPTION = TELEMETRY_KEYS_BASE_DESCRIPTION + " If keys are not selected, the result will return all latest timeseries. For example, 'temp,humidity'.";
    private static final String TELEMETRY_SCOPE_DESCRIPTION = "Value is not used in the API call implementation. However, you need to specify whatever value cause scope is a path variable.";
    private static final String TELEMETRY_JSON_REQUEST_DESCRIPTION = "A string value representing the json object. For example, '{\"key\":\"value\"}' or '{\"ts\":1527863043000,\"values\":{\"key1\":\"value1\",\"key2\":\"value2\"}}' or [{\"ts\":1527863043000,\"values\":{\"key1\":\"value1\",\"key2\":\"value2\"}}, {\"ts\":1527863053000,\"values\":{\"key1\":\"value3\",\"key2\":\"value4\"}}]";


    private static final String STRICT_DATA_TYPES_DESCRIPTION = "A boolean value to specify if values of selected telemetry keys will represent string values(by default) or use strict data type.";
    private static final String INVALID_ENTITY_ID_OR_ENTITY_TYPE_DESCRIPTION = "Referencing a non-existing entity Id or invalid entity type will cause an error. ";

    private static final String SAVE_ENTITY_ATTRIBUTES_DESCRIPTION = "Creates or updates the entity attributes based on entity id, entity type, specified attributes scope " +
            "and request payload that represents a JSON object with key-value format of attributes to create or update. " +
            "For example, '{\"temperature\": 26}'. Key is a unique parameter and cannot be overwritten. Only value can be overwritten for the key. ";
    private static final String SAVE_ATTIRIBUTES_STATUS_OK = "Attribute from the request was created or updated. ";
    private static final String INVALID_STRUCTURE_OF_THE_REQUEST = "Invalid structure of the request";
    private static final String SAVE_ATTIRIBUTES_STATUS_BAD_REQUEST = INVALID_STRUCTURE_OF_THE_REQUEST + " or invalid attributes scope provided.";
    private static final String SAVE_ENTITY_ATTRIBUTES_STATUS_OK = "Platform creates an audit log event about entity attributes updates with action type 'ATTRIBUTES_UPDATED', " +
            "and also sends event msg to the rule engine with msg type 'ATTRIBUTES_UPDATED'.";
    private static final String SAVE_ENTITY_ATTRIBUTES_STATUS_UNAUTHORIZED = "User is not authorized to save entity attributes for selected entity. Most likely, User belongs to different Customer or Tenant.";
    private static final String SAVE_ENTITY_ATTRIBUTES_STATUS_INTERNAL_SERVER_ERROR = "The exception was thrown during processing the request. " +
            "Platform creates an audit log event about entity attributes updates with action type 'ATTRIBUTES_UPDATED' that includes an error stacktrace.";
    private static final String SAVE_ENTITY_TIMESERIES_DESCRIPTION = "Creates or updates the entity timeseries based on entity id, entity type " +
            "and request payload that represents a JSON object with key-value or ts-values format. " +
            "For example, '{\"temperature\": 26}'  or '{\"ts\":1634712287000,\"values\":{\"temperature\":26, \"humidity\":87}}', " +
            "or JSON array with inner objects inside of ts-values format. " +
            "For example, '[{\"ts\":1634712287000,\"values\":{\"temperature\":26, \"humidity\":87}}, {\"ts\":1634712588000,\"values\":{\"temperature\":25, \"humidity\":88}}]'. " +
            "The scope parameter is not used in the API call implementation but should be specified whatever value because it is used as a path variable. ";
    private static final String SAVE_ENTITY_TIMESERIES_STATUS_OK = "Timeseries from the request was created or updated. " +
            "Platform creates an audit log event about entity timeseries updates with action type 'TIMESERIES_UPDATED'.";
    private static final String SAVE_ENTITY_TIMESERIES_STATUS_UNAUTHORIZED = "User is not authorized to save entity timeseries for selected entity. Most likely, User belongs to different Customer or Tenant.";
    private static final String SAVE_ENTITY_TIMESERIES_STATUS_INTERNAL_SERVER_ERROR = "The exception was thrown during processing the request. " +
            "Platform creates an audit log event about entity timeseries updates with action type 'TIMESERIES_UPDATED' that includes an error stacktrace.";

    @Autowired
    private TimeseriesService tsService;

    @Autowired
    private AccessValidator accessValidator;

    @Value("${transport.json.max_string_value_length:0}")
    private int maxStringValueLength;

    private ExecutorService executor;

    private static final ObjectMapper mapper = new ObjectMapper();

    @PostConstruct
    public void initExecutor() {
        executor = Executors.newSingleThreadExecutor(ThingsBoardThreadFactory.forName("telemetry-controller"));
    }

    @PreDestroy
    public void shutdownExecutor() {
        if (executor != null) {
            executor.shutdownNow();
        }
    }

    @ApiOperation(value = "Get all attribute keys (getAttributeKeys)",
            notes = "Returns a list of all attribute key names for the selected entity. " +
                    "In the case of device entity specified, a response will include merged attribute key names list from each scope: " +
                    "SERVER_SCOPE, CLIENT_SCOPE, SHARED_SCOPE. "
                    + INVALID_ENTITY_ID_OR_ENTITY_TYPE_DESCRIPTION + TENANT_OR_CUSTOMER_AUTHORITY_PARAGRAPH,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/{entityType}/{entityId}/keys/attributes", method = RequestMethod.GET)
    @ResponseBody
    public DeferredResult<ResponseEntity> getAttributeKeys(
            @ApiParam(value = ENTITY_TYPE_PARAM_DESCRIPTION) @PathVariable("entityType") String entityType,
            @ApiParam(value = ENTITY_ID_PARAM_DESCRIPTION) @PathVariable("entityId") String entityIdStr) throws ThingsboardException {
        return accessValidator.validateEntityAndCallback(getCurrentUser(), Operation.READ_ATTRIBUTES, entityType, entityIdStr, this::getAttributeKeysCallback);
    }

    @ApiOperation(value = "Get all attributes keys by scope (getAttributeKeysByScope)",
            notes = "Returns a list of attribute key names from the specified attributes scope for the selected entity. " +
                    "If scope parameter is omitted, Get all attribute keys(getAttributeKeys) API will be called. "
                    + INVALID_ENTITY_ID_OR_ENTITY_TYPE_DESCRIPTION + TENANT_OR_CUSTOMER_AUTHORITY_PARAGRAPH,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/{entityType}/{entityId}/keys/attributes/{scope}", method = RequestMethod.GET)
    @ResponseBody
    public DeferredResult<ResponseEntity> getAttributeKeysByScope(
            @ApiParam(value = ENTITY_TYPE_PARAM_DESCRIPTION) @PathVariable("entityType") String entityType,
            @ApiParam(value = ENTITY_ID_PARAM_DESCRIPTION) @PathVariable("entityId") String entityIdStr,
            @ApiParam(value = ATTRIBUTES_SCOPE_DESCRIPTION, allowableValues = ATTRIBUTES_SCOPE_ALLOWED_VALUES) @PathVariable("scope") String scope) throws ThingsboardException {
        return accessValidator.validateEntityAndCallback(getCurrentUser(), Operation.READ_ATTRIBUTES, entityType, entityIdStr,
                (result, tenantId, entityId) -> getAttributeKeysCallback(result, tenantId, entityId, scope));
    }

    @ApiOperation(value = "Get attributes (getAttributes)",
            notes = GET_ALL_ATTRIBUTES_BASE_DESCRIPTION + " If 'keys' parameter is omitted, AttributeData class objects will be added to the response for all existing keys of the selected entity. " +
                    INVALID_ENTITY_ID_OR_ENTITY_TYPE_DESCRIPTION + TENANT_OR_CUSTOMER_AUTHORITY_PARAGRAPH,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/{entityType}/{entityId}/values/attributes", method = RequestMethod.GET)
    @ResponseBody
    public DeferredResult<ResponseEntity> getAttributes(
            @ApiParam(value = ENTITY_TYPE_PARAM_DESCRIPTION) @PathVariable("entityType") String entityType,
            @ApiParam(value = ENTITY_ID_PARAM_DESCRIPTION) @PathVariable("entityId") String entityIdStr,
            @ApiParam(value = ATTRIBUTES_KEYS_DESCRIPTION) @RequestParam(name = "keys", required = false) String keysStr) throws ThingsboardException {
        SecurityUser user = getCurrentUser();
        return accessValidator.validateEntityAndCallback(getCurrentUser(), Operation.READ_ATTRIBUTES, entityType, entityIdStr,
                (result, tenantId, entityId) -> getAttributeValuesCallback(result, user, entityId, null, keysStr));
    }

    @ApiOperation(value = "Get attributes by scope (getAttributesByScope)",
            notes = GET_ALL_ATTRIBUTES_BY_SCOPE_BASE_DESCRIPTION + " In case that 'keys' parameter is not selected, " +
                    "AttributeData class objects will be added to the response for all existing attribute keys from the " +
                    "specified attributes scope of the selected entity. If 'scope' parameter is omitted, " +
                    "Get attributes (getAttributes) API will be called. " + INVALID_ENTITY_ID_OR_ENTITY_TYPE_DESCRIPTION + TENANT_OR_CUSTOMER_AUTHORITY_PARAGRAPH,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/{entityType}/{entityId}/values/attributes/{scope}", method = RequestMethod.GET)
    @ResponseBody
    public DeferredResult<ResponseEntity> getAttributesByScope(
            @ApiParam(value = ENTITY_TYPE_PARAM_DESCRIPTION) @PathVariable("entityType") String entityType,
            @ApiParam(value = ENTITY_ID_PARAM_DESCRIPTION) @PathVariable("entityId") String entityIdStr,
            @ApiParam(value = ATTRIBUTES_SCOPE_DESCRIPTION, allowableValues = ATTRIBUTES_SCOPE_ALLOWED_VALUES) @PathVariable("scope") String scope,
            @ApiParam(value = ATTRIBUTES_KEYS_DESCRIPTION) @RequestParam(name = "keys", required = false) String keysStr) throws ThingsboardException {
        SecurityUser user = getCurrentUser();
        return accessValidator.validateEntityAndCallback(getCurrentUser(), Operation.READ_ATTRIBUTES, entityType, entityIdStr,
                (result, tenantId, entityId) -> getAttributeValuesCallback(result, user, entityId, scope, keysStr));
    }

    @ApiOperation(value = "Get timeseries keys (getTimeseriesKeys)",
            notes = "Returns a list of all telemetry key names for the selected entity based on entity id and entity type specified. " +
                    INVALID_ENTITY_ID_OR_ENTITY_TYPE_DESCRIPTION + TENANT_OR_CUSTOMER_AUTHORITY_PARAGRAPH,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/{entityType}/{entityId}/keys/timeseries", method = RequestMethod.GET)
    @ResponseBody
    public DeferredResult<ResponseEntity> getTimeseriesKeys(
            @ApiParam(value = ENTITY_TYPE_PARAM_DESCRIPTION) @PathVariable("entityType") String entityType,
            @ApiParam(value = ENTITY_ID_PARAM_DESCRIPTION) @PathVariable("entityId") String entityIdStr) throws ThingsboardException {
        return accessValidator.validateEntityAndCallback(getCurrentUser(), Operation.READ_TELEMETRY, entityType, entityIdStr,
                (result, tenantId, entityId) -> Futures.addCallback(tsService.findAllLatest(tenantId, entityId), getTsKeysToResponseCallback(result), MoreExecutors.directExecutor()));
    }

    @ApiOperation(value = "Get latest timeseries (getLatestTimeseries)",
            notes = "Returns a JSON structure that represents a Map, where the map key is a telemetry key name " +
                    "and map value - is a singleton list of TsData class objects. "
                    + TS_DATA_CLASS_DESCRIPTION + INVALID_ENTITY_ID_OR_ENTITY_TYPE_DESCRIPTION + TENANT_OR_CUSTOMER_AUTHORITY_PARAGRAPH,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/{entityType}/{entityId}/values/timeseries", method = RequestMethod.GET)
    @ResponseBody
    public DeferredResult<ResponseEntity> getLatestTimeseries(
            @ApiParam(value = ENTITY_TYPE_PARAM_DESCRIPTION) @PathVariable("entityType") String entityType,
            @ApiParam(value = ENTITY_ID_PARAM_DESCRIPTION) @PathVariable("entityId") String entityIdStr,
            @ApiParam(value = TELEMETRY_KEYS_DESCRIPTION) @RequestParam(name = "keys", required = false) String keysStr,
            @ApiParam(value = STRICT_DATA_TYPES_DESCRIPTION)
            @RequestParam(name = "useStrictDataTypes", required = false, defaultValue = "false") Boolean useStrictDataTypes) throws ThingsboardException {
        SecurityUser user = getCurrentUser();
        return accessValidator.validateEntityAndCallback(getCurrentUser(), Operation.READ_TELEMETRY, entityType, entityIdStr,
                (result, tenantId, entityId) -> getLatestTimeseriesValuesCallback(result, user, entityId, keysStr, useStrictDataTypes));
    }

    @ApiOperation(value = "Get timeseries (getTimeseries)",
            notes = "Returns a JSON structure that represents a Map, where the map key is a telemetry key name " +
                    "and map value - is a list of TsData class objects. " + TS_DATA_CLASS_DESCRIPTION +
                    "This method allows us to group original data into intervals and aggregate it using one of the aggregation methods or just limit the number of TsData objects to fetch for each key specified. " +
                    "See the desription of the request parameters for more details. " +
                    "The result can also be sorted in ascending or descending order. "
                    + INVALID_ENTITY_ID_OR_ENTITY_TYPE_DESCRIPTION + TENANT_OR_CUSTOMER_AUTHORITY_PARAGRAPH,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/{entityType}/{entityId}/values/timeseries", method = RequestMethod.GET, params = {"keys", "startTs", "endTs"})
    @ResponseBody
    public DeferredResult<ResponseEntity> getTimeseries(
            @ApiParam(value = ENTITY_TYPE_PARAM_DESCRIPTION) @PathVariable("entityType") String entityType,
            @ApiParam(value = ENTITY_ID_PARAM_DESCRIPTION) @PathVariable("entityId") String entityIdStr,
            @ApiParam(value = TELEMETRY_KEYS_BASE_DESCRIPTION) @RequestParam(name = "keys") String keys,
            @ApiParam(value = "A long value representing the start timestamp of search time range in milliseconds.")
            @RequestParam(name = "startTs") Long startTs,
            @ApiParam(value = "A long value representing the end timestamp of search time range in milliseconds.")
            @RequestParam(name = "endTs") Long endTs,
            @ApiParam(value = "A long value representing the aggregation interval range in milliseconds.")
            @RequestParam(name = "interval", defaultValue = "0") Long interval,
            @ApiParam(value = "An integer value that represents a max number of timeseries data points to fetch." +
                    " This parameter is used only in the case if 'agg' parameter is set to 'NONE'.", defaultValue = "100")
            @RequestParam(name = "limit", defaultValue = "100") Integer limit,
            @ApiParam(value = "A string value representing the aggregation function. " +
                    "If the interval is not specified, 'agg' parameter will use 'NONE' value.",
                    allowableValues = "MIN, MAX, AVG, SUM, COUNT, NONE")
            @RequestParam(name = "agg", defaultValue = "NONE") String aggStr,
            @ApiParam(value = SORT_ORDER_DESCRIPTION, allowableValues = SORT_ORDER_ALLOWABLE_VALUES)
            @RequestParam(name = "orderBy", defaultValue = "DESC") String orderBy,
            @ApiParam(value = STRICT_DATA_TYPES_DESCRIPTION)
            @RequestParam(name = "useStrictDataTypes", required = false, defaultValue = "false") Boolean useStrictDataTypes) throws ThingsboardException {
        return accessValidator.validateEntityAndCallback(getCurrentUser(), Operation.READ_TELEMETRY, entityType, entityIdStr,
                (result, tenantId, entityId) -> {
                    // If interval is 0, convert this to a NONE aggregation, which is probably what the user really wanted
                    Aggregation agg = interval == 0L ? Aggregation.valueOf(Aggregation.NONE.name()) : Aggregation.valueOf(aggStr);
                    List<ReadTsKvQuery> queries = toKeysList(keys).stream().map(key -> new BaseReadTsKvQuery(key, startTs, endTs, interval, limit, agg, orderBy))
                            .collect(Collectors.toList());

                    Futures.addCallback(tsService.findAll(tenantId, entityId, queries), getTsKvListCallback(result, useStrictDataTypes), MoreExecutors.directExecutor());
                });
    }

    @ApiOperation(value = "Save or update device attributes (saveDeviceAttributes)",
            notes = "Creates or updates the device attributes based on device id, specified attribute scope, " +
                    "and request payload that represents a JSON object with key-value format of attributes to create or update. " +
                    "For example, '{\"temperature\": 26}'. Key is a unique parameter and cannot be overwritten. Only value can " +
                    "be overwritten for the key. " + TENANT_OR_CUSTOMER_AUTHORITY_PARAGRAPH,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = SAVE_ATTIRIBUTES_STATUS_OK +
                    "Platform creates an audit log event about device attributes updates with action type 'ATTRIBUTES_UPDATED', " +
                    "and also sends event msg to the rule engine with msg type 'ATTRIBUTES_UPDATED'."),
            @ApiResponse(code = 400, message = SAVE_ATTIRIBUTES_STATUS_BAD_REQUEST),
            @ApiResponse(code = 401, message = "User is not authorized to save device attributes for selected device. Most likely, User belongs to different Customer or Tenant."),
            @ApiResponse(code = 500, message = "The exception was thrown during processing the request. " +
                    "Platform creates an audit log event about device attributes updates with action type 'ATTRIBUTES_UPDATED' that includes an error stacktrace."),
    })
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/{deviceId}/{scope}", method = RequestMethod.POST)
    @ResponseBody
    public DeferredResult<ResponseEntity> saveDeviceAttributes(
            @ApiParam(value = DEVICE_ID_PARAM_DESCRIPTION) @PathVariable("deviceId") String deviceIdStr,
            @ApiParam(value = ATTRIBUTES_SCOPE_DESCRIPTION, allowableValues = ATTRIBUTES_SCOPE_ALLOWED_VALUES) @PathVariable("scope") String scope,
            @ApiParam(value = ATTRIBUTES_JSON_REQUEST_DESCRIPTION) @RequestBody JsonNode request) throws ThingsboardException {
        EntityId entityId = EntityIdFactory.getByTypeAndUuid(EntityType.DEVICE, deviceIdStr);
        return saveAttributes(getTenantId(), entityId, scope, request);
    }

    @ApiOperation(value = "Save or update attributes (saveEntityAttributesV1)",
            notes = SAVE_ENTITY_ATTRIBUTES_DESCRIPTION + INVALID_ENTITY_ID_OR_ENTITY_TYPE_DESCRIPTION + TENANT_OR_CUSTOMER_AUTHORITY_PARAGRAPH,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = SAVE_ATTIRIBUTES_STATUS_OK + SAVE_ENTITY_ATTRIBUTES_STATUS_OK),
            @ApiResponse(code = 400, message = SAVE_ATTIRIBUTES_STATUS_BAD_REQUEST),
            @ApiResponse(code = 401, message = SAVE_ENTITY_ATTRIBUTES_STATUS_UNAUTHORIZED),
            @ApiResponse(code = 500, message = SAVE_ENTITY_ATTRIBUTES_STATUS_INTERNAL_SERVER_ERROR),
    })
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/{entityType}/{entityId}/{scope}", method = RequestMethod.POST)
    @ResponseBody
    public DeferredResult<ResponseEntity> saveEntityAttributesV1(
            @ApiParam(value = ENTITY_TYPE_PARAM_DESCRIPTION) @PathVariable("entityType") String entityType,
            @ApiParam(value = ENTITY_ID_PARAM_DESCRIPTION) @PathVariable("entityId") String entityIdStr,
            @ApiParam(value = ATTRIBUTES_SCOPE_DESCRIPTION, allowableValues = ATTRIBUTES_SCOPE_ALLOWED_VALUES) @PathVariable("scope") String scope,
            @ApiParam(value = ATTRIBUTES_JSON_REQUEST_DESCRIPTION) @RequestBody JsonNode request) throws ThingsboardException {
        EntityId entityId = EntityIdFactory.getByTypeAndId(entityType, entityIdStr);
        return saveAttributes(getTenantId(), entityId, scope, request);
    }

    @ApiOperation(value = "Save or update attributes (saveEntityAttributesV2)",
            notes = SAVE_ENTITY_ATTRIBUTES_DESCRIPTION + INVALID_ENTITY_ID_OR_ENTITY_TYPE_DESCRIPTION + TENANT_OR_CUSTOMER_AUTHORITY_PARAGRAPH,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = SAVE_ATTIRIBUTES_STATUS_OK + SAVE_ENTITY_ATTRIBUTES_STATUS_OK),
            @ApiResponse(code = 400, message = SAVE_ATTIRIBUTES_STATUS_BAD_REQUEST),
            @ApiResponse(code = 401, message = SAVE_ENTITY_ATTRIBUTES_STATUS_UNAUTHORIZED),
            @ApiResponse(code = 500, message = SAVE_ENTITY_ATTRIBUTES_STATUS_INTERNAL_SERVER_ERROR),
    })
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/{entityType}/{entityId}/attributes/{scope}", method = RequestMethod.POST)
    @ResponseBody
    public DeferredResult<ResponseEntity> saveEntityAttributesV2(
            @ApiParam(value = ENTITY_TYPE_PARAM_DESCRIPTION) @PathVariable("entityType") String entityType,
            @ApiParam(value = ENTITY_ID_PARAM_DESCRIPTION) @PathVariable("entityId") String entityIdStr,
            @ApiParam(value = ATTRIBUTES_SCOPE_DESCRIPTION, allowableValues = ATTRIBUTES_SCOPE_ALLOWED_VALUES) @PathVariable("scope") String scope,
            @ApiParam(value = ATTRIBUTES_JSON_REQUEST_DESCRIPTION) @RequestBody JsonNode request) throws ThingsboardException {
        EntityId entityId = EntityIdFactory.getByTypeAndId(entityType, entityIdStr);
        return saveAttributes(getTenantId(), entityId, scope, request);
    }

    @ApiOperation(value = "Save or update telemetry (saveEntityTelemetry)",
            notes = SAVE_ENTITY_TIMESERIES_DESCRIPTION + INVALID_ENTITY_ID_OR_ENTITY_TYPE_DESCRIPTION + TENANT_OR_CUSTOMER_AUTHORITY_PARAGRAPH,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = SAVE_ENTITY_TIMESERIES_STATUS_OK),
            @ApiResponse(code = 400, message = INVALID_STRUCTURE_OF_THE_REQUEST),
            @ApiResponse(code = 401, message = SAVE_ENTITY_TIMESERIES_STATUS_UNAUTHORIZED),
            @ApiResponse(code = 500, message = SAVE_ENTITY_TIMESERIES_STATUS_INTERNAL_SERVER_ERROR),
    })
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/{entityType}/{entityId}/timeseries/{scope}", method = RequestMethod.POST)
    @ResponseBody
    public DeferredResult<ResponseEntity> saveEntityTelemetry(
            @ApiParam(value = ENTITY_TYPE_PARAM_DESCRIPTION) @PathVariable("entityType") String entityType,
            @ApiParam(value = ENTITY_ID_PARAM_DESCRIPTION) @PathVariable("entityId") String entityIdStr,
            @ApiParam(value = TELEMETRY_SCOPE_DESCRIPTION) @PathVariable("scope") String scope,
            @ApiParam(value = TELEMETRY_JSON_REQUEST_DESCRIPTION) @RequestBody String requestBody) throws ThingsboardException {
        EntityId entityId = EntityIdFactory.getByTypeAndId(entityType, entityIdStr);
        return saveTelemetry(getTenantId(), entityId, requestBody, 0L);
    }

    @ApiOperation(value = "Save or update telemetry with TTL (saveEntityTelemetryWithTTL)",
            notes = SAVE_ENTITY_TIMESERIES_DESCRIPTION + "The ttl parameter used only in case of Cassandra DB use for timeseries data storage. "
                    + INVALID_ENTITY_ID_OR_ENTITY_TYPE_DESCRIPTION + TENANT_OR_CUSTOMER_AUTHORITY_PARAGRAPH,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = SAVE_ENTITY_TIMESERIES_STATUS_OK),
            @ApiResponse(code = 400, message = INVALID_STRUCTURE_OF_THE_REQUEST),
            @ApiResponse(code = 401, message = SAVE_ENTITY_TIMESERIES_STATUS_UNAUTHORIZED),
            @ApiResponse(code = 500, message = SAVE_ENTITY_TIMESERIES_STATUS_INTERNAL_SERVER_ERROR),
    })
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/{entityType}/{entityId}/timeseries/{scope}/{ttl}", method = RequestMethod.POST)
    @ResponseBody
    public DeferredResult<ResponseEntity> saveEntityTelemetryWithTTL(
            @ApiParam(value = ENTITY_TYPE_PARAM_DESCRIPTION) @PathVariable("entityType") String entityType,
            @ApiParam(value = ENTITY_ID_PARAM_DESCRIPTION) @PathVariable("entityId") String entityIdStr,
            @ApiParam(value = TELEMETRY_SCOPE_DESCRIPTION) @PathVariable("scope") String scope,
            @ApiParam(value = "A long value representing TTL (Time to Live) parameter.") @PathVariable("ttl") Long ttl,
            @ApiParam(value = TELEMETRY_JSON_REQUEST_DESCRIPTION) @RequestBody String requestBody) throws ThingsboardException {
        EntityId entityId = EntityIdFactory.getByTypeAndId(entityType, entityIdStr);
        return saveTelemetry(getTenantId(), entityId, requestBody, ttl);
    }

    @ApiOperation(value = "Delete entity timeseries (deleteEntityTimeseries)",
            notes = "Delete timeseries for selected entity based on entity id, entity type, keys " +
                    "and removal time range. To delete all data for keys parameter 'deleteAllDataForKeys' should be set to true, " +
                    "otherwise, will be deleted data that is in range of the selected time interval. " + TENANT_OR_CUSTOMER_AUTHORITY_PARAGRAPH,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Timeseries for the selected keys in the request was removed. " +
                    "Platform creates an audit log event about entity timeseries removal with action type 'TIMESERIES_DELETED'."),
            @ApiResponse(code = 400, message = "Platform returns a bad request in case if keys list is empty or start and end timestamp values is empty when deleteAllDataForKeys is set to false."),
            @ApiResponse(code = 401, message = "User is not authorized to delete entity timeseries for selected entity. Most likely, User belongs to different Customer or Tenant."),
            @ApiResponse(code = 500, message = "The exception was thrown during processing the request. " +
                    "Platform creates an audit log event about entity timeseries removal with action type 'TIMESERIES_DELETED' that includes an error stacktrace."),
    })
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/{entityType}/{entityId}/timeseries/delete", method = RequestMethod.DELETE)
    @ResponseBody
    public DeferredResult<ResponseEntity> deleteEntityTimeseries(
            @ApiParam(value = ENTITY_TYPE_PARAM_DESCRIPTION) @PathVariable("entityType") String entityType,
            @ApiParam(value = ENTITY_ID_PARAM_DESCRIPTION) @PathVariable("entityId") String entityIdStr,
            @ApiParam(value = TELEMETRY_KEYS_DESCRIPTION) @RequestParam(name = "keys") String keysStr,
            @ApiParam(value = "A boolean value to specify if should be deleted all data for selected keys or only data that are in the selected time range.")
            @RequestParam(name = "deleteAllDataForKeys", defaultValue = "false") boolean deleteAllDataForKeys,
            @ApiParam(value = "A long value representing the start timestamp of removal time range in milliseconds.")
            @RequestParam(name = "startTs", required = false) Long startTs,
            @ApiParam(value = "A long value representing the end timestamp of removal time range in milliseconds.")
            @RequestParam(name = "endTs", required = false) Long endTs,
            @ApiParam(value = "If the parameter is set to true, the latest telemetry will be rewritten in case that current latest value was removed, otherwise, in case that parameter is set to false the new latest value will not set.")
            @RequestParam(name = "rewriteLatestIfDeleted", defaultValue = "false") boolean rewriteLatestIfDeleted) throws ThingsboardException {
        EntityId entityId = EntityIdFactory.getByTypeAndId(entityType, entityIdStr);
        return deleteTimeseries(entityId, keysStr, deleteAllDataForKeys, startTs, endTs, rewriteLatestIfDeleted);
    }

    private DeferredResult<ResponseEntity> deleteTimeseries(EntityId entityIdStr, String keysStr, boolean deleteAllDataForKeys,
                                                            Long startTs, Long endTs, boolean rewriteLatestIfDeleted) throws ThingsboardException {
        List<String> keys = toKeysList(keysStr);
        if (keys.isEmpty()) {
            return getImmediateDeferredResult("Empty keys: " + keysStr, HttpStatus.BAD_REQUEST);
        }
        SecurityUser user = getCurrentUser();

        long deleteFromTs;
        long deleteToTs;
        if (deleteAllDataForKeys) {
            deleteFromTs = 0L;
            deleteToTs = System.currentTimeMillis();
        } else {
            if (startTs == null || endTs == null) {
                return getImmediateDeferredResult("When deleteAllDataForKeys is false, start and end timestamp values shouldn't be empty", HttpStatus.BAD_REQUEST);
            } else {
                deleteFromTs = startTs;
                deleteToTs = endTs;
            }
        }

        return accessValidator.validateEntityAndCallback(user, Operation.WRITE_TELEMETRY, entityIdStr, (result, tenantId, entityId) -> {
            List<DeleteTsKvQuery> deleteTsKvQueries = new ArrayList<>();
            for (String key : keys) {
                deleteTsKvQueries.add(new BaseDeleteTsKvQuery(key, deleteFromTs, deleteToTs, rewriteLatestIfDeleted));
            }
            ListenableFuture<List<Void>> future = tsService.remove(user.getTenantId(), entityId, deleteTsKvQueries);
            Futures.addCallback(future, new FutureCallback<List<Void>>() {
                @Override
                public void onSuccess(@Nullable List<Void> tmp) {
                    logTimeseriesDeleted(user, entityId, keys, deleteFromTs, deleteToTs, null);
                    result.setResult(new ResponseEntity<>(HttpStatus.OK));
                }

                @Override
                public void onFailure(Throwable t) {
                    logTimeseriesDeleted(user, entityId, keys, deleteFromTs, deleteToTs, t);
                    result.setResult(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
                }
            }, executor);
        });
    }

    @ApiOperation(value = "Delete device attributes (deleteEntityAttributes)",
            notes = "Delete device attributes from the specified attributes scope based on device id and a list of keys to delete. " +
                    "Selected keys will be deleted only if there are exist in the specified attribute scope. Referencing a non-existing device Id will cause an error" + TENANT_OR_CUSTOMER_AUTHORITY_PARAGRAPH,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Device attributes was removed for the selected keys in the request. " +
                    "Platform creates an audit log event about device attributes removal with action type 'ATTRIBUTES_DELETED'."),
            @ApiResponse(code = 400, message = "Platform returns a bad request in case if keys or scope are not specified."),
            @ApiResponse(code = 401, message = "User is not authorized to delete device attributes for selected entity. Most likely, User belongs to different Customer or Tenant."),
            @ApiResponse(code = 500, message = "The exception was thrown during processing the request. " +
                    "Platform creates an audit log event about device attributes removal with action type 'ATTRIBUTES_DELETED' that includes an error stacktrace."),
    })
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/{deviceId}/{scope}", method = RequestMethod.DELETE)
    @ResponseBody
    public DeferredResult<ResponseEntity> deleteEntityAttributes(
            @ApiParam(value = DEVICE_ID_PARAM_DESCRIPTION) @PathVariable("deviceId") String deviceIdStr,
            @ApiParam(value = ATTRIBUTES_SCOPE_DESCRIPTION, allowableValues = ATTRIBUTES_SCOPE_ALLOWED_VALUES) @PathVariable("scope") String scope,
            @ApiParam(value = ATTRIBUTES_KEYS_DESCRIPTION) @RequestParam(name = "keys") String keysStr) throws ThingsboardException {
        EntityId entityId = EntityIdFactory.getByTypeAndUuid(EntityType.DEVICE, deviceIdStr);
        return deleteAttributes(entityId, scope, keysStr);
    }

    @ApiOperation(value = "Delete entity attributes (deleteEntityAttributes)",
            notes = "Delete entity attributes from the specified attributes scope based on entity id, entity type and a list of keys to delete. " +
                    "Selected keys will be deleted only if there are exist in the specified attribute scope." + INVALID_ENTITY_ID_OR_ENTITY_TYPE_DESCRIPTION + TENANT_OR_CUSTOMER_AUTHORITY_PARAGRAPH,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Entity attributes was removed for the selected keys in the request. " +
                    "Platform creates an audit log event about entity attributes removal with action type 'ATTRIBUTES_DELETED'."),
            @ApiResponse(code = 400, message = "Platform returns a bad request in case if keys or scope are not specified."),
            @ApiResponse(code = 401, message = "User is not authorized to delete entity attributes for selected entity. Most likely, User belongs to different Customer or Tenant."),
            @ApiResponse(code = 500, message = "The exception was thrown during processing the request. " +
                    "Platform creates an audit log event about entity attributes removal with action type 'ATTRIBUTES_DELETED' that includes an error stacktrace."),
    })
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/{entityType}/{entityId}/{scope}", method = RequestMethod.DELETE)
    @ResponseBody
    public DeferredResult<ResponseEntity> deleteEntityAttributes(
            @ApiParam(value = ENTITY_TYPE_PARAM_DESCRIPTION) @PathVariable("entityType") String entityType,
            @ApiParam(value = ENTITY_ID_PARAM_DESCRIPTION) @PathVariable("entityId") String entityIdStr,
            @ApiParam(value = ATTRIBUTES_SCOPE_DESCRIPTION, allowableValues = ATTRIBUTES_SCOPE_ALLOWED_VALUES) @PathVariable("scope") String scope,
            @ApiParam(value = ATTRIBUTES_KEYS_DESCRIPTION) @RequestParam(name = "keys") String keysStr) throws ThingsboardException {
        EntityId entityId = EntityIdFactory.getByTypeAndId(entityType, entityIdStr);
        return deleteAttributes(entityId, scope, keysStr);
    }

    private DeferredResult<ResponseEntity> deleteAttributes(EntityId entityIdSrc, String scope, String keysStr) throws ThingsboardException {
        List<String> keys = toKeysList(keysStr);
        if (keys.isEmpty()) {
            return getImmediateDeferredResult("Empty keys: " + keysStr, HttpStatus.BAD_REQUEST);
        }
        SecurityUser user = getCurrentUser();

        if (DataConstants.SERVER_SCOPE.equals(scope) ||
                DataConstants.SHARED_SCOPE.equals(scope) ||
                DataConstants.CLIENT_SCOPE.equals(scope)) {
            return accessValidator.validateEntityAndCallback(getCurrentUser(), Operation.WRITE_ATTRIBUTES, entityIdSrc, (result, tenantId, entityId) -> {
                tsSubService.deleteAndNotify(tenantId, entityId, scope, keys, new FutureCallback<Void>() {
                    @Override
                    public void onSuccess(@Nullable Void tmp) {
                        logAttributesDeleted(user, entityId, scope, keys, null);
                        if (entityIdSrc.getEntityType().equals(EntityType.DEVICE)) {
                            DeviceId deviceId = new DeviceId(entityId.getId());
                            Set<AttributeKey> keysToNotify = new HashSet<>();
                            keys.forEach(key -> keysToNotify.add(new AttributeKey(scope, key)));
                            tbClusterService.pushMsgToCore(DeviceAttributesEventNotificationMsg.onDelete(
                                    user.getTenantId(), deviceId, keysToNotify), null);
                        }
                        result.setResult(new ResponseEntity<>(HttpStatus.OK));
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        logAttributesDeleted(user, entityId, scope, keys, t);
                        result.setResult(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
                    }
                });
            });
        } else {
            return getImmediateDeferredResult("Invalid attribute scope: " + scope, HttpStatus.BAD_REQUEST);
        }
    }

    private DeferredResult<ResponseEntity> saveAttributes(TenantId srcTenantId, EntityId entityIdSrc, String scope, JsonNode json) throws ThingsboardException {
        if (!DataConstants.SERVER_SCOPE.equals(scope) && !DataConstants.SHARED_SCOPE.equals(scope)) {
            return getImmediateDeferredResult("Invalid scope: " + scope, HttpStatus.BAD_REQUEST);
        }
        if (json.isObject()) {
            List<AttributeKvEntry> attributes = extractRequestAttributes(json);
            if (attributes.isEmpty()) {
                return getImmediateDeferredResult("No attributes data found in request body!", HttpStatus.BAD_REQUEST);
            }
            for (AttributeKvEntry attributeKvEntry : attributes) {
                if (attributeKvEntry.getKey().isEmpty() || attributeKvEntry.getKey().trim().length() == 0) {
                    return getImmediateDeferredResult("Key cannot be empty or contains only spaces", HttpStatus.BAD_REQUEST);
                }
            }
            SecurityUser user = getCurrentUser();
            return accessValidator.validateEntityAndCallback(getCurrentUser(), Operation.WRITE_ATTRIBUTES, entityIdSrc, (result, tenantId, entityId) -> {
                tsSubService.saveAndNotify(tenantId, entityId, scope, attributes, new FutureCallback<Void>() {
                    @Override
                    public void onSuccess(@Nullable Void tmp) {
                        logAttributesUpdated(user, entityId, scope, attributes, null);
                        result.setResult(new ResponseEntity(HttpStatus.OK));
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        logAttributesUpdated(user, entityId, scope, attributes, t);
                        AccessValidator.handleError(t, result, HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                });
            });
        } else {
            return getImmediateDeferredResult("Request is not a JSON object", HttpStatus.BAD_REQUEST);
        }
    }

    private DeferredResult<ResponseEntity> saveTelemetry(TenantId curTenantId, EntityId entityIdSrc, String requestBody, long ttl) throws ThingsboardException {
        Map<Long, List<KvEntry>> telemetryRequest;
        JsonElement telemetryJson;
        try {
            telemetryJson = new JsonParser().parse(requestBody);
        } catch (Exception e) {
            return getImmediateDeferredResult("Unable to parse timeseries payload: Invalid JSON body!", HttpStatus.BAD_REQUEST);
        }
        try {
            telemetryRequest = JsonConverter.convertToTelemetry(telemetryJson, System.currentTimeMillis());
        } catch (Exception e) {
            return getImmediateDeferredResult("Unable to parse timeseries payload. Invalid JSON body: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        List<TsKvEntry> entries = new ArrayList<>();
        for (Map.Entry<Long, List<KvEntry>> entry : telemetryRequest.entrySet()) {
            for (KvEntry kv : entry.getValue()) {
                entries.add(new BasicTsKvEntry(entry.getKey(), kv));
            }
        }
        if (entries.isEmpty()) {
            return getImmediateDeferredResult("No timeseries data found in request body!", HttpStatus.BAD_REQUEST);
        }
        SecurityUser user = getCurrentUser();
        return accessValidator.validateEntityAndCallback(getCurrentUser(), Operation.WRITE_TELEMETRY, entityIdSrc, (result, tenantId, entityId) -> {
            long tenantTtl = ttl;
            if (!TenantId.SYS_TENANT_ID.equals(tenantId) && tenantTtl == 0) {
                TenantProfile tenantProfile = tenantProfileCache.get(tenantId);
                tenantTtl = TimeUnit.DAYS.toSeconds(((DefaultTenantProfileConfiguration) tenantProfile.getProfileData().getConfiguration()).getDefaultStorageTtlDays());
            }
            tsSubService.saveAndNotify(tenantId, user.getCustomerId(), entityId, entries, tenantTtl, new FutureCallback<Void>() {
                @Override
                public void onSuccess(@Nullable Void tmp) {
                    logTelemetryUpdated(user, entityId, entries, null);
                    result.setResult(new ResponseEntity(HttpStatus.OK));
                }

                @Override
                public void onFailure(Throwable t) {
                    logTelemetryUpdated(user, entityId, entries, t);
                    AccessValidator.handleError(t, result, HttpStatus.INTERNAL_SERVER_ERROR);
                }
            });
        });
    }

    private void getLatestTimeseriesValuesCallback(@Nullable DeferredResult<ResponseEntity> result, SecurityUser user, EntityId entityId, String keys, Boolean useStrictDataTypes) {
        ListenableFuture<List<TsKvEntry>> future;
        if (StringUtils.isEmpty(keys)) {
            future = tsService.findAllLatest(user.getTenantId(), entityId);
        } else {
            future = tsService.findLatest(user.getTenantId(), entityId, toKeysList(keys));
        }
        Futures.addCallback(future, getTsKvListCallback(result, useStrictDataTypes), MoreExecutors.directExecutor());
    }

    private void getAttributeValuesCallback(@Nullable DeferredResult<ResponseEntity> result, SecurityUser user, EntityId entityId, String scope, String keys) {
        List<String> keyList = toKeysList(keys);
        FutureCallback<List<AttributeKvEntry>> callback = getAttributeValuesToResponseCallback(result, user, scope, entityId, keyList);
        if (!StringUtils.isEmpty(scope)) {
            if (keyList != null && !keyList.isEmpty()) {
                Futures.addCallback(attributesService.find(user.getTenantId(), entityId, scope, keyList), callback, MoreExecutors.directExecutor());
            } else {
                Futures.addCallback(attributesService.findAll(user.getTenantId(), entityId, scope), callback, MoreExecutors.directExecutor());
            }
        } else {
            List<ListenableFuture<List<AttributeKvEntry>>> futures = new ArrayList<>();
            for (String tmpScope : DataConstants.allScopes()) {
                if (keyList != null && !keyList.isEmpty()) {
                    futures.add(attributesService.find(user.getTenantId(), entityId, tmpScope, keyList));
                } else {
                    futures.add(attributesService.findAll(user.getTenantId(), entityId, tmpScope));
                }
            }

            ListenableFuture<List<AttributeKvEntry>> future = mergeAllAttributesFutures(futures);

            Futures.addCallback(future, callback, MoreExecutors.directExecutor());
        }
    }

    private void getAttributeKeysCallback(@Nullable DeferredResult<ResponseEntity> result, TenantId tenantId, EntityId entityId, String scope) {
        Futures.addCallback(attributesService.findAll(tenantId, entityId, scope), getAttributeKeysToResponseCallback(result), MoreExecutors.directExecutor());
    }

    private void getAttributeKeysCallback(@Nullable DeferredResult<ResponseEntity> result, TenantId tenantId, EntityId entityId) {
        List<ListenableFuture<List<AttributeKvEntry>>> futures = new ArrayList<>();
        for (String scope : DataConstants.allScopes()) {
            futures.add(attributesService.findAll(tenantId, entityId, scope));
        }

        ListenableFuture<List<AttributeKvEntry>> future = mergeAllAttributesFutures(futures);

        Futures.addCallback(future, getAttributeKeysToResponseCallback(result), MoreExecutors.directExecutor());
    }

    private FutureCallback<List<TsKvEntry>> getTsKeysToResponseCallback(final DeferredResult<ResponseEntity> response) {
        return new FutureCallback<List<TsKvEntry>>() {
            @Override
            public void onSuccess(List<TsKvEntry> values) {
                List<String> keys = values.stream().map(KvEntry::getKey).collect(Collectors.toList());
                response.setResult(new ResponseEntity<>(keys, HttpStatus.OK));
            }

            @Override
            public void onFailure(Throwable e) {
                log.error("Failed to fetch attributes", e);
                AccessValidator.handleError(e, response, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        };
    }

    private FutureCallback<List<AttributeKvEntry>> getAttributeKeysToResponseCallback(final DeferredResult<ResponseEntity> response) {
        return new FutureCallback<List<AttributeKvEntry>>() {

            @Override
            public void onSuccess(List<AttributeKvEntry> attributes) {
                List<String> keys = attributes.stream().map(KvEntry::getKey).collect(Collectors.toList());
                response.setResult(new ResponseEntity<>(keys, HttpStatus.OK));
            }

            @Override
            public void onFailure(Throwable e) {
                log.error("Failed to fetch attributes", e);
                AccessValidator.handleError(e, response, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        };
    }

    private FutureCallback<List<AttributeKvEntry>> getAttributeValuesToResponseCallback(final DeferredResult<ResponseEntity> response,
                                                                                        final SecurityUser user, final String scope,
                                                                                        final EntityId entityId, final List<String> keyList) {
        return new FutureCallback<List<AttributeKvEntry>>() {
            @Override
            public void onSuccess(List<AttributeKvEntry> attributes) {
                List<AttributeData> values = attributes.stream().map(attribute ->
                        new AttributeData(attribute.getLastUpdateTs(), attribute.getKey(), getKvValue(attribute))
                ).collect(Collectors.toList());
                logAttributesRead(user, entityId, scope, keyList, null);
                response.setResult(new ResponseEntity<>(values, HttpStatus.OK));
            }

            @Override
            public void onFailure(Throwable e) {
                log.error("Failed to fetch attributes", e);
                logAttributesRead(user, entityId, scope, keyList, e);
                AccessValidator.handleError(e, response, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        };
    }

    private FutureCallback<List<TsKvEntry>> getTsKvListCallback(final DeferredResult<ResponseEntity> response, Boolean useStrictDataTypes) {
        return new FutureCallback<List<TsKvEntry>>() {
            @Override
            public void onSuccess(List<TsKvEntry> data) {
                Map<String, List<TsData>> result = new LinkedHashMap<>();
                for (TsKvEntry entry : data) {
                    Object value = useStrictDataTypes ? getKvValue(entry) : entry.getValueAsString();
                    result.computeIfAbsent(entry.getKey(), k -> new ArrayList<>()).add(new TsData(entry.getTs(), value));
                }
                response.setResult(new ResponseEntity<>(result, HttpStatus.OK));
            }

            @Override
            public void onFailure(Throwable e) {
                log.error("Failed to fetch historical data", e);
                AccessValidator.handleError(e, response, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        };
    }

    private void logTimeseriesDeleted(SecurityUser user, EntityId entityId, List<String> keys, long startTs, long endTs, Throwable e) {
        try {
            logEntityAction(user, (UUIDBased & EntityId) entityId, null, null, ActionType.TIMESERIES_DELETED, toException(e),
                    keys, startTs, endTs);
        } catch (ThingsboardException te) {
            log.warn("Failed to log timeseries delete", te);
        }
    }

    private void logTelemetryUpdated(SecurityUser user, EntityId entityId, List<TsKvEntry> telemetry, Throwable e) {
        try {
            logEntityAction(user, (UUIDBased & EntityId) entityId, null, null, ActionType.TIMESERIES_UPDATED, toException(e), telemetry);
        } catch (ThingsboardException te) {
            log.warn("Failed to log telemetry update");
        }
    }

    private void logAttributesDeleted(SecurityUser user, EntityId entityId, String scope, List<String> keys, Throwable e) {
        try {
            logEntityAction(user, (UUIDBased & EntityId) entityId, null, null, ActionType.ATTRIBUTES_DELETED, toException(e),
                    scope, keys);
        } catch (ThingsboardException te) {
            log.warn("Failed to log attributes delete", te);
        }
    }

    private void logAttributesUpdated(SecurityUser user, EntityId entityId, String scope, List<AttributeKvEntry> attributes, Throwable e) {
        try {
            logEntityAction(user, (UUIDBased & EntityId) entityId, null, null, ActionType.ATTRIBUTES_UPDATED, toException(e),
                    scope, attributes);
        } catch (ThingsboardException te) {
            log.warn("Failed to log attributes update", te);
        }
    }


    private void logAttributesRead(SecurityUser user, EntityId entityId, String scope, List<String> keys, Throwable e) {
        try {
            logEntityAction(user, (UUIDBased & EntityId) entityId, null, null, ActionType.ATTRIBUTES_READ, toException(e),
                    scope, keys);
        } catch (ThingsboardException te) {
            log.warn("Failed to log attributes read", te);
        }
    }

    private ListenableFuture<List<AttributeKvEntry>> mergeAllAttributesFutures(List<ListenableFuture<List<AttributeKvEntry>>> futures) {
        return Futures.transform(Futures.successfulAsList(futures),
                (Function<? super List<List<AttributeKvEntry>>, ? extends List<AttributeKvEntry>>) input -> {
                    List<AttributeKvEntry> tmp = new ArrayList<>();
                    if (input != null) {
                        input.forEach(tmp::addAll);
                    }
                    return tmp;
                }, executor);
    }

    private List<String> toKeysList(String keys) {
        List<String> keyList = null;
        if (!StringUtils.isEmpty(keys)) {
            keyList = Arrays.asList(keys.split(","));
        }
        return keyList;
    }

    private DeferredResult<ResponseEntity> getImmediateDeferredResult(String message, HttpStatus status) {
        DeferredResult<ResponseEntity> result = new DeferredResult<>();
        result.setResult(new ResponseEntity<>(message, status));
        return result;
    }

    private List<AttributeKvEntry> extractRequestAttributes(JsonNode jsonNode) {
        long ts = System.currentTimeMillis();
        List<AttributeKvEntry> attributes = new ArrayList<>();
        jsonNode.fields().forEachRemaining(entry -> {
            String key = entry.getKey();
            JsonNode value = entry.getValue();
            if (entry.getValue().isObject() || entry.getValue().isArray()) {
                attributes.add(new BaseAttributeKvEntry(new JsonDataEntry(key, toJsonStr(value)), ts));
            } else if (entry.getValue().isTextual()) {
                if (maxStringValueLength > 0 && entry.getValue().textValue().length() > maxStringValueLength) {
                    String message = String.format("String value length [%d] for key [%s] is greater than maximum allowed [%d]", entry.getValue().textValue().length(), key, maxStringValueLength);
                    throw new UncheckedApiException(new InvalidParametersException(message));
                }
                attributes.add(new BaseAttributeKvEntry(new StringDataEntry(key, value.textValue()), ts));
            } else if (entry.getValue().isBoolean()) {
                attributes.add(new BaseAttributeKvEntry(new BooleanDataEntry(key, value.booleanValue()), ts));
            } else if (entry.getValue().isDouble()) {
                attributes.add(new BaseAttributeKvEntry(new DoubleDataEntry(key, value.doubleValue()), ts));
            } else if (entry.getValue().isNumber()) {
                if (entry.getValue().isBigInteger()) {
                    throw new UncheckedApiException(new InvalidParametersException("Big integer values are not supported!"));
                } else {
                    attributes.add(new BaseAttributeKvEntry(new LongDataEntry(key, value.longValue()), ts));
                }
            }
        });
        return attributes;
    }

    private String toJsonStr(JsonNode value) {
        try {
            return mapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new JsonParseException("Can't parse jsonValue: " + value, e);
        }
    }

    private JsonNode toJsonNode(String value) {
        try {
            return mapper.readTree(value);
        } catch (IOException e) {
            throw new JsonParseException("Can't parse jsonValue: " + value, e);
        }
    }

    private Object getKvValue(KvEntry entry) {
        if (entry.getDataType() == DataType.JSON) {
            return toJsonNode(entry.getJsonValue().get());
        }
        return entry.getValue();
    }
}
