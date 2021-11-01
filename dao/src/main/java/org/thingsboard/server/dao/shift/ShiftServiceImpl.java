package org.thingsboard.server.dao.shift;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.shift.Shift;
import org.thingsboard.server.common.data.id.ShiftId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.shift.ShiftInfo;
import org.thingsboard.server.dao.entity.AbstractEntityService;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import static org.thingsboard.server.common.data.CacheConstants.SHIFT_CACHE;
import static org.thingsboard.server.dao.service.Validator.validateId;
import static org.thingsboard.server.dao.service.Validator.validatePageLink;

@Service
@Slf4j
public class ShiftServiceImpl extends AbstractEntityService implements ShiftService{
    public static final String INCORRECT_TENANT_ID = "Incorrect tenantId ";

    @Autowired
    ShiftDao shiftDao;

    @Override
    public PageData<Shift> findShiftsByTenantId(TenantId tenantId, PageLink pageLink) {
        log.trace("Executing findShiftsByTenantId, tenantId [{}], pageLink [{}]", tenantId, pageLink);
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        validatePageLink(pageLink);
        return shiftDao.findShiftsByTennantId(tenantId.getId(), pageLink);
    }

//    @CacheEvict(cacheNames = SHIFT_CACHE, key = "{#shift.tenantId, #shift.name}")
    @Override
    public Shift saveShift(Shift shift) {
        log.trace("Executing saveShift [{}]", shift);
        Shift savedShift = null;
        try {
            savedShift = shiftDao.save(shift.getTenantId(), shift);
        } catch (Exception t) {
//            ConstraintViolationException e = extractConstraintViolationException(t).orElse(null);
//            if (e != null && e.getConstraintName() != null && e.getConstraintName().equalsIgnoreCase("asset_name_unq_key")) {
//                throw new DataValidationException("Asset with such name already exists!");
//            } else {
//                throw t;
//            }
        }
        return savedShift;
    }

    @Override
    public void deleteShift(TenantId tenantId, ShiftId shiftId) {
        shiftDao.removeById(tenantId, shiftId.getId());
    }

    @Override
    public Shift findAssetInfoById(TenantId tenantId, ShiftId shiftId) {
//        log.trace("Executing findAssetInfoById [{}]", assetId);
//        validateId(assetId, INCORRECT_ASSET_ID + assetId);
        return shiftDao.findAssetInfoById(tenantId, shiftId.getId());
    }
}
