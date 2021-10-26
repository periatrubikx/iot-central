package org.thingsboard.server.common.data;

import io.swagger.annotations.ApiModelProperty;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.validation.NoXss;

import java.util.Date;

public class Shift extends BaseData implements HasName, HasTenantId, HasCustomerId{

    private TenantId tenantId;
    private CustomerId customerId;
    @NoXss
    private String name;
    @NoXss
    private String areaName;
    @NoXss
    private Date startTime;
    @NoXss
    private Date endTime;


    @ApiModelProperty(position = 2, value = "Timestamp of the device creation, in milliseconds", example = "1609459200000", readOnly = true)
    @Override
    public long getCreatedTime() {
        return super.getCreatedTime();
    }

    @ApiModelProperty(position = 3, value = "JSON object with Tenant Id. Use 'assignDeviceToTenant' to change the Tenant Id.", readOnly = true)
    public TenantId getTenantId() {
        return tenantId;
    }

    public void setTenantId(TenantId tenantId) {
        this.tenantId = tenantId;
    }

    @ApiModelProperty(position = 4, value = "JSON object with Customer Id. Use 'assignDeviceToCustomer' to change the Customer Id.", readOnly = true)
    public CustomerId getCustomerId() {
        return customerId;
    }

    public void setCustomerId(CustomerId customerId) {
        this.customerId = customerId;
    }

    @ApiModelProperty(position = 5, required = true, value = "Unique Device Name in scope of Tenant", example = "A4B72CCDFF33")
    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ApiModelProperty(position = 6, required = true, value = "Unique Area name", example = "Packaging")
    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    @ApiModelProperty(position = 7, required = true, value = "Start time for the shift", example = "Date time")
    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    @ApiModelProperty(position = 8, required = true, value = "End time", example = "Date time")
    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}
