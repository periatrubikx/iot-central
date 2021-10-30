package org.thingsboard.server.dao.shift;

import org.thingsboard.server.common.data.shift.Shift;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;

public interface ShiftService {
    PageData<Shift> findShiftsByTenantId(TenantId tenantId, PageLink pageLink);

    Shift saveShift(Shift shift);
}
