import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { Authority } from '@app/shared/models/authority.enum';
import { EntitiesTableComponent } from '../../components/entity/entities-table.component';
import { DowntimeCodesConfigTableConfigResolver } from './downtime-codes-table-config.resolver';

const routes: Routes = [
  {
    path:'downtimeCodesConfiguration',
    component:EntitiesTableComponent,
    data:{
      auth:[Authority.TENANT_ADMIN,Authority.CUSTOMER_USER],
      title:'downtimeCodesConfiguration.downtimeCodesConfiguration',
      downtimeCodesConfigurationsType:'downtimeCodesConfiguration',
      breadcrumb:{
        label:'downtimeCodesConfiguration.downtimeCodesConfiguration',
        icon:'settings_applications'
      }
    },
    resolve:{
      entitiesTableConfig:DowntimeCodesConfigTableConfigResolver
    }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
  providers:[
    DowntimeCodesConfigTableConfigResolver
  ]
})
export class DowntimeCodesConfigRoutingModule { }
