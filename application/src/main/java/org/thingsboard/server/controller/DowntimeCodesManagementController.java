package org.thingsboard.server.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.audit.ActionType;
import org.thingsboard.server.common.data.downtimecode.DowntimeCode;
import org.thingsboard.server.common.data.downtimecode.DowntimeCodeType;
import org.thingsboard.server.common.data.edge.EdgeEventActionType;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.DowntimeCodeId;
import org.thingsboard.server.common.data.id.ShiftId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.shift.Shift;
import org.thingsboard.server.common.data.shift.ShiftArea;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.security.model.SecurityUser;
import org.thingsboard.server.service.security.permission.Operation;
import org.thingsboard.server.service.security.permission.Resource;

import java.util.*;

import static org.thingsboard.server.controller.ControllerConstants.*;
import static org.thingsboard.server.controller.ControllerConstants.SORT_ORDER_ALLOWABLE_VALUES;
import static org.thingsboard.server.dao.service.Validator.validateId;

@RestController
@TbCoreComponent
@RequestMapping("/api")
@Slf4j
public class DowntimeCodesManagementController extends BaseController {

    public static final String DOWNTIME_CODE_ID = "downtimeCodeId";

    @ApiOperation(value = "Get DowntimeCode Info (getDowntimeCodeInfoById)",
            notes = "Fetch the DowntimeCode Info object based on the provided DowntimeCode Id. " +
                    "If the user has the authority of 'Tenant Administrator', the server checks that the asset is owned by the same tenant. " +
                    "If the user has the authority of 'Customer User', the server checks that the asset is assigned to the same customer. "
                    + TENANT_OR_CUSTOMER_AUTHORITY_PARAGRAPH, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/downtimeCodesConfig/info/{downtimeCodeId}", method = RequestMethod.GET)
    @ResponseBody
    public DowntimeCode getDowntimeCodeInfoById(@ApiParam(value = ASSET_ID_PARAM_DESCRIPTION)
                                  @PathVariable(DOWNTIME_CODE_ID) String strDowntimeCodeId) throws ThingsboardException {
        checkParameter(DOWNTIME_CODE_ID, strDowntimeCodeId);
        try {
            DowntimeCodeId downtimeCodeId = new DowntimeCodeId(toUUID(strDowntimeCodeId));
            return checkDowntimeCodeId(downtimeCodeId, Operation.READ);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @ApiOperation(value = "Get Types (getTypes)",
            notes = "Returns a list of types.", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/downtimeCodesConfig/type", method = RequestMethod.GET)
    @ResponseBody
    public List<DowntimeCodeType> getTypes() throws ThingsboardException {
        try {
            SecurityUser user = getCurrentUser();
            TenantId tenantId = user.getTenantId();
            DowntimeCodeType downtimeCodeType = new DowntimeCodeType();
            downtimeCodeType.setTenantId(tenantId);
            downtimeCodeType.setCreatedTime(new Date().getTime());
            downtimeCodeType.setName("All");
            return Arrays.asList(downtimeCodeType);
        } catch (Exception e) {
            throw handleException(e);
        }
    }


    @ApiOperation(value = "Get Downtime Codes (getDowntimeCodes)",
            notes = "Returns a page of shifts. " +
                    PAGE_DATA_PARAMETERS)
    @RequestMapping(value = "/downtimeCodesConfig/downtimeCodesConfigInfos", params = {"pageSize", "page"}, method = RequestMethod.GET)
    @ResponseBody
    public PageData<DowntimeCode> getDowntimeCodes(
            @ApiParam(value = PAGE_SIZE_DESCRIPTION, required = true)
            @RequestParam int pageSize,
            @ApiParam(value = PAGE_NUMBER_DESCRIPTION, required = true)
            @RequestParam int page,
            @ApiParam(value = CUSTOMER_TEXT_SEARCH_DESCRIPTION)
            @RequestParam(required = false) String textSearch,
            @ApiParam(value = SORT_PROPERTY_DESCRIPTION, allowableValues = CUSTOMER_SORT_PROPERTY_ALLOWABLE_VALUES)
            @RequestParam(required = false) String sortProperty,
            @ApiParam(value = SORT_ORDER_DESCRIPTION, allowableValues = SORT_ORDER_ALLOWABLE_VALUES)
            @RequestParam(required = false) String sortOrder) throws ThingsboardException {
        try {
            PageLink pageLink = createPageLink(pageSize, page, textSearch, sortProperty, sortOrder);
            TenantId tenantId = getCurrentUser().getTenantId();
            return checkNotNull(downtimeCodesService.findDowntimeCodesByTenantId(tenantId, pageLink));
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @ApiOperation(value = "Create Or Update DowntimeCode (saveDowntimeCode)",
            notes = "Creates or Updates the DowntimeCode. When creating DowntimeCode, platform generates DowntimeCode Id as [time-based UUID](https://en.wikipedia.org/wiki/Universally_unique_identifier#Version_1_(date-time_and_MAC_address) " +
                    "The newly created DowntimeCode id will be present in the response. " +
                    "Specify existing DowntimeCode id to update the DowntimeCode. " +
                    "Referencing non-existing DowntimeCode Id will cause 'Not Found' error." + TENANT_OR_CUSTOMER_AUTHORITY_PARAGRAPH, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/downtimeCodesConfig", method = RequestMethod.POST)
    @ResponseBody
    public DowntimeCode saveDowntimeCode(@ApiParam(value = "A JSON value representing the asset.") @RequestBody DowntimeCode downtimeCode) throws ThingsboardException {
        try {
            downtimeCode.setTenantId(getCurrentUser().getTenantId());
            //TODO: get it from the object. Wasn't getting passed by the UI
            downtimeCode.getCreatedTime();
            checkEntity(downtimeCode.getId(), downtimeCode, Resource.DOWNTIME_CODE);

            DowntimeCode savedDowntimeCode = checkNotNull(downtimeCodesService.saveDowntimeCode(downtimeCode));

            onDowntimeCodeCreatedOrUpdated(savedDowntimeCode, downtimeCode.getId() != null, getCurrentUser());

            return savedDowntimeCode;
        } catch (Exception e) {
            logEntityAction(emptyId(EntityType.DOWNTIME_CODE), downtimeCode,
                    null, downtimeCode.getId() == null ? ActionType.ADDED : ActionType.UPDATED, e);
            throw handleException(e);
        }
    }

    private void onDowntimeCodeCreatedOrUpdated(DowntimeCode downtimeCode, boolean updated, SecurityUser user) {
        try {
            logEntityAction(user, downtimeCode.getId(), downtimeCode,
                    downtimeCode.getCustomerId(),
                    updated ? ActionType.UPDATED : ActionType.ADDED, null);
        } catch (ThingsboardException e) {
            log.error("Failed to log entity action", e);
        }

        if (updated) {
            sendEntityNotificationMsg(downtimeCode.getTenantId(), downtimeCode.getId(), EdgeEventActionType.UPDATED);
        }
    }

    @ApiOperation(value = "Delete DowntimeCode (deleteDowntimeCode)",
            notes = "Deletes the DowntimeCode. Referencing non-existing DowntimeCode Id will cause an error." + TENANT_OR_CUSTOMER_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/downtimeCodesConfig/{downtimeCodeId}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    public void deleteDowntimeCode(@ApiParam(value = ASSET_ID_PARAM_DESCRIPTION) @PathVariable(DOWNTIME_CODE_ID) String strDowntimeCodeId) throws ThingsboardException {
        checkParameter(DOWNTIME_CODE_ID, strDowntimeCodeId);
        try {
            DowntimeCodeId downtimeCodeId = new DowntimeCodeId(toUUID(strDowntimeCodeId));
            DowntimeCode downtimeCode = checkDowntimeCodeId(downtimeCodeId, Operation.DELETE);

            downtimeCodesService.deleteDowntimeCode(getTenantId(), downtimeCodeId);

            logEntityAction(downtimeCodeId, downtimeCode,
                    downtimeCode.getCustomerId(),
                    ActionType.DELETED, null, strDowntimeCodeId);

        } catch (Exception e) {
            logEntityAction(emptyId(EntityType.ASSET),
                    null,
                    null,
                    ActionType.DELETED, e, strDowntimeCodeId);
            throw handleException(e);
        }
    }

    DowntimeCode checkDowntimeCodeId(DowntimeCodeId downtimeCodeId, Operation operation) throws ThingsboardException {
        try {
            validateId(downtimeCodeId, "Incorrect downtimeCodeId " + downtimeCodeId);
            DowntimeCode downtimeCode = downtimeCodesService.findDowntimeCodeById(getCurrentUser().getTenantId(), downtimeCodeId);
            checkNotNull(downtimeCode);
            accessControlService.checkPermission(getCurrentUser(), Resource.DOWNTIME_CODE, operation, downtimeCodeId, downtimeCode);
            return downtimeCode;
        } catch (Exception e) {
            throw handleException(e);
        }
    }
}
