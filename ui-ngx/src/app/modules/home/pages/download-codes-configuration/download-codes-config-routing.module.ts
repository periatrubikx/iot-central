import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { Authority } from '@app/shared/models/authority.enum';
import { EntitiesTableComponent } from '../../components/entity/entities-table.component';
import { DownloadCodesConfigTableConfigResolver } from './download-codes-table-config.resolver';

const routes: Routes = [
  {
    path:'downloadCodesConfiguration',
    component:EntitiesTableComponent,
    data:{
      auth:[Authority.TENANT_ADMIN,Authority.CUSTOMER_USER],
      title:'downloadCodesConfiguration.downloadCodesConfiguration',
      downloadCodesConfigurationsType:'downloadCodesConfiguration',
      breadcrumb:{
        label:'downloadCodesConfiguration.downloadCodesConfiguration',
        icon:'settings_applications'
      }
    },
    resolve:{
      entitiesTableConfig:DownloadCodesConfigTableConfigResolver
    }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
  providers:[
    DownloadCodesConfigTableConfigResolver
  ]
})
export class DownloadCodesConfigRoutingModule { }
