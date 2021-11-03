import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { DownloadCodesConfigRoutingModule } from './download-codes-config-routing.module';
import { SharedModule } from '@app/shared/shared.module';
import { HomeComponentsModule } from '../../components/home-components.module';
import { HomeDialogsModule } from '../../dialogs/home-dialogs.module';
import { DownloadCodesConfigTableHeaderComponent } from './download-codes-config-table-header/download-codes-config-table-header.component';
import { DownloadCodesConfigComponent } from './download-codes-config/download-codes-config.component';

@NgModule({
  declarations: [
    DownloadCodesConfigTableHeaderComponent,
    DownloadCodesConfigComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    HomeComponentsModule,
    HomeDialogsModule,
    DownloadCodesConfigRoutingModule
  ]
})
export class DownloadCodesConfigModule { }
