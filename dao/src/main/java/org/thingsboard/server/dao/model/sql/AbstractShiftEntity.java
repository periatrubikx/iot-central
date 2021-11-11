package org.thingsboard.server.dao.model.sql;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.TypeDef;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.ShiftId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.shift.Shift;
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
public abstract class AbstractShiftEntity<T extends Shift> extends BaseSqlEntity<T> implements SearchTextEntity<T> {

    @Column(name="name")
    private String name;

    @Column(name="area_name")
    private String areaName;

    @Column(name="search_text")
    private String searchText;

    @Column(name="customer_id")
    private UUID customerId;

    @Column(name="tenant_id")
    private UUID tenantId;

    @Column(name="start_time")
    private long startTime;

    @Column(name="end_time")
    private long endTime;

    public AbstractShiftEntity() {
        super();
    }

    public AbstractShiftEntity(Shift shift){
        if(shift.getId()!=null){
            this.setUuid(shift.getId().getId());
        }
        this.setCreatedTime(shift.getCreatedTime());
        if(shift.getTenantId()!=null){
            this.tenantId = shift.getTenantId().getId();
        }
        if(shift.getCustomerId()!=null){
            this.customerId = shift.getCustomerId().getId();
        }
        this.name = shift.getName();
        this.areaName = shift.getAreaName();
        this.startTime = shift.getStartTimeMs();
        this.endTime = shift.getEndTimeMs();
    }

    public AbstractShiftEntity(ShiftEntity shiftEntity){
        this.setId(shiftEntity.getId());
        this.setCreatedTime(shiftEntity.getCreatedTime());
        this.name = shiftEntity.getName();
        this.tenantId = shiftEntity.getTenantId();
        this.customerId = shiftEntity.getCustomerId();
        this.areaName = shiftEntity.getAreaName();
        this.startTime = shiftEntity.getStartTime();
        this.endTime = shiftEntity.getEndTime();
        this.searchText = shiftEntity.getSearchText();
    }

    @Override
    public String getSearchTextSource() {
        return name;
    }

    @Override
    public void setSearchText(String searchText){
        this.searchText = searchText;
    }

    public String getSearchText() {
        return searchText;
    }

    protected Shift toShift(){
        Shift shift = new Shift(new ShiftId(id));
        shift.setCreatedTime(createdTime);
        if(tenantId != null){
            shift.setTenantId(new TenantId(tenantId));
        }
        if(customerId != null){
            shift.setCustomerId(new CustomerId(customerId));
        }
        shift.setName(name);
        shift.setAreaName(areaName);
        shift.setStartTimeMs(startTime);
        shift.setEndTimeMs(endTime);
        return shift;
    }
}
