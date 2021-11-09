import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { RubikxRoutingModule } from './rubikx-routing.module';
import { HomeComponentsModule } from '../../components/home-components.module';
import { SharedModule } from '@app/shared/shared.module';
import { ShiftComponent } from './shift.component';
import { ShiftTableHeaderComponent } from './shift-table-header/shift-table-header.component';
import { HomeDialogsModule } from '../../dialogs/home-dialogs.module';
import { ShiftTableConfigResolver } from './shift-table-config.resolver';
import { DowntimeCodesConfigComponent } from './downtime-codes-config.component';
import { DowntimeCodesConfigTableHeaderComponent } from './downtime-codes-config-table-header/downtime-codes-config-table-header.component';
import { DowntimeCodesConfigTableConfigResolver } from './downtime-codes-table-config.resolver';


@NgModule({
  declarations: [
    ShiftComponent,
    ShiftTableHeaderComponent,
    DowntimeCodesConfigTableHeaderComponent,
    DowntimeCodesConfigComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    HomeComponentsModule,
    HomeDialogsModule,
    RubikxRoutingModule,
  ],
  providers:[
    ShiftTableConfigResolver,
    DowntimeCodesConfigTableConfigResolver
  ]
})
export class RubikxModule { }
