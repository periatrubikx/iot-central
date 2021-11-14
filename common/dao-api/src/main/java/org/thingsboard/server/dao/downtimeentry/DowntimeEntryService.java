package org.thingsboard.server.dao.downtimeentry;

import org.thingsboard.server.common.data.downtime_entry.DowntimeEntry;
import org.thingsboard.server.common.data.downtime_entry.DowntimeEntryInfo;
import org.thingsboard.server.common.data.id.DowntimeEntryId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;

public interface DowntimeEntryService {
    DowntimeEntry saveDowntimeEntry(DowntimeEntry downtimeEntry);

    PageData<DowntimeEntryInfo> findDowntimeEntryInfosByTenantId(TenantId tenantId, PageLink pageLink);

    DowntimeEntry findDowntimeEntryById(TenantId tenantId, DowntimeEntryId downtimeEntryId);

    void deleteDowntimeEntry(TenantId tenantId, DowntimeEntryId downtimeEntryId);
}
