import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ShiftInfo } from '@app/shared/models/shift.models';
import { PageData, PageLink } from '@app/shared/public-api';
import { Observable } from 'rxjs';
import { defaultHttpOptionsFromConfig, RequestConfig } from './http-utils';
import { EntitySubtype } from '@app/shared/models/entity-type.models';

@Injectable({
  providedIn: 'root'
})
export class ShiftService {

  constructor(
    private http: HttpClient
  ) { }

  public getShiftInfos(pageLink:PageLink,type:string='',config?:RequestConfig):Observable<PageData<ShiftInfo>>{
    return this.http.get<PageData<ShiftInfo>>(`/api/shift/shiftInfos${pageLink.toQuery()}&type=${type}`,
      defaultHttpOptionsFromConfig(config))
  }


  public getShiftArea(config?:RequestConfig):Observable<Array<EntitySubtype>>{
    return this.http.get<Array<EntitySubtype>>('api/areaInfos',defaultHttpOptionsFromConfig(config));
  }

  public getCustomerShiftInfos(customerId:string,pageLink:PageLink,type:string='',
                               config?:RequestConfig):Observable<PageData<ShiftInfo>>{
    return this.http.get<PageData<ShiftInfo>>(`api/cutomer/${customerId}/shiftInfos${pageLink.toQuery()}&type=${type}`,
      defaultHttpOptionsFromConfig(config))
  }
}
