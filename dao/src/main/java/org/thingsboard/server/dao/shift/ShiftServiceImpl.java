package org.thingsboard.server.dao.shift;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.Shift;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;

import java.util.ArrayList;
import java.util.Date;

@Service
@Slf4j
public class ShiftServiceImpl implements ShiftService{
    @Override
    public PageData<Shift> findShiftsByTenantId(TenantId tenantId, PageLink pageLink) {
        ArrayList<Shift> shifts = new ArrayList<>();
        Shift shift = new Shift();
        shift.setTenantId(tenantId);
        shift.setName("A-Shift");
        shift.setAreaName("Packaging");
        shift.setStartTime(new Date());
        shift.setEndTime(new Date());
        shifts.add(shift);

        Shift shift1 = new Shift();
        shift1.setTenantId(tenantId);
        shift1.setName("B-Shift");
        shift1.setAreaName("Packaging");
        shift1.setStartTime(new Date());
        shift1.setEndTime(new Date());
        shifts.add(shift1);

        Shift shift2 = new Shift();
        shift2.setTenantId(tenantId);
        shift2.setName("C-Shift");
        shift2.setAreaName("Packaging");
        shift2.setStartTime(new Date());
        shift2.setEndTime(new Date());
        shifts.add(shift2);


        PageData<Shift> shiftPageData = new PageData<>(shifts, 1, 1, false);

        return shiftPageData;
    }

    @Override
    public Shift saveShift(Shift shift) {
        return null;
    }
}
