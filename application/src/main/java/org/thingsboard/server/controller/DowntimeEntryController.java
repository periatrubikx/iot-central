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
import org.thingsboard.server.common.data.downtime_code.DowntimeCode;
import org.thingsboard.server.common.data.downtime_entry.DowntimeEntry;
import org.thingsboard.server.common.data.downtime_entry.DowntimeEntryInfo;
import org.thingsboard.server.common.data.edge.EdgeEventActionType;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.DowntimeCodeId;
import org.thingsboard.server.common.data.id.DowntimeEntryId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.shift.Shift;
import org.thingsboard.server.common.data.shift.ShiftInfo;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.security.model.SecurityUser;
import org.thingsboard.server.service.security.permission.Operation;
import org.thingsboard.server.service.security.permission.Resource;

import static org.thingsboard.server.controller.ControllerConstants.*;
import static org.thingsboard.server.controller.ControllerConstants.SORT_ORDER_ALLOWABLE_VALUES;
import static org.thingsboard.server.dao.service.Validator.validateId;

@RestController
@TbCoreComponent
@RequestMapping("/api")
@Slf4j
public class DowntimeEntryController extends BaseController {
    public static final String DOWNTIME_ENTRY_ID = "downtimeEntryId";

    @ApiOperation(value = "Get DowntimeCode Info (getDowntimeEntryInfoById)",
            notes = "Fetch the DowntimeCode Info object based on the provided DowntimeCode Id. " +
                    "If the user has the authority of 'Tenant Administrator', the server checks that the asset is owned by the same tenant. " +
                    "If the user has the authority of 'Customer User', the server checks that the asset is assigned to the same customer. "
                    + TENANT_OR_CUSTOMER_AUTHORITY_PARAGRAPH, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/downtimeEntry/info/{downtimeEntryId}", method = RequestMethod.GET)
    @ResponseBody
    public DowntimeEntry getDowntimeEntryInfoById(@ApiParam(value = DOWNTIME_ENTRY_ID_PARAM_DESCRIPTION)
                                                @PathVariable(DOWNTIME_ENTRY_ID) String strDowntimeEntryId) throws ThingsboardException {
        checkParameter(DOWNTIME_ENTRY_ID, strDowntimeEntryId);
        try {
            DowntimeEntryId downtimeEntryId = new DowntimeEntryId(toUUID(strDowntimeEntryId));
            return checkDowntimeEntryId(downtimeEntryId, Operation.READ);
        } catch (Exception e) {
            throw handleException(e);
        }
    }


    @ApiOperation(value = "Get Downtime Entry Info (getDowntimeEntries)",
            notes = "Returns a page of Downtime Entries. " +
                    PAGE_DATA_PARAMETERS)
    @RequestMapping(value = "/downtimeEntry/downtimeEntryInfos", params = {"pageSize", "page"}, method = RequestMethod.GET)
    @ResponseBody
    public PageData<DowntimeEntryInfo> getDowntimeEntries(
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
            return checkNotNull(downtimeEntryService.findDowntimeEntryInfosByTenantId(tenantId, pageLink));
        } catch (Exception e) {
            throw handleException(e);
        }
    }


    @ApiOperation(value = "Delete DowntimeEntry (deleteDowntimeEntry)",
            notes = "Deletes the DowntimeCode. Referencing non-existing DowntimeCode Id will cause an error." + TENANT_OR_CUSTOMER_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/downtimeEntry/{downtimeEntryId}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    public void deleteDowntimeEntry(@ApiParam(value = DOWNTIME_ENTRY_ID_PARAM_DESCRIPTION) @PathVariable(DOWNTIME_ENTRY_ID) String strDowntimeEntryId) throws ThingsboardException {
        checkParameter(DOWNTIME_ENTRY_ID, strDowntimeEntryId);
        try {
            DowntimeEntryId downtimeEntryId = new DowntimeEntryId(toUUID(strDowntimeEntryId));
            DowntimeEntry downtimeEntry = checkDowntimeEntryId(downtimeEntryId, Operation.DELETE);

            downtimeEntryService.deleteDowntimeEntry(getTenantId(), downtimeEntryId);

            logEntityAction(downtimeEntryId, downtimeEntry,
                    downtimeEntry.getCustomerId(),
                    ActionType.DELETED, null, strDowntimeEntryId);

        } catch (Exception e) {
            logEntityAction(emptyId(EntityType.DOWNTIME_ENTRY),
                    null,
                    null,
                    ActionType.DELETED, e, strDowntimeEntryId);
            throw handleException(e);
        }
    }


    @ApiOperation(value = "Create Or Update Downtime Entry (saveDowntimeEntry)",
            notes = "Creates or Updates the Downtime Entry. When creating downtime entry, platform generates Downtime Entry Id as [time-based UUID](https://en.wikipedia.org/wiki/Universally_unique_identifier#Version_1_(date-time_and_MAC_address) " +
                    "The newly created Downtime Entry id will be present in the response. " +
                    "Specify existing Downtime Entry id to update the shift. " +
                    "Referencing non-existing Shift Id will cause 'Not Found' error." + TENANT_OR_CUSTOMER_AUTHORITY_PARAGRAPH, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/downtimeEntry", method = RequestMethod.POST)
    @ResponseBody
    public DowntimeEntry saveDowntimeEntry(@ApiParam(value = "A JSON value representing the asset.") @RequestBody DowntimeEntry downtimeEntry) throws ThingsboardException {
        try {
            downtimeEntry.setTenantId(getCurrentUser().getTenantId());
            checkEntity(downtimeEntry.getId(), downtimeEntry, Resource.DOWNTIME_ENTRY);
            DowntimeEntry savedDowntimeEntry = checkNotNull(downtimeEntryService.saveDowntimeEntry(downtimeEntry));
            onDowntimeEntryCreatedOrUpdated(savedDowntimeEntry, downtimeEntry.getId() != null, getCurrentUser());
            return savedDowntimeEntry;
        } catch (Exception e) {
            logEntityAction(emptyId(EntityType.SHIFT), downtimeEntry,
                    null, downtimeEntry.getId() == null ? ActionType.ADDED : ActionType.UPDATED, e);
            throw handleException(e);
        }
    }

    private void onDowntimeEntryCreatedOrUpdated(DowntimeEntry downtimeEntry, boolean updated, SecurityUser user) {
        try {
            logEntityAction(user, downtimeEntry.getId(), downtimeEntry,
                    downtimeEntry.getCustomerId(),
                    updated ? ActionType.UPDATED : ActionType.ADDED, null);
        } catch (ThingsboardException e) {
            log.error("Failed to log entity action", e);
        }

        if (updated) {
            sendEntityNotificationMsg(downtimeEntry.getTenantId(), downtimeEntry.getId(), EdgeEventActionType.UPDATED);
        }
    }

    DowntimeEntry checkDowntimeEntryId(DowntimeEntryId downtimeEntryId, Operation operation) throws ThingsboardException {
        try {
            validateId(downtimeEntryId, "Incorrect downtimeEntryId " + downtimeEntryId);
            DowntimeEntry downtimeEntry = downtimeEntryService.findDowntimeEntryById(getCurrentUser().getTenantId(), downtimeEntryId);
            checkNotNull(downtimeEntry);
            accessControlService.checkPermission(getCurrentUser(), Resource.DOWNTIME_CODE, operation, downtimeEntryId, downtimeEntry);
            return downtimeEntry;
        } catch (Exception e) {
            throw handleException(e);
        }
    }

}
