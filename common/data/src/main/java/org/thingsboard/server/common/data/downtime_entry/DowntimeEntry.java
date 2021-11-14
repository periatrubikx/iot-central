package org.thingsboard.server.common.data.downtime_entry;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import org.thingsboard.server.common.data.BaseData;
import org.thingsboard.server.common.data.HasCustomerId;
import org.thingsboard.server.common.data.HasName;
import org.thingsboard.server.common.data.HasTenantId;
import org.thingsboard.server.common.data.id.*;
import org.thingsboard.server.common.data.validation.NoXss;

@ApiModel
@EqualsAndHashCode(callSuper = true)
public class DowntimeEntry extends BaseData<DowntimeEntryId> implements HasName, HasTenantId, HasCustomerId {
    private TenantId tenantId;
    private CustomerId customerId;
    @NoXss
    private String name;
    @NoXss
    private DeviceId deviceId;
    @NoXss
    private AssetId assetId;
    @NoXss
    private Long startDateTimeMs;
    @NoXss
    private Long endDateTimeMs;
    @NoXss
    private String reason;

    public DowntimeEntry() {
        super();
    }

    public DowntimeEntry(DowntimeEntryId id){
        super(id);
    }

    public DowntimeEntry(DowntimeEntry downtimeEntry){
        super(downtimeEntry);
        this.tenantId = downtimeEntry.getTenantId();
        this.customerId = downtimeEntry.getCustomerId();
        this.name = downtimeEntry.getName();
        this.deviceId = downtimeEntry.getDeviceId();
        this.reason = downtimeEntry.getReason();
        this.startDateTimeMs = downtimeEntry.startDateTimeMs;
        this.endDateTimeMs = downtimeEntry.endDateTimeMs;
    }


    @ApiModelProperty(position = 1, value = "JSON object with the downtime entry Id. " +
            "Specify this field to update the downtime entry. " +
            "Referencing non-existing asset Id will cause error. " +
            "Omit this field to create new downtime entry.")
    @Override
    public DowntimeEntryId getId() {
        return super.getId();
    }

    @ApiModelProperty(position = 2, value = "Timestamp of the DowntimeEntry creation, in milliseconds", example = "1609459200000", readOnly = true)
    @Override
    public long getCreatedTime() {
        return super.getCreatedTime();
    }

    @ApiModelProperty(position = 3, value = "JSON object with Tenant Id. Use 'assignDowntimeEntryToTenant' to change the Tenant Id.", readOnly = true)
    public TenantId getTenantId() {
        return tenantId;
    }

    public void setTenantId(TenantId tenantId) {
        this.tenantId = tenantId;
    }

    @ApiModelProperty(position = 4, value = "JSON object with Customer Id. Use 'assignDowntimeEntryToCustomer' to change the Customer Id.", readOnly = true)
    public CustomerId getCustomerId() {
        return customerId;
    }

    public void setCustomerId(CustomerId customerId) {
        this.customerId = customerId;
    }

    @ApiModelProperty(position = 5, required = true, value = "Unique DowntimeEntry Name in scope of Tenant", example = "A4B72CCDFF33")
    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ApiModelProperty(position = 6, required = true, value = "Asset Id", example = "Test Asset")
    public AssetId getAssetId() {
        return assetId;
    }

    public void setAssetId(AssetId assetId) {
        this.assetId = assetId;
    }

    @ApiModelProperty(position = 7, required = true, value = "Asset Id", example = "Test Asset")
    public DeviceId getDeviceId(){return deviceId;}

    public void setDeviceId(DeviceId deviceId){this.deviceId = deviceId;}

    @ApiModelProperty(position = 8, required = true, value = "Asset Id", example = "Test Asset")
    public String getReason(){return reason;}

    public void setReason(String reason){this.reason = reason;}

    @ApiModelProperty(position = 9, required = true, value = "Start time for the shift", example = "Date time")
    public Long getStartDateTimeMs() {
        return startDateTimeMs;
    }

    public void setStartDateTimeMs(Long startDateTimeMs) {
        this.startDateTimeMs = startDateTimeMs;
    }

    @ApiModelProperty(position = 10, required = true, value = "End time", example = "Date time")
    public Long getEndDateTimeMs() {
        return endDateTimeMs;
    }

    public void setEndDateTimeMs(Long endDateTimeMs) {
        this.endDateTimeMs = endDateTimeMs;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Downtime Entry [tenantId=");
        builder.append(tenantId);
        builder.append(", customerId=");
        builder.append(customerId);
        builder.append(", name=");
        builder.append(name);
        builder.append(", deviceId=");
        builder.append(deviceId);
        builder.append(", startTime=");
        builder.append(startDateTimeMs);
        builder.append(", endTime=");
        builder.append(endDateTimeMs);
        builder.append(", createdTime=");
        builder.append(createdTime);
        builder.append(", reason=");
        builder.append(reason);
        builder.append(", id=");
        builder.append(id);
        builder.append("]");
        return builder.toString();
    }
}
