package org.thingsboard.server.dao.model.sql;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.TypeDef;
import org.thingsboard.server.common.data.downtimecode.DowntimeCode;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.DowntimeCodeId;
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
public abstract class AbstractDowntimeCodeEntity<T extends DowntimeCode> extends BaseSqlEntity<T> implements SearchTextEntity<T> {

    @Column(name="level1")
    private String level1;

    @Column(name="level2")
    private String level2;

    @Column(name="level3")
    private String level3;

    @Column(name="search_text")
    private String searchText;

    @Column(name="code")
    private Integer code;

    @Column(name="customer_id")
    private UUID customerId;

    @Column(name="tenant_id")
    private UUID tenantId;

    public AbstractDowntimeCodeEntity() {
        super();
    }

    public AbstractDowntimeCodeEntity(DowntimeCode downtimeCode){
        if(downtimeCode.getId()!=null){
            this.setUuid(downtimeCode.getId().getId());
        }
        this.setCreatedTime(downtimeCode.getCreatedTime());
        if(downtimeCode.getTenantId()!=null){
            this.tenantId = downtimeCode.getTenantId().getId();
        }
        if(downtimeCode.getCustomerId()!=null){
            this.customerId = downtimeCode.getCustomerId().getId();
        }
        this.level1 = downtimeCode.getLevel1();
        this.level2 = downtimeCode.getLevel2();
        this.level3 = downtimeCode.getLevel3();
        this.code = downtimeCode.getCode();
    }

    public AbstractDowntimeCodeEntity(DowntimeCodeEntity downtimeCodeEntity){
        this.setId(downtimeCodeEntity.getId());
        this.setCreatedTime(downtimeCodeEntity.getCreatedTime());
        this.tenantId = downtimeCodeEntity.getTenantId();
        this.customerId = downtimeCodeEntity.getCustomerId();
        this.level1 = downtimeCodeEntity.getLevel1();
        this.level2 = downtimeCodeEntity.getLevel2();
        this.level3 = downtimeCodeEntity.getLevel3();
        this.code = downtimeCodeEntity.getCode();
        this.searchText = downtimeCodeEntity.getSearchText();
    }

    @Override
    public String getSearchTextSource() {
        return code.toString();
    }

    @Override
    public void setSearchText(String searchText){
        this.searchText = searchText;
    }

    public String getSearchText() {
        return searchText;
    }

    protected DowntimeCode toDowntimeCode() {
        DowntimeCode downtimeCode = new DowntimeCode(new DowntimeCodeId(id));
        downtimeCode.setCreatedTime(createdTime);
        if(tenantId!=null){
            downtimeCode.setTenantId(new TenantId(tenantId));
        }
        if(customerId!=null){
            downtimeCode.setCustomerId(new CustomerId(customerId));
        }
        downtimeCode.setLevel1(level1);
        downtimeCode.setLevel2(level2);
        downtimeCode.setLevel3(level3);
        downtimeCode.setCode(code);
        return downtimeCode;
    }

}
