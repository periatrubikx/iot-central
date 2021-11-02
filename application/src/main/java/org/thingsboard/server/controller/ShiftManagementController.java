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

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.asset.Asset;
import org.thingsboard.server.common.data.asset.AssetInfo;
import org.thingsboard.server.common.data.id.AssetId;
import org.thingsboard.server.common.data.id.EdgeId;
import org.thingsboard.server.common.data.id.ShiftId;
import org.thingsboard.server.common.data.shift.Shift;
import org.thingsboard.server.common.data.audit.ActionType;
import org.thingsboard.server.common.data.edge.EdgeEventActionType;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.shift.ShiftInfo;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.security.model.SecurityUser;
import org.thingsboard.server.service.security.permission.Operation;
import org.thingsboard.server.service.security.permission.Resource;

import java.util.List;

import static org.thingsboard.server.controller.ControllerConstants.*;
import static org.thingsboard.server.dao.service.Validator.validateId;

@RestController
@TbCoreComponent
@RequestMapping("/api")
@Slf4j
public class ShiftManagementController extends BaseController {

    public static final String SHIFT_ID = "shiftId";

    @ApiOperation(value = "Get Shift Info (getShiftInfoById)",
            notes = "Fetch the Shift Info object based on the provided Shift Id. " +
                    "If the user has the authority of 'Tenant Administrator', the server checks that the asset is owned by the same tenant. " +
                    "If the user has the authority of 'Customer User', the server checks that the asset is assigned to the same customer. "
                    + TENANT_OR_CUSTOMER_AUTHORITY_PARAGRAPH, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/shift/info/{shiftId}", method = RequestMethod.GET)
    @ResponseBody
    public Shift getShiftInfoById(@ApiParam(value = ASSET_ID_PARAM_DESCRIPTION)
                                      @PathVariable(SHIFT_ID) String strAssetId) throws ThingsboardException {
        checkParameter(SHIFT_ID, strAssetId);
        try {
            ShiftId shiftId = new ShiftId(toUUID(strAssetId));
            return checkShiftInfoId(shiftId, Operation.READ);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    Shift checkShiftInfoId(ShiftId shiftId, Operation operation) throws ThingsboardException {
        try {
//            validateId(assetId, "Incorrect assetId " + assetId);
            Shift shift = shiftService.findAssetInfoById(getCurrentUser().getTenantId(), shiftId);
            checkNotNull(shift);
//            accessControlService.checkPermission(getCurrentUser(), Resource.ASSET, operation, assetId, asset);
            return shift;
        } catch (Exception e) {
//            throw handleException(e, false);
            throw handleException(e);
        }
    }

    @ApiOperation(value = "Get Shifts (getShifts)",
            notes = "Returns a page of shifts. " +
                    PAGE_DATA_PARAMETERS)
    @RequestMapping(value = "/shift/shiftInfos", params = {"pageSize", "page"}, method = RequestMethod.GET)
    @ResponseBody
    public PageData<Shift> getShifts(
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
            return checkNotNull(shiftService.findShiftsByTenantId(tenantId, pageLink));
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @ApiOperation(value = "Create Or Update Shift (saveShift)",
            notes = "Creates or Updates the Shift. When creating shift, platform generates Shift Id as [time-based UUID](https://en.wikipedia.org/wiki/Universally_unique_identifier#Version_1_(date-time_and_MAC_address) " +
                    "The newly created Shift id will be present in the response. " +
                    "Specify existing Shift id to update the shift. " +
                    "Referencing non-existing Shift Id will cause 'Not Found' error." + TENANT_OR_CUSTOMER_AUTHORITY_PARAGRAPH, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/shift", method = RequestMethod.POST)
    @ResponseBody
    public Shift saveShift(@ApiParam(value = "A JSON value representing the asset.") @RequestBody Shift shift) throws ThingsboardException {
        try {
            shift.setTenantId(getCurrentUser().getTenantId());

            checkEntity(shift.getId(), shift, Resource.SHIFT);

            Shift savedShiftAsset = checkNotNull(shiftService.saveShift(shift));

            onAssetCreatedOrUpdated(savedShiftAsset, shift.getId() != null, getCurrentUser());

            return savedShiftAsset;
        } catch (Exception e) {
            logEntityAction(emptyId(EntityType.SHIFT), shift,
                    null, shift.getId() == null ? ActionType.ADDED : ActionType.UPDATED, e);
            throw handleException(e);
        }
    }

    private void onAssetCreatedOrUpdated(Shift shift, boolean updated, SecurityUser user) {
        try {
            logEntityAction(user, shift.getId(), shift,
                    shift.getCustomerId(),
                    updated ? ActionType.UPDATED : ActionType.ADDED, null);
        } catch (ThingsboardException e) {
            log.error("Failed to log entity action", e);
        }

        if (updated) {
            sendEntityNotificationMsg(shift.getTenantId(), shift.getId(), EdgeEventActionType.UPDATED);
        }
    }

    @ApiOperation(value = "Delete shift (deleteShift)",
            notes = "Deletes the shift. Referencing non-existing shift Id will cause an error." + TENANT_OR_CUSTOMER_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/shift/{shiftId}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    public void deleteShift(@ApiParam(value = ASSET_ID_PARAM_DESCRIPTION) @PathVariable(SHIFT_ID) String strShiftId) throws ThingsboardException {
        checkParameter(SHIFT_ID, strShiftId);
        try {
            ShiftId shiftId = new ShiftId(toUUID(strShiftId));
//            Shift shift = checkShiftId(shiftId, Operation.DELETE);

            shiftService.deleteShift(getTenantId(), shiftId);

//            logEntityAction(shiftId, shift,
//                    shift.getCustomerId(),
//                    ActionType.DELETED, null, strShiftId);

        } catch (Exception e) {
            logEntityAction(emptyId(EntityType.ASSET),
                    null,
                    null,
                    ActionType.DELETED, e, strShiftId);
            throw handleException(e);
        }
    }

//    Asset checkShiftId(ShiftId shiftId, Operation operation) throws ThingsboardException {
//        try {
//            validateId(shiftId, "Incorrect shiftId " + shiftId);
//            Asset asset = shiftService.findAssetById(getCurrentUser().getTenantId(), shiftId);
//            checkNotNull(asset);
//            accessControlService.checkPermission(getCurrentUser(), Resource.ASSET, operation, shiftId, asset);
//            return asset;
//        } catch (Exception e) {
//            throw handleException(e, false);
//        }
//    }
}
