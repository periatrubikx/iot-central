package org.thingsboard.server.common.data.shift;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.common.data.asset.Asset;
import org.thingsboard.server.common.data.id.AssetId;
import org.thingsboard.server.common.data.id.ShiftId;

@ApiModel
@Data
public class ShiftInfo extends Shift {
    @ApiModelProperty(position = 9, value = "Title of the Customer that owns the asset.", readOnly = true)
    private String customerTitle;
    @ApiModelProperty(position = 10, value = "Indicates special 'Public' Customer that is auto-generated to use the assets on public dashboards.", readOnly = true)
    private boolean customerIsPublic;

    public ShiftInfo() {
        super();
    }

    public ShiftInfo(ShiftId shiftId) {
        super(shiftId);
    }

    public ShiftInfo(Shift shift, String customerTitle, boolean customerIsPublic) {
        super(shift);
        this.customerTitle = customerTitle;
        this.customerIsPublic = customerIsPublic;
    }
}
