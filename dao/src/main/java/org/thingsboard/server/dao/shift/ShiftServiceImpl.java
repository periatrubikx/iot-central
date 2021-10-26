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
        PageData<Shift> shiftPageData = new PageData<>(shifts, 1, 1, false);

        return shiftPageData;
    }
}
