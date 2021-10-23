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


import { EntityId } from '@shared/models/id/entity-id';
import { HasUUID } from '@shared/models/id/has-uuid';
import { ShiftId } from './id/shift-id';
import { CustomerId, TenantId } from './public-api';
import { EntitySearchQuery } from '@shared/models/relation.models';


export declare type HasId = EntityId | HasUUID;

export interface BaseData<T extends HasId> {
  createdTime?: number;
  id?: T;
  name?: string;
  label?: string;
}

export interface Shift extends BaseData<ShiftId>{
    tenantId:TenantId;
    customerId:CustomerId;
    name:string;
    areaName:string;
    startTime:string;
    endTime:string;
    additionalInfo?:any;
}



export interface ShiftInfo extends Shift{ 
    customerTitle : string;
    customerIsPublic:boolean
}


export interface ShiftSearchQuery extends EntitySearchQuery {
  shiftTypes: Array<string>;
}