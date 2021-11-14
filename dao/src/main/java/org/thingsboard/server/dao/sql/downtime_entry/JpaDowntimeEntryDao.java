package org.thingsboard.server.dao.sql.downtime_entry;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.downtime_entry.DowntimeEntry;
import org.thingsboard.server.common.data.downtime_entry.DowntimeEntryInfo;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.downtimeentry.DowntimeEntryDao;
import org.thingsboard.server.dao.model.sql.DowntimeEntryEntity;
import org.thingsboard.server.dao.model.sql.DowntimeEntryInfoEntity;
import org.thingsboard.server.dao.sql.JpaAbstractSearchTextDao;

import java.util.Objects;
import java.util.UUID;

@Component
@Slf4j
public class JpaDowntimeEntryDao extends JpaAbstractSearchTextDao<DowntimeEntryEntity, DowntimeEntry> implements DowntimeEntryDao {
    @Autowired
    DowntimeEntryRepository downtimeEntryRepository;

    @Override
    protected Class<DowntimeEntryEntity> getEntityClass() {
        return DowntimeEntryEntity.class;
    }

    @Override
    protected CrudRepository<DowntimeEntryEntity, UUID> getCrudRepository() {
        return downtimeEntryRepository;
    }

    @Override
    public PageData<DowntimeEntryInfo> findDowntimeEntryInfosByTenantId(UUID tenantId, PageLink pageLink) {
        return DaoUtil.toPageData(
                downtimeEntryRepository.findDowntimeEntryInfosByTenantId(
                        tenantId,
                        Objects.toString(pageLink.getTextSearch(), ""),
                        DaoUtil.toPageable(pageLink, DowntimeEntryInfoEntity.downtimeEntryInfoColumnMap)));
    }

    @Override
    public DowntimeEntry findDowntimeCodeInfoById(TenantId tenantId, UUID downtimeEntryId) {
        return DaoUtil.getData(downtimeEntryRepository.findDowntimeEntryById(downtimeEntryId));
    }
}
