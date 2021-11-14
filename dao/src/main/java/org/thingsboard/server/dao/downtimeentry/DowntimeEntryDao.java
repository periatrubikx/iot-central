package org.thingsboard.server.dao.downtimeentry;

import org.thingsboard.server.common.data.downtime_code.DowntimeCode;
import org.thingsboard.server.common.data.downtime_entry.DowntimeEntry;
import org.thingsboard.server.common.data.downtime_entry.DowntimeEntryInfo;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.Dao;

import java.util.UUID;

public interface DowntimeEntryDao extends Dao<DowntimeEntry> {
    DowntimeEntry save(TenantId tenantId, DowntimeEntry downtimeEntry);

    PageData<DowntimeEntryInfo> findDowntimeEntryInfosByTenantId(UUID id, PageLink pageLink);
}
