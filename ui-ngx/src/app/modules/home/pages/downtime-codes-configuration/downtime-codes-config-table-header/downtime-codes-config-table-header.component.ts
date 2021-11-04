import { Component, OnInit } from '@angular/core';
import { AppState } from '@app/core/core.state';
import { EntityTableHeaderComponent } from '@app/modules/home/components/entity/entity-table-header.component';
import { DowntimeCodesConfigurationInfo } from '@app/shared/models/downtime-codes-config';
import { EntityType } from '@app/shared/models/entity-type.models';
import { Store } from '@ngrx/store';

@Component({
  selector: 'tb-downtime-codes-config-table-header',
  templateUrl: './downtime-codes-config-table-header.component.html',
  styleUrls: ['./downtime-codes-config-table-header.component.scss']
})
export class DowntimeCodesConfigTableHeaderComponent extends EntityTableHeaderComponent<DowntimeCodesConfigurationInfo> {

  entityType = EntityType;

  constructor(protected store:Store<AppState>) {
    super(store);
  }

  downtimeCodesConfigurationTypeChanged(downtimeCodesConfigurationType: string) {
    this.entitiesTableConfig.componentsData.downtimeCodesConfigurationsType = downtimeCodesConfigurationType;
    this.entitiesTableConfig.table.resetSortAndFilter(true);
  }

}
