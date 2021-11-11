package org.thingsboard.server.dao.shift;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.asset.Asset;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.DowntimeCodeId;
import org.thingsboard.server.common.data.shift.Shift;
import org.thingsboard.server.common.data.id.ShiftId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.shift.ShiftInfo;
import org.thingsboard.server.dao.entity.AbstractEntityService;
import org.thingsboard.server.dao.exception.DataValidationException;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import static org.thingsboard.server.common.data.CacheConstants.SHIFT_CACHE;
import static org.thingsboard.server.dao.asset.BaseAssetService.INCORRECT_ASSET_ID;
import static org.thingsboard.server.dao.service.Validator.validateId;
import static org.thingsboard.server.dao.service.Validator.validatePageLink;

@Service
@Slf4j
public class ShiftServiceImpl extends AbstractEntityService implements ShiftService{
    public static final String INCORRECT_TENANT_ID = "Incorrect tenantId ";
    public static final String INCORRECT_SHIFT_ID = "Incorrect shiftId ";

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
          t.printStackTrace();
        }
        return savedShift;
    }

    @Override
    public void deleteShift(TenantId tenantId, ShiftId shiftId) {
        shiftDao.removeById(tenantId, shiftId.getId());
    }

    @Override
    public Shift findAssetInfoById(TenantId tenantId, ShiftId shiftId) {
        log.trace("Executing findAssetInfoById [{}]", shiftId);
        validateId(shiftId, INCORRECT_SHIFT_ID + shiftId);
        return shiftDao.findAssetInfoById(tenantId, shiftId.getId());
    }

    @Override
    public Shift findShiftById(TenantId tenantId, ShiftId shiftId) {
        log.trace("Executing findShiftById [{}]", shiftId);
        validateId(shiftId, INCORRECT_SHIFT_ID + shiftId);
        return shiftDao.findById(tenantId, shiftId.getId());
    }

    @Override
    public Shift assignShiftToCustomer(TenantId tenantId, ShiftId shiftId, CustomerId customerId) {
        Shift shift = findShiftById(tenantId, shiftId);
        shift.setCustomerId(customerId);
        return saveShift(shift);
    }

    @Override
    public Shift unassignAssetFromCustomer(TenantId tenantId, ShiftId shiftId) {
        Shift shift = findShiftById(tenantId, shiftId);
        shift.setCustomerId(null);
        return saveShift(shift);
    }

    @Override
    public PageData<ShiftInfo> findShiftInfosByTenantId(TenantId tenantId, PageLink pageLink) {
        log.trace("Executing findShiftInfosByTenantId, tenantId [{}], pageLink [{}]", tenantId, pageLink);
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        validatePageLink(pageLink);
        return shiftDao.findShiftInfosByTenantId(tenantId.getId(), pageLink);
    }
}
