import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ShiftRoutingModule } from './shift-routing.module';
import { SharedModule } from '@app/shared/shared.module';
import { HomeComponentsModule } from '../../components/home-components.module';
import { HomeDialogsModule } from '../../dialogs/home-dialogs.module';
import { ShiftComponent } from './shift.component';
import { ShiftTableHeaderComponent } from './shift-table-header/shift-table-header.component';


@NgModule({
  declarations: [
    ShiftComponent,
    ShiftTableHeaderComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    HomeComponentsModule,
    HomeDialogsModule,
    ShiftRoutingModule,
  ]
})
export class ShiftModule { }
