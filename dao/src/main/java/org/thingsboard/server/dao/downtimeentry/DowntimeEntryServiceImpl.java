package org.thingsboard.server.dao.downtimeentry;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.downtime_entry.DowntimeEntry;
import org.thingsboard.server.common.data.downtime_entry.DowntimeEntryInfo;
import org.thingsboard.server.common.data.id.DowntimeEntryId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.entity.AbstractEntityService;

import static org.thingsboard.server.dao.service.Validator.validateId;
import static org.thingsboard.server.dao.service.Validator.validatePageLink;

@Service
@Slf4j
public class DowntimeEntryServiceImpl extends AbstractEntityService implements DowntimeEntryService {
    public static final String INCORRECT_TENANT_ID = "Incorrect tenantId ";
    public static final String INCORRECT_DOWNTIME_ENTRY_ID = "Incorrect downtimeEntryId ";

    @Autowired
    DowntimeEntryDao downtimeEntryDao;

    @Override
    public DowntimeEntry saveDowntimeEntry(DowntimeEntry downtimeEntry) {
        log.trace("Executing saveDowntimeCode [{}]", downtimeEntryDao);
        DowntimeEntry savedDowntimeEntry = null;
        try {
            savedDowntimeEntry = downtimeEntryDao.save(downtimeEntry.getTenantId(), downtimeEntry);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return savedDowntimeEntry;
    }

    @Override
    public PageData<DowntimeEntryInfo> findDowntimeEntryInfosByTenantId(TenantId tenantId, PageLink pageLink) {
        log.trace("Executing findDowntimeEntryInfosByTenantId, tenantId [{}], pageLink [{}]", tenantId, pageLink);
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        validatePageLink(pageLink);
        PageData<DowntimeEntryInfo> downtimeEntryInfosByTenantId = downtimeEntryDao.findDowntimeEntryInfosByTenantId(tenantId.getId(), pageLink);
        return downtimeEntryInfosByTenantId;
    }

    @Override
    public DowntimeEntry findDowntimeEntryById(TenantId tenantId, DowntimeEntryId downtimeEntryId) {
        log.trace("Executing findDowntimeEntryById [{}]", downtimeEntryId);
        validateId(downtimeEntryId, INCORRECT_DOWNTIME_ENTRY_ID + downtimeEntryId);
        return downtimeEntryDao.findDowntimeCodeInfoById(tenantId, downtimeEntryId.getId());
    }

    @Override
    public void deleteDowntimeEntry(TenantId tenantId, DowntimeEntryId downtimeEntryId) {
        downtimeEntryDao.removeById(tenantId, downtimeEntryId.getId());
    }
}
