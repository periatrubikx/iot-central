package org.thingsboard.server.dao.sql.downtimecode;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.downtimecode.DowntimeCode;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.shift.Shift;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.downtimecode.DowntimeCodesDao;
import org.thingsboard.server.dao.model.sql.DowntimeCodeEntity;
import org.thingsboard.server.dao.model.sql.ShiftEntity;
import org.thingsboard.server.dao.sql.JpaAbstractSearchTextDao;

import java.util.Objects;
import java.util.UUID;

@Component
@Slf4j
public class JpaDowntimeCodesDao extends JpaAbstractSearchTextDao<DowntimeCodeEntity, DowntimeCode> implements DowntimeCodesDao {
    @Autowired  DowntimeCodesRepository downtimeCodesRepository;

    @Override
    protected Class<DowntimeCodeEntity> getEntityClass() {
        return DowntimeCodeEntity.class;
    }

    @Override
    protected CrudRepository<DowntimeCodeEntity, UUID> getCrudRepository() {
        return downtimeCodesRepository;
    }

    @Override
    public PageData<DowntimeCode> findDowntimeCodesByTenantId(UUID tenantId, PageLink pageLink) {
        Page<DowntimeCodeEntity> byTenantId = downtimeCodesRepository.findByTenantId(tenantId, Objects.toString(pageLink.getTextSearch(), ""), DaoUtil.toPageable(pageLink));
        return DaoUtil.toPageData(byTenantId);
    }

    @Override
    public DowntimeCode findDowntimeCodeInfoById(TenantId tenantId, UUID downtimeCodeId) {
        return DaoUtil.getData(downtimeCodesRepository.findDowntimeCodeInfoById(downtimeCodeId));
    }
}
