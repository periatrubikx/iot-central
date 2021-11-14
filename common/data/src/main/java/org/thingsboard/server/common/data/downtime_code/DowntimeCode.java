package org.thingsboard.server.common.data.downtime_code;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import org.thingsboard.server.common.data.BaseData;
import org.thingsboard.server.common.data.HasCustomerId;
import org.thingsboard.server.common.data.HasName;
import org.thingsboard.server.common.data.HasTenantId;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.DowntimeCodeId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.validation.NoXss;

@ApiModel
@EqualsAndHashCode(callSuper = true)
public class DowntimeCode extends BaseData<DowntimeCodeId> implements HasName, HasTenantId, HasCustomerId {

    private TenantId tenantId;
    private CustomerId customerId;
    @NoXss
    private String level1;
    @NoXss
    private String level2;
    @NoXss
    private String level3;
    @NoXss
    private Integer code;

    public DowntimeCode(){
        super();
    }

    public DowntimeCode(DowntimeCodeId id){
        super(id);
    }

    public DowntimeCode(DowntimeCode downtimeCode){
        super(downtimeCode);
        this.level1 = downtimeCode.getLevel1();
    }

    @ApiModelProperty(position = 1, value = "JSON object with the asset Id. " +
            "Specify this field to update the asset. " +
            "Referencing non-existing asset Id will cause error. " +
            "Omit this field to create new asset.")
    @Override
    public DowntimeCodeId getId() {
        return super.getId();
    }

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

    @ApiModelProperty(position = 5, required = true, value = "Level 1", example = "Planned Downtime")
    public String getLevel1() {
        return level1;
    }

    public void setLevel1(String level1) {
        this.level1 = level1;
    }

    @ApiModelProperty(position = 6, required = true, value = "Level 2", example = "Generic Stop")
    public String getLevel2() {
        return level2;
    }

    public void setLevel2(String level2) {
        this.level2 = level2;
    }

    @ApiModelProperty(position = 7, required = true, value = "Level 3", example = "Lunch Break")
    public String getLevel3() {
        return level3;
    }

    public void setLevel3(String level3) {
        this.level3 = level3;
    }

    @ApiModelProperty(position = 8, required = true, value = "Code", example = "1001")
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("DowntimeCode [downtimeCodeId=");
        builder.append(tenantId);
        builder.append(", customerId=");
        builder.append(customerId);
        builder.append(", level1=");
        builder.append(level1);
        builder.append(", level2=");
        builder.append(level2);
        builder.append(", level3=");
        builder.append(level3);
        builder.append(", code=");
        builder.append(code);
        builder.append(", createdTime=");
        builder.append(createdTime);
        builder.append(", id=");
        builder.append(id);
        builder.append("]");
        return builder.toString();
    }

    @Override
    public String getName() {
        return code.toString();
    }
}
