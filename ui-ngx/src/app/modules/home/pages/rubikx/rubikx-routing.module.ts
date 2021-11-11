import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { Authority } from '@app/shared/models/authority.enum';
import { ShiftTableConfigResolver } from './shift-table-config.resolver';
import { EntitiesTableComponent } from '../../components/entity/entities-table.component';
import { DowntimeCodesConfigTableConfigResolver } from './downtime-codes-table-config.resolver';
import { DowntimeEntryId } from '@app/shared/models/id/downtime-entry-id';
import { DowntimeEntryConfigResolver } from './downtime-entry-table-config.resolver';

const routes: Routes = [
  {
    path: 'rubikx',
    data: {
      auth: [Authority.TENANT_ADMIN, Authority.CUSTOMER_USER],
      breadcrumb: {
        label: 'rubikx.rubikx-management',
        icon: 'settings'
      }
    },
    children: [
      {
        path: '',
        data: {
          auth: [Authority.TENANT_ADMIN, Authority.CUSTOMER_USER],
          redirectTo: {
            TENANT_ADMIN: '/rubikx/shift',
            CUSTOMER_USER:'/rubikx/shift'
          }
        }
      },
      {
        path: 'shift',
        component: EntitiesTableComponent,
        data: {
          auth: [Authority.TENANT_ADMIN, Authority.CUSTOMER_USER],
          title:'shift.shift',
          shiftsType:'shift',
          breadcrumb:{
              label:'shift.shift',
              icon:'transfer_within_a_station'
          }
        },
        resolve: {
          entitiesTableConfig: ShiftTableConfigResolver
        }
      },
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
      },
      {
        path:'downtimeEntry',
        component:EntitiesTableComponent,
        data:{
          auth:[Authority.TENANT_ADMIN,Authority.CUSTOMER_USER],
          title:'downtimeEntry.downtime-entry',
          downtimeEntrysType:'downtimeEntry',
          breadcrumb:{
            label:'downtimeEntry.downtime-entry',
            icon:'update'
          }
        },
        resolve:{
          entitiesTableConfig:DowntimeEntryConfigResolver
        }
      }
    ]
  }
];

@NgModule({
  declarations:[],
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class RubikxRoutingModule {
}
