package org.thingsboard.server.common.data.id;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.downtime_entry.DowntimeEntry;

import java.util.UUID;

@ApiModel
public class DowntimeEntryId extends UUIDBased implements EntityId {

    @JsonCreator
    public DowntimeEntryId(@JsonProperty("id") UUID id) {
        super(id);
    }

    public static DowntimeEntryId fromString(String downtimeEntryId) {
        return new DowntimeEntryId(UUID.fromString(downtimeEntryId));
    }

    @ApiModelProperty(position = 2, required = true, value = "string", example = "DOWNTIME_ENTRY", allowableValues = "DOWNTIME_ENTRY")
    @Override
    public EntityType getEntityType() {
        return EntityType.DOWNTIME_ENTRY;
    }
}
