///
/// Copyright © 2016-2021 The Thingsboard Authors
///
/// Licensed under the Apache License, Version 2.0 (the "License");
/// you may not use this file except in compliance with the License.
/// You may obtain a copy of the License at
///
///     http://www.apache.org/licenses/LICENSE-2.0
///
/// Unless required by applicable law or agreed to in writing, software
/// distributed under the License is distributed on an "AS IS" BASIS,
/// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
/// See the License for the specific language governing permissions and
/// limitations under the License.
///

import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {Authority} from '@shared/models/authority.enum';
import {DigitalTwinComponent} from '@home/pages/digital-twin/digital-twin.component';

const routes: Routes = [
  {
    path: 'digital-twin',
    component: DigitalTwinComponent,
    data: {
      auth: [Authority.TENANT_ADMIN, Authority.CUSTOMER_USER],
      title: 'digital-twin.digital-twin',
      breadcrumb: {
        label: 'digital-twin.digital-twin',
        icon: 'settings_ethernet'
      }
    }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class DigitalTwinRoutingModule { }
