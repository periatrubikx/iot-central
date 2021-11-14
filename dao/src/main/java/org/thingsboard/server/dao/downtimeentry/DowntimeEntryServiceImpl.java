package org.thingsboard.server.dao.downtime_entry;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.downtime_code.DowntimeCode;
import org.thingsboard.server.common.data.downtime_entry.DowntimeEntry;
import org.thingsboard.server.dao.downtimecode.DowntimeCodesService;
import org.thingsboard.server.dao.entity.AbstractEntityService;

@Service
@Slf4j
public class DowntimeEntryServiceImpl extends AbstractEntityService implements DowntimeEntryService {
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
}
