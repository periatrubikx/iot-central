package org.thingsboard.server.common.data.id;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.thingsboard.server.common.data.EntityType;

import java.util.UUID;

@ApiModel
public class DowntimeCodeId extends UUIDBased implements EntityId {


    @JsonCreator
    public DowntimeCodeId(@JsonProperty("id") UUID id) {
        super(id);
    }

    public static DowntimeCodeId fromString(String downtimeCodeId) {
        return new DowntimeCodeId(UUID.fromString(downtimeCodeId));
    }

    @ApiModelProperty(position = 2, required = true, value = "string", example = "DOWNTIME CODE", allowableValues = "DOWNTIME_CODE")
    @Override
    public EntityType getEntityType() {
        return EntityType.DOWNTIME_CODE;
    }

}
