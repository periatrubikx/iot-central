import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { Authority } from '@app/shared/public-api';
import { EntitiesTableComponent } from '../../components/entity/entities-table.component';
import { ShiftTableConfigResolver } from './shift-table-config.resolver';

const routes: Routes = [
  {
    path:'shift',
    component:EntitiesTableComponent,
    data:{
      auth:[Authority.TENANT_ADMIN,Authority.CUSTOMER_USER],
      title:'shift.shift',
      shiftsType:'shift',
      breadcrumb:{
        label:'shift.shift',
        icon:'transfer_within_a_station'
      }
    },
    resolve:{
      entitiesTableConfig:ShiftTableConfigResolver
    }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
  providers: [
    ShiftTableConfigResolver
  ]
})
export class ShiftRoutingModule { }
