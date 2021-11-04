import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { DowntimeCodesConfigRoutingModule } from './downtime-codes-config-routing.module';
import { SharedModule } from '@app/shared/shared.module';
import { HomeComponentsModule } from '../../components/home-components.module';
import { HomeDialogsModule } from '../../dialogs/home-dialogs.module';
import { DowntimeCodesConfigTableHeaderComponent } from './downtime-codes-config-table-header/downtime-codes-config-table-header.component';
import { DowntimeCodesConfigComponent } from './downtime-codes-config/downtime-codes-config.component';

@NgModule({
  declarations: [
    DowntimeCodesConfigTableHeaderComponent,
    DowntimeCodesConfigComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    HomeComponentsModule,
    HomeDialogsModule,
    DowntimeCodesConfigRoutingModule
  ]
})
export class DowntimeCodesConfigModule { }
