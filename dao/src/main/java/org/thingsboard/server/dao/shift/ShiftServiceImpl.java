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

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import static org.thingsboard.server.common.data.CacheConstants.SHIFT_CACHE;

@Service
@Slf4j
public class ShiftServiceImpl implements ShiftService{

    @Autowired
    ShiftDao shiftDao;

    @Override
    public PageData<Shift> findShiftsByTenantId(TenantId tenantId, PageLink pageLink) {
        ArrayList<Shift> shifts = new ArrayList<>();
        Shift shift = new Shift();
        shift.setId(new ShiftId(UUID.randomUUID()));
        shift.setTenantId(tenantId);
        shift.setName("A-Shift");
        shift.setAreaName("Packaging");
        shift.setStartTimeMs(new Date().getTime());
        shift.setEndTimeMs(new Date().getTime());
        shifts.add(shift);

        Shift shift1 = new Shift();
        shift1.setId(new ShiftId(UUID.randomUUID()));
        shift1.setTenantId(tenantId);
        shift1.setName("B-Shift");
        shift1.setAreaName("Packaging");
        shift1.setStartTimeMs(new Date().getTime());
        shift1.setEndTimeMs(new Date().getTime());
        shifts.add(shift1);

        Shift shift2 = new Shift();
        shift2.setId(new ShiftId(UUID.randomUUID()));
        shift2.setTenantId(tenantId);
        shift2.setName("C-Shift");
        shift2.setAreaName("Packaging");
        shift2.setStartTimeMs(new Date().getTime());
        shift2.setEndTimeMs(new Date().getTime());
        shifts.add(shift2);


        PageData<Shift> shiftPageData = new PageData<>(shifts, 1, 1, false);

        return shiftPageData;
    }

//    @CacheEvict(cacheNames = SHIFT_CACHE, key = "{#shift.tenantId, #shift.name}")
    @Override
    public Shift saveShift(Shift shift) {
        log.trace("Executing saveShift [{}]", shift);
        Shift savedShift = null;
        try {
//            savedShift = shiftDao.save(shift.getTenantId(), shift);
            savedShift = new Shift();
            savedShift.setId(new ShiftId(UUID.randomUUID()));
            savedShift.setTenantId(shift.getTenantId());
            savedShift.setName(shift.getName());
            savedShift.setAreaName(shift.getAreaName());
            savedShift.setStartTimeMs(shift.getStartTimeMs());
            savedShift.setEndTimeMs(shift.getEndTimeMs());
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
}
