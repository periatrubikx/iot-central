package org.thingsboard.server.dao.model.sql;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.TypeDef;
import org.thingsboard.server.common.data.downtime_entry.DowntimeEntry;
import org.thingsboard.server.common.data.id.*;
import org.thingsboard.server.dao.model.BaseSqlEntity;
import org.thingsboard.server.dao.model.SearchTextEntity;
import org.thingsboard.server.dao.util.mapping.JsonStringType;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@TypeDef(name = "json", typeClass = JsonStringType.class)
@MappedSuperclass
public abstract class AbstractDowntimeEntryEntity<T extends DowntimeEntry> extends BaseSqlEntity<T> implements SearchTextEntity<T> {
    @Column(name="shift_id")
    private UUID shiftId;

    @Column(name="tenant_id")
    private UUID tenantId;

    @Column(name="customer_id")
    private UUID customerId;

    @Column(name="asset_id")
    private UUID assetId;

    @Column(name="device_id")
    private UUID deviceId;

    @Column(name="downtime_code_id")
    private UUID downtimeCodeId;

    @Column(name="start_date_time")
    private Long startDateTime;

    @Column(name="end_date_time")
    private Long endDateTime;

    @Column(name="name")
    private String name;

    @Column(name="search_text")
    private String searchText;

    public AbstractDowntimeEntryEntity() {
        super();
    }

    public AbstractDowntimeEntryEntity(DowntimeEntry downtimeEntry){
        if(downtimeEntry.getId()!=null){
            this.setUuid(downtimeEntry.getId().getId());
        }
        this.setCreatedTime(downtimeEntry.getCreatedTime());
        if(downtimeEntry.getTenantId()!=null){
            this.tenantId = downtimeEntry.getTenantId().getId();
        }
        if(downtimeEntry.getCustomerId()!=null){
            this.customerId = downtimeEntry.getCustomerId().getId();
        }
        if (downtimeEntry.getAssetId()!=null) {
            this.assetId = downtimeEntry.getAssetId().getId();
        }
        if (downtimeEntry.getDeviceId()!=null) {
            this.deviceId = downtimeEntry.getDeviceId().getId();
        }
        if (downtimeEntry.getDowntimeCodeId()!=null) {
            this.downtimeCodeId = downtimeEntry.getDowntimeCodeId().getId();
        }
        this.startDateTime = downtimeEntry.getStartDateTimeMs();
        this.endDateTime = downtimeEntry.getEndDateTimeMs();
        this.name = downtimeEntry.getName();
//        this.searchText = downtimeEntry.getDowntimeCodeId();
    }

    public AbstractDowntimeEntryEntity(DowntimeEntryEntity downtimeEntryEntity){
        this.setId(downtimeEntryEntity.getId());
        this.setCreatedTime(downtimeEntryEntity.getCreatedTime());
        this.tenantId = downtimeEntryEntity.getTenantId();
        this.customerId = downtimeEntryEntity.getCustomerId();
        this.assetId = downtimeEntryEntity.getAssetId();
        this.deviceId = downtimeEntryEntity.getDeviceId();
        this.startDateTime = downtimeEntryEntity.getStartDateTime();
        this.endDateTime = downtimeEntryEntity.getEndDateTime();
        this.downtimeCodeId = downtimeEntryEntity.getDowntimeCodeId();
        this.name = downtimeEntryEntity.getName();
//        this.searchText = downtimeEntryEntity.getDowntimeCodeId();
    }


    protected DowntimeEntry toDowntimeEntry() {
        DowntimeEntry downtimeEntry = new DowntimeEntry(new DowntimeEntryId(id));
        downtimeEntry.setCreatedTime(createdTime);
        if(tenantId!=null){
            downtimeEntry.setTenantId(new TenantId(tenantId));
        }
        if(customerId!=null){
            downtimeEntry.setCustomerId(new CustomerId(customerId));
        }
        if (assetId!=null) {
            downtimeEntry.setAssetId(new AssetId(assetId));
        }
        if (deviceId!=null) {
            downtimeEntry.setDeviceId(new DeviceId(deviceId));
        }
        if (downtimeCodeId!=null) {
            downtimeEntry.setDowntimeCodeId(new DowntimeCodeId(downtimeCodeId));
        }
        downtimeEntry.setStartDateTimeMs(startDateTime);
        downtimeEntry.setEndDateTimeMs(endDateTime);
        downtimeEntry.setName(name);
        return downtimeEntry;
    }


    //TODO: Need to update this to something meaningful.
    @Override
    public String getSearchTextSource() {
        return "";
    }

    @Override
    public void setSearchText(String searchText){
        this.searchText = searchText;
    }



}
