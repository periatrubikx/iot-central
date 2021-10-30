package org.thingsboard.server.dao.shift;

import org.thingsboard.server.common.data.shift.Shift;
import org.thingsboard.server.common.data.id.TenantId;

public interface ShiftDao {
    /**
     * Save or update shift object
     *
     * @param shift the shift object
     * @return saved shift object
     */
    Shift save(TenantId tenantId, Shift shift);
}
