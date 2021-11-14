package org.thingsboard.server.dao.downtime_entry;

import org.thingsboard.server.common.data.downtime_entry.DowntimeEntry;
import org.thingsboard.server.common.data.id.DowntimeEntryId;

public interface DowntimeEntryService {
    DowntimeEntry saveDowntimeEntry(DowntimeEntry downtimeEntry);
}
