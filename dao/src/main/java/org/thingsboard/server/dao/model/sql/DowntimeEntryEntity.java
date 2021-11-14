package org.thingsboard.server.dao.model.sql;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.TypeDef;
import org.thingsboard.server.common.data.downtime_entry.DowntimeEntry;
import org.thingsboard.server.dao.util.mapping.JsonStringType;

import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@TypeDef(name = "json", typeClass = JsonStringType.class)
@Table(name = "downtime_code")
public class DowntimeEntryEntity extends AbstractDowntimeEntryEntity<DowntimeEntry> {
    public DowntimeEntryEntity() {
        super();
    }

    public DowntimeEntryEntity(DowntimeEntry downtimeEntry){
        super((downtimeEntry));
    }


    @Override
    public DowntimeEntry toData() {
        return super.toDowntimeEntry();
    }
}
