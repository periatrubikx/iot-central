package org.thingsboard.server.dao.shift;

import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.shift.Shift;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.dao.Dao;

import java.util.UUID;

public interface ShiftDao extends Dao<Shift> {
    /**
     * Save or update shift object
     *
     * @param shift the shift object
     * @return saved shift object
     */
    Shift save(TenantId tenantId, Shift shift);

    PageData<Shift> findShiftsByTennantId(UUID id, PageLink pageLink);
}
