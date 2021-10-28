package org.thingsboard.server.dao.shift;

import org.thingsboard.server.common.data.Shift;
import org.thingsboard.server.common.data.ShiftArea;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;

public interface ShiftAreaService {
    PageData<ShiftArea> findAreasByTenantId(TenantId tenantId, PageLink pageLink);
}
