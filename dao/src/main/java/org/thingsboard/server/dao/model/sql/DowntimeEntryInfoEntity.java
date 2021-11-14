package org.thingsboard.server.dao.model.sql;

import com.fasterxml.jackson.databind.JsonNode;
import org.thingsboard.server.common.data.downtime_entry.DowntimeEntry;
import org.thingsboard.server.common.data.downtime_entry.DowntimeEntryInfo;

import java.util.HashMap;
import java.util.Map;

public class DowntimeEntryInfoEntity extends AbstractDowntimeEntryEntity<DowntimeEntryInfo> {
    public static final Map<String,String> downtimeEntryInfoColumnMap = new HashMap<>();
    static {
        downtimeEntryInfoColumnMap.put("customerTitle", "c.title");
    }

    private String customerTitle;
    private boolean customerIsPublic;

    public DowntimeEntryInfoEntity() {
        super();
    }

    public DowntimeEntryInfoEntity(DowntimeEntryEntity downtimeEntryEntity, String customerTitle, Object customerAdditionalInfo){
        super(downtimeEntryEntity);
        this.customerTitle = customerTitle;
        if (customerAdditionalInfo != null && ((JsonNode)customerAdditionalInfo).has("isPublic")) {
            this.customerIsPublic = ((JsonNode)customerAdditionalInfo).get("isPublic").asBoolean();
        } else {
            this.customerIsPublic = false;
        }

    }

    @Override
    public DowntimeEntryInfo toData() {
        return new DowntimeEntryInfo(super.toDowntimeEntry(), customerTitle, customerIsPublic);
    }
}
