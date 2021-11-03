import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { DownloadCodesConfiguration, DownloadCodesConfigurationInfo } from '@app/shared/models/download-codes-config';
import { PageData, PageLink } from '@app/shared/public-api';
import { Observable } from 'rxjs';
import { defaultHttpOptionsFromConfig, RequestConfig } from './http-utils';
import { EntitySubtype } from '@app/shared/models/entity-type.models';

@Injectable({
  providedIn: 'root'
})
export class DownloadCodesConfigurationService {

  constructor(
    private http: HttpClient
  ) { }

  public getTenantDownloadCodesConfigurationInfos(pageLink:PageLink,type:string='',config?:RequestConfig):Observable<PageData<DownloadCodesConfigurationInfo>>{
    return this.http.get<PageData<DownloadCodesConfigurationInfo>>(`/api/downloadCodesConfig/downloadCodesConfigInfos${pageLink.toQuery()}&type=${type}`,
    defaultHttpOptionsFromConfig(config))
  }

  public getDownloadCodesConfigurationType(config?:RequestConfig):Observable<Array<EntitySubtype>>{
    return this.http.get<Array<EntitySubtype>>('/api/downloadCodesConfig/type', defaultHttpOptionsFromConfig(config));
  }

  public getCustomerDownloadCodesConfigurationInfos(customerId:string,pageLink:PageLink,type:string='',
          config?:RequestConfig):Observable<PageData<DownloadCodesConfigurationInfo>>{
      return this.http.get<PageData<DownloadCodesConfigurationInfo>>(`api/downloadCodesConfig/${customerId}/downloadCodesConfigInfos${pageLink.toQuery()}&type=${type}`,
              defaultHttpOptionsFromConfig(config))
  }

  public saveDownloadCodesConfiguration(downloadCodesConfig: DownloadCodesConfiguration, config?: RequestConfig): Observable<DownloadCodesConfigurationInfo> {
    return this.http.post<DownloadCodesConfigurationInfo>('/api/downloadCodesConfig', downloadCodesConfig, defaultHttpOptionsFromConfig(config));
  }


  public getDownloadCodesConfigurationInfo(downloadCodesConfigId: string, config?: RequestConfig): Observable<DownloadCodesConfigurationInfo> {
    return this.http.get<DownloadCodesConfigurationInfo>(`/api/downloadCodesConfig/info/${downloadCodesConfigId}`, defaultHttpOptionsFromConfig(config));
  }

  public deleteDownloadCodesConfiguration(downloadCodesConfigId: string, config?: RequestConfig) {
    return this.http.delete(`/api/downloadCodesConfig/${downloadCodesConfigId}`, defaultHttpOptionsFromConfig(config));
  }


}
