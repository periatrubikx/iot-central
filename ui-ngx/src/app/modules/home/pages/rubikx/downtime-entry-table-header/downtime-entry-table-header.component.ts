import { Component, OnInit } from '@angular/core';
import { AppState } from '@app/core/core.state';
import { EntityTableHeaderComponent } from '@app/modules/home/components/entity/entity-table-header.component';
import { DowntimeEntryInfo } from '@app/shared/models/downtime-entry.models';
import { EntityType } from '@app/shared/public-api';
import { Store } from '@ngrx/store';

@Component({
  selector: 'tb-downtime-entry-table-header',
  templateUrl: './downtime-entry-table-header.component.html',
  styleUrls: ['./downtime-entry-table-header.component.scss']
})
export class DowntimeEntryTableHeaderComponent extends EntityTableHeaderComponent<DowntimeEntryInfo> {

  entityType = EntityType;
  
  constructor(protected store:Store<AppState>) {
    super(store);
  }

  downtimeEntryTypeChanged(downtimeEntryType: string) {
    this.entitiesTableConfig.componentsData.downtimeEntrysType = downtimeEntryType;
    this.entitiesTableConfig.table.resetSortAndFilter(true);
  }

}
