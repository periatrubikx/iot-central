package org.thingsboard.server.dao.shift;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.shift.ShiftArea;
import org.thingsboard.server.common.data.id.TenantId;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ShiftAreaImpl implements ShiftAreaService{
    @Override
    public List<ShiftArea> findAreasByTenantId(TenantId tenantId) {
        ArrayList<ShiftArea> shiftAreas = new ArrayList<>();
        ShiftArea shiftArea = new ShiftArea();
        shiftArea.setTenantId(tenantId);
        shiftArea.setName("Packaging");
        shiftAreas.add(shiftArea);

        ShiftArea shiftArea1 = new ShiftArea();
        shiftArea1.setTenantId(tenantId);
        shiftArea1.setName("Treatment");
        shiftAreas.add(shiftArea1);

        return shiftAreas;
    }
}
