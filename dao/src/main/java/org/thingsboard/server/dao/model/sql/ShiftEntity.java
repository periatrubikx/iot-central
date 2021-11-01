package org.thingsboard.server.dao.model.sql;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.TypeDef;
import org.thingsboard.server.common.data.shift.Shift;
import org.thingsboard.server.dao.util.mapping.JsonStringType;

import javax.persistence.Entity;
import javax.persistence.Table;

import static org.thingsboard.server.dao.model.ModelConstants.ASSET_COLUMN_FAMILY_NAME;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@TypeDef(name = "json", typeClass = JsonStringType.class)
@Table(name = "shift")
public class ShiftEntity extends AbstractShiftEntity<Shift> {

    public ShiftEntity() {
        super();
    }

    public ShiftEntity(Shift shift){
        super(shift);
    }

    @Override
    public Shift toData() {
        return super.toShift();
    }
}
