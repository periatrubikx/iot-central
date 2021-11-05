package org.thingsboard.server.dao.downtimecode;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.downtimecode.DowntimeCode;
import org.thingsboard.server.common.data.id.DowntimeCodeId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.entity.AbstractEntityService;

import static org.thingsboard.server.dao.service.Validator.validateId;
import static org.thingsboard.server.dao.service.Validator.validatePageLink;

@Service
@Slf4j
public class DowntimeCodesServiceImpl extends AbstractEntityService implements DowntimeCodesService {
    public static final String INCORRECT_TENANT_ID = "Incorrect tenantId ";
    public static final String INCORRECT_DOWNTIME_CODE_ID = "Incorrect downtimeCodeId ";

    @Autowired
    DowntimeCodesDao downtimeCodesDao;

    @Override
    public PageData<DowntimeCode> findDowntimeCodesByTenantId(TenantId tenantId, PageLink pageLink) {
        log.trace("Executing findDowntimeCodesByTenantId, tenantId [{}], pageLink [{}]", tenantId, pageLink);
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        validatePageLink(pageLink);
        return downtimeCodesDao.findDowntimeCodesByTenantId(tenantId.getId(), pageLink);
    }

    @Override
    public DowntimeCode saveDowntimeCode(DowntimeCode downtimeCode) {
        log.trace("Executing saveDowntimeCode [{}]", downtimeCode);
        DowntimeCode savedDowntimeCode = null;
        try {
            savedDowntimeCode = downtimeCodesDao.save(downtimeCode.getTenantId(), downtimeCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return savedDowntimeCode;
    }

    @Override
    public void deleteDowntimeCode(TenantId tenantId, DowntimeCodeId downtimeCodeId) {
        downtimeCodesDao.removeById(tenantId, downtimeCodeId.getId());
    }

    @Override
    public DowntimeCode findDowntimeCodeById(TenantId tenantId, DowntimeCodeId downtimeCodeId) {
        log.trace("Executing findAssetInfoById [{}]", downtimeCodeId);
        validateId(downtimeCodeId, INCORRECT_DOWNTIME_CODE_ID + downtimeCodeId);
        return findDowntimeCodeById(tenantId, downtimeCodeId);
    }
}
