package org.thingsboard.server.common.data.downtime_code;

import io.swagger.annotations.ApiModelProperty;
import org.thingsboard.server.common.data.BaseData;
import org.thingsboard.server.common.data.HasCustomerId;
import org.thingsboard.server.common.data.HasName;
import org.thingsboard.server.common.data.HasTenantId;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.validation.NoXss;

public class DowntimeCodeType extends BaseData implements HasName, HasTenantId, HasCustomerId {
    private TenantId tenantId;
    private CustomerId customerId;
    @NoXss
    private String name;

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
}
