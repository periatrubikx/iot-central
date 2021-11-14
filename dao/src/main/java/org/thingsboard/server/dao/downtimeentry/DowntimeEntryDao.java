package org.thingsboard.server.dao.downtime_entry;

import org.thingsboard.server.common.data.downtime_code.DowntimeCode;
import org.thingsboard.server.common.data.downtime_entry.DowntimeEntry;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.dao.Dao;

public interface DowntimeEntryDao extends Dao<DowntimeEntry> {
    DowntimeEntry save(TenantId tenantId, DowntimeCode downtimeCode);
}
