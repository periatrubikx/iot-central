package org.thingsboard.server.dao.sql.shift;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.shift.Shift;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.dao.model.sql.ShiftEntity;
import org.thingsboard.server.dao.shift.ShiftDao;
import org.thingsboard.server.dao.sql.JpaAbstractDao;

import java.util.UUID;

@Component
@Slf4j
public class JpaShiftDao extends JpaAbstractDao<ShiftEntity, Shift> implements ShiftDao {
    @Autowired
    ShiftRepository shiftRepository;

    @Override
    protected Class<ShiftEntity> getEntityClass() {
        return ShiftEntity.class;
    }

    @Override
    protected CrudRepository<ShiftEntity, UUID> getCrudRepository() {
        return shiftRepository;
    }

    @Override
    public Shift save(TenantId tenantId, Shift shift) {
        return null;
    }
}
