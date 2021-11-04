package org.thingsboard.server.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.downtimecode.DowntimeCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.DowntimeCodeId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.queue.util.TbCoreComponent;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import static org.thingsboard.server.controller.ControllerConstants.*;
import static org.thingsboard.server.controller.ControllerConstants.SORT_ORDER_ALLOWABLE_VALUES;

@RestController
@TbCoreComponent
@RequestMapping("/api")
@Slf4j
public class DowntimeCodesManagementController extends BaseController {

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

            ArrayList<DowntimeCode> downtimeCodes = new ArrayList<>();
            DowntimeCode downtimeCode = new DowntimeCode();
            downtimeCode.setCode(1001);
            downtimeCode.setCreatedTime(new Date().getTime());
            downtimeCode.setLevel1("Planned Time");
            downtimeCode.setLevel2("Generic");
            downtimeCode.setLevel3("Lunch Time");
            downtimeCode.setTenantId(tenantId);
            downtimeCode.setId(new DowntimeCodeId(UUID.randomUUID()));
            downtimeCodes.add(downtimeCode);
            PageData<DowntimeCode> downtimeCodesInfoPageData = new PageData<>(downtimeCodes, 1, 1, false);
            return downtimeCodesInfoPageData;


//            return checkNotNull(shiftService.findShiftsByTenantId(tenantId, pageLink));
        } catch (Exception e) {
            throw handleException(e);
        }
    }

}
