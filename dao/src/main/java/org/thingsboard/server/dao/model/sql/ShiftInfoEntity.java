package org.thingsboard.server.dao.model.sql;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.thingsboard.server.common.data.shift.ShiftInfo;

import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
public class ShiftInfoEntity extends AbstractShiftEntity<ShiftInfo> {

    public static final Map<String,String> shiftInfoColumnMap = new HashMap<>();
    static {
        shiftInfoColumnMap.put("customerTitle", "c.title");
    }

    private String customerTitle;
    private boolean customerIsPublic;

    public ShiftInfoEntity() {
        super();
    }

    public ShiftInfoEntity(ShiftEntity shiftEntity, String customerTitle, Object customerAdditionalInfo){
        super(shiftEntity);
        this.customerTitle = customerTitle;
        if (customerAdditionalInfo != null && ((JsonNode)customerAdditionalInfo).has("isPublic")) {
            this.customerIsPublic = ((JsonNode)customerAdditionalInfo).get("isPublic").asBoolean();
        } else {
            this.customerIsPublic = false;
        }

    }

    @Override
    public ShiftInfo toData() {
        return new ShiftInfo(super.toShift(), customerTitle, customerIsPublic);
    }
}
