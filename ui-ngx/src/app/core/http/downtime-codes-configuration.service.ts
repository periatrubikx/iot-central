import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { DowntimeCodesConfiguration, DowntimeCodesConfigurationInfo } from '@app/shared/models/downtime-codes-config';
import { PageData, PageLink } from '@app/shared/public-api';
import { Observable } from 'rxjs';
import { defaultHttpOptionsFromConfig, RequestConfig } from './http-utils';
import { EntitySubtype } from '@app/shared/models/entity-type.models';

@Injectable({
  providedIn: 'root'
})
export class DowntimeCodesConfigurationService {

  constructor(
    private http: HttpClient
  ) { }

  public getTenantDowntimeCodesConfigurationInfos(pageLink:PageLink,type:string='',config?:RequestConfig):Observable<PageData<DowntimeCodesConfigurationInfo>>{
    return this.http.get<PageData<DowntimeCodesConfigurationInfo>>(`/api/downtimeCodesConfig/downtimeCodesConfigInfos${pageLink.toQuery()}&type=${type}`,
    defaultHttpOptionsFromConfig(config))
  }

  public getDowntimeCodesConfigurationType(config?:RequestConfig):Observable<Array<EntitySubtype>>{
    return this.http.get<Array<EntitySubtype>>('/api/downtimeCodesConfig/type', defaultHttpOptionsFromConfig(config));
  }

  public getCustomerDowntimeCodesConfigurationInfos(customerId:string,pageLink:PageLink,type:string='',
          config?:RequestConfig):Observable<PageData<DowntimeCodesConfigurationInfo>>{
      return this.http.get<PageData<DowntimeCodesConfigurationInfo>>(`api/downtimeCodesConfig/${customerId}/downtimeCodesConfigInfos${pageLink.toQuery()}&type=${type}`,
              defaultHttpOptionsFromConfig(config))
  }

  public saveDowntimeCodesConfiguration(downtimeCodesConfig: DowntimeCodesConfiguration, config?: RequestConfig): Observable<DowntimeCodesConfigurationInfo> {
    return this.http.post<DowntimeCodesConfigurationInfo>('/api/downtimeCodesConfig', downtimeCodesConfig, defaultHttpOptionsFromConfig(config));
  }


  public getDowntimeCodesConfigurationInfo(downtimeCodesConfigId: string, config?: RequestConfig): Observable<DowntimeCodesConfigurationInfo> {
    return this.http.get<DowntimeCodesConfigurationInfo>(`/api/downtimeCodesConfig/info/${downtimeCodesConfigId}`, defaultHttpOptionsFromConfig(config));
  }

  public deleteDowntimeCodesConfiguration(downtimeCodesConfigId: string, config?: RequestConfig) {
    return this.http.delete(`/api/downtimeCodesConfig/${downtimeCodesConfigId}`, defaultHttpOptionsFromConfig(config));
  }


}
