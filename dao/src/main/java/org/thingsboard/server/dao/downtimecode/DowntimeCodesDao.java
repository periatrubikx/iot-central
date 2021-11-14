package org.thingsboard.server.dao.downtimecode;

import org.thingsboard.server.common.data.downtime_code.DowntimeCode;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.Dao;

import java.util.UUID;

public interface DowntimeCodesDao extends Dao<DowntimeCode> {
    DowntimeCode save(TenantId tenantId, DowntimeCode downtimeCode);

    PageData<DowntimeCode> findDowntimeCodesByTenantId(UUID id, PageLink pageLink);

    DowntimeCode findDowntimeCodeInfoById(TenantId tenantId, UUID id);
}
