package org.thingsboard.server.dao.shift;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.Shift;
import org.thingsboard.server.common.data.ShiftArea;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;

import java.util.ArrayList;
import java.util.Date;

@Service
@Slf4j
public class ShiftAreaImpl implements ShiftAreaService{
    @Override
    public PageData<ShiftArea> findAreasByTenantId(TenantId tenantId, PageLink pageLink) {
        ArrayList<ShiftArea> shiftAreas = new ArrayList<>();
        ShiftArea shiftArea = new ShiftArea();
        shiftArea.setTenantId(tenantId);
        shiftArea.setName("Packaging");
        shiftAreas.add(shiftArea);

        ShiftArea shiftArea1 = new ShiftArea();
        shiftArea1.setTenantId(tenantId);
        shiftArea1.setName("Treatment");
        shiftAreas.add(shiftArea1);

        PageData<ShiftArea> shiftAreaPageData = new PageData<>(shiftAreas, 1, 1, false);

        return shiftAreaPageData;
    }
}
