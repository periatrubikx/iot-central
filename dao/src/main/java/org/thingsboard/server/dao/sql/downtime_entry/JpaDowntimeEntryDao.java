package org.thingsboard.server.dao.sql.downtime_entry;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.downtime_code.DowntimeCode;
import org.thingsboard.server.common.data.downtime_entry.DowntimeEntry;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.dao.downtime_entry.DowntimeEntryDao;
import org.thingsboard.server.dao.model.sql.DowntimeEntryEntity;
import org.thingsboard.server.dao.sql.JpaAbstractSearchTextDao;

import java.util.UUID;

@Component
@Slf4j
public class JpaDowntimeEntryDao extends JpaAbstractSearchTextDao<DowntimeEntryEntity, DowntimeEntry> implements DowntimeEntryDao {
    @Autowired
    DowntimeEntryRespository downtimeEntryRespository;

    @Override
    public DowntimeEntry save(TenantId tenantId, DowntimeCode downtimeCode) {
        return null;
    }

    @Override
    protected Class<DowntimeEntryEntity> getEntityClass() {
        return DowntimeEntryEntity.class;
    }

    @Override
    protected CrudRepository<DowntimeEntryEntity, UUID> getCrudRepository() {
        return downtimeEntryRespository;
    }
}
