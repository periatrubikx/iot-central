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
import { DowntimeCodesConfigurationId } from './id/downtime-codes-configuration'
import { AttributeData, CustomerId, TenantId } from './public-api';
import { EntitySearchQuery } from '@shared/models/relation.models';
import { BaseData } from '@shared/models/base-data';

export declare type HasId = EntityId | HasUUID;

export interface DowntimeCodesConfiguration extends BaseData<DowntimeCodesConfigurationId>{
    tenantId?:TenantId;
    customerId?:CustomerId;
    level1:string;
    level2:string;
    level3:string;
    code:number;
}

export interface ImportEntityData{
    level1:string;
    level2:string;
    level3:string;
    code:number;
    credential: {
        accessToken?: string;
        x509?: string;
      };
      attributes: {
        server: AttributeData[],
        shared: AttributeData[]
      };
      timeseries: AttributeData[];
}

export interface DowntimeCodesConfigurationImportEntityData extends ImportEntityData{
    secret: string;
    routingKey: string;
    cloudEndpoint: string;
}


export interface DowntimeCodesConfigurationInfo extends DowntimeCodesConfiguration{
    customerTitle : string;
    customerIsPublic:boolean
}

export interface DowntimeCodesConfigurationSearchQuery extends EntitySearchQuery {
    DowntimeCodesConfigurationType: Array<string>;
}
