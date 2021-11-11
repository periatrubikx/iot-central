import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { PageData, PageLink } from '@app/shared/public-api';
import { defaultHttpOptionsFromConfig, RequestConfig } from './http-utils';
import { EntitySubtype } from '@app/shared/models/entity-type.models';
import { Observable } from 'rxjs';
import { DowntimeEntry, DowntimeEntryInfo } from '@app/shared/models/downtime-entry.models';

@Injectable({
  providedIn: 'root'
})
export class DowntimeEntryService {

  constructor(private http: HttpClient) { }

  public getTenantDowntimeEntryInfos(pageLink:PageLink,type:string='',config?:RequestConfig):Observable<PageData<DowntimeEntryInfo>>{
    return this.http.get<PageData<DowntimeEntryInfo>>(`/api/downtimeEntry/downtimeEntryInfos${pageLink.toQuery()}&type=${type}`,
    defaultHttpOptionsFromConfig(config))
  }


  public getDowntimeEntryAreas(config?:RequestConfig):Observable<Array<EntitySubtype>>{
    return this.http.get<Array<EntitySubtype>>('/api/downtimeEntry/areaInfos', defaultHttpOptionsFromConfig(config));
  }

  public getCustomerDowntimeEntryInfos(customerId:string,pageLink:PageLink,type:string='',
          config?:RequestConfig):Observable<PageData<DowntimeEntryInfo>>{
    return this.http.get<PageData<DowntimeEntryInfo>>(`api/cutomer/${customerId}/downtimeEntryInfos${pageLink.toQuery()}&type=${type}`,
    defaultHttpOptionsFromConfig(config))
  }

  public saveDowntimeEntry(downtimeEntry: DowntimeEntry, config?: RequestConfig): Observable<DowntimeEntryInfo> {
    return this.http.post<DowntimeEntryInfo>('/api/downtimeEntry', downtimeEntry, defaultHttpOptionsFromConfig(config));
  }


  public getDowntimeEntryInfo(downtimeEntryId: string, config?: RequestConfig): Observable<DowntimeEntryInfo> {
    return this.http.get<DowntimeEntryInfo>(`/api/downtimeEntry/info/${downtimeEntryId}`, defaultHttpOptionsFromConfig(config));
  }

  public deleteDowntimeEntry(downtimeEntryId: string, config?: RequestConfig) {
    return this.http.delete(`/api/downtimeEntry/${downtimeEntryId}`, defaultHttpOptionsFromConfig(config));
  }
}
