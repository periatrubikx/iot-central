package org.thingsboard.server.dao.model.sql;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.TypeDef;
import org.thingsboard.server.common.data.downtimecode.DowntimeCode;
import org.thingsboard.server.common.data.shift.Shift;
import org.thingsboard.server.dao.util.mapping.JsonStringType;

import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@TypeDef(name = "json", typeClass = JsonStringType.class)
@Table(name = "downtime_code")
public class DowntimeCodeEntity extends AbstractDowntimeCodeEntity<DowntimeCode> {
    public DowntimeCodeEntity() {
        super();
    }

    public DowntimeCodeEntity(DowntimeCode downtimeCode){
        super(downtimeCode);
    }

    @Override
    public DowntimeCode toData() {
        return super.toDowntimeCode();
    }
}
