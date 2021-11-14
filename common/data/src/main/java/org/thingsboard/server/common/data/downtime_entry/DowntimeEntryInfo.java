package org.thingsboard.server.common.data.downtime_entry;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.common.data.id.DowntimeEntryId;

@ApiModel
@Data
public class DowntimeEntryInfo extends DowntimeEntry {
    @ApiModelProperty(position = 11, value = "Title of the Customer that owns the asset.", readOnly = true)
    private String customerTitle;
    @ApiModelProperty(position = 12, value = "Indicates special 'Public' Customer that is auto-generated to use the assets on public dashboards.", readOnly = true)
    private boolean customerIsPublic;

    public DowntimeEntryInfo() {
        super();
    }

    public DowntimeEntryInfo(DowntimeEntryId downtimeEntryId) {
        super(downtimeEntryId);
    }

    public DowntimeEntryInfo(DowntimeEntry downtimeEntry, String customerTitle, boolean customerIsPublic) {
        super(downtimeEntry);
        this.customerTitle = customerTitle;
        this.customerIsPublic = customerIsPublic;
    }
}
