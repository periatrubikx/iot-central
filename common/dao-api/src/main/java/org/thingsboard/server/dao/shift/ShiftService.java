package org.thingsboard.server.dao.shift;

import org.thingsboard.server.common.data.id.DowntimeCodeId;
import org.thingsboard.server.common.data.id.ShiftId;
import org.thingsboard.server.common.data.shift.Shift;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.shift.ShiftInfo;

public interface ShiftService {
    PageData<Shift> findShiftsByTenantId(TenantId tenantId, PageLink pageLink);

    Shift saveShift(Shift shift);

    void deleteShift(TenantId tenantId, ShiftId shiftId);

    Shift findAssetInfoById(TenantId tenantId, ShiftId shiftId);

    Shift findShiftById(TenantId tenantId, ShiftId shiftId);
}
