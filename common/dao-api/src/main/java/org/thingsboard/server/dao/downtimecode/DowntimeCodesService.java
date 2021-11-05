package org.thingsboard.server.dao.downtimecode;

import org.thingsboard.server.common.data.downtimecode.DowntimeCode;
import org.thingsboard.server.common.data.id.DowntimeCodeId;
import org.thingsboard.server.common.data.id.ShiftId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.shift.Shift;

public interface DowntimeCodesService {
    PageData<DowntimeCode> findDowntimeCodesByTenantId(TenantId tenantId, PageLink pageLink);

    DowntimeCode saveDowntimeCode(DowntimeCode downtimeCode);

    void deleteDowntimeCode(TenantId tenantId, DowntimeCodeId downtimeCodeId);

    DowntimeCode findDowntimeCodeById(TenantId tenantId, DowntimeCodeId downtimeCodeId);
}
