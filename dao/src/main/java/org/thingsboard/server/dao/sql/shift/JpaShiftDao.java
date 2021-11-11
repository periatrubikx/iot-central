package org.thingsboard.server.dao.sql.shift;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.shift.Shift;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.shift.ShiftInfo;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.model.sql.ShiftEntity;
import org.thingsboard.server.dao.model.sql.ShiftInfoEntity;
import org.thingsboard.server.dao.shift.ShiftDao;
import org.thingsboard.server.dao.sql.JpaAbstractSearchTextDao;

import java.util.Objects;
import java.util.UUID;

@Component
@Slf4j
public class JpaShiftDao extends JpaAbstractSearchTextDao<ShiftEntity, Shift> implements ShiftDao {
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
    public PageData<Shift> findShiftsByTennantId(UUID tenantId, PageLink pageLink) {
        Page<ShiftEntity> byTenantId = shiftRepository.findByTenantId(tenantId, Objects.toString(pageLink.getTextSearch(), ""), DaoUtil.toPageable(pageLink));
        return DaoUtil.toPageData(byTenantId);
    }

    @Override
    public Shift findAssetInfoById(TenantId tenantId, UUID shiftId) {
        return DaoUtil.getData(shiftRepository.findAssetInfoById(shiftId));
    }

    @Override
    public PageData<ShiftInfo> findShiftInfosByTenantId(UUID tenantId, PageLink pageLink) {
        return DaoUtil.toPageData(
                shiftRepository.findShiftInfosByTenantId(
                        tenantId,
                        Objects.toString(pageLink.getTextSearch(), ""),
                        DaoUtil.toPageable(pageLink, ShiftInfoEntity.shiftInfoColumnMap)));
    }
}
