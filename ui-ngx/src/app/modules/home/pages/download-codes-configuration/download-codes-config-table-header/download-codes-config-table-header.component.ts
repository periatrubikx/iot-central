import { Component, OnInit } from '@angular/core';
import { AppState } from '@app/core/core.state';
import { EntityTableHeaderComponent } from '@app/modules/home/components/entity/entity-table-header.component';
import { DownloadCodesConfigurationInfo } from '@app/shared/models/download-codes-config';
import { EntityType } from '@app/shared/models/entity-type.models';
import { Store } from '@ngrx/store';

@Component({
  selector: 'tb-download-codes-config-table-header',
  templateUrl: './download-codes-config-table-header.component.html',
  styleUrls: ['./download-codes-config-table-header.component.scss']
})
export class DownloadCodesConfigTableHeaderComponent extends EntityTableHeaderComponent<DownloadCodesConfigurationInfo> {

  entityType = EntityType;
  
  constructor(protected store:Store<AppState>) {
    super(store);
  }

  downloadCodesConfigurationTypeChanged(downloadCodesConfigurationType: string) {
    this.entitiesTableConfig.componentsData.downloadCodesConfigurationsType = downloadCodesConfigurationType;
    this.entitiesTableConfig.table.resetSortAndFilter(true);
  }

}
