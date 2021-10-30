package org.thingsboard.server.dao.shift;

import org.thingsboard.server.common.data.shift.ShiftArea;
import org.thingsboard.server.common.data.id.TenantId;

import java.util.List;

public interface ShiftAreaService {
    List<ShiftArea> findAreasByTenantId(TenantId tenantId);
}
