package org.thingsboard.server.dao.model.sql;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.TypeDef;
import org.thingsboard.server.common.data.downtime_code.DowntimeCode;
import org.thingsboard.server.common.data.downtime_entry.DowntimeEntry;
import org.thingsboard.server.common.data.id.AssetId;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.DowntimeEntryId;
import org.thingsboard.server.common.data.id.TenantId;
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

    @Column(name="reason")
    private String reason;

    @Column(name="start_date_time")
    private Long startDateTime;

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
        this.assetId = downtimeEntry.getAssetId().getId();
        this.deviceId = downtimeEntry.getDeviceId().getId();
        this.reason = downtimeEntry.getReason();
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
        downtimeEntry.setAssetId(new AssetId(assetId));
        downtimeEntry.setCustomerId(new CustomerId(customerId));
        downtimeEntry.setReason(reason);
        return downtimeEntry;
    }


    @Override
    public String getSearchTextSource() {
        return reason;
    }

    @Override
    public void setSearchText(String searchText){
        this.searchText = searchText;
    }



}
