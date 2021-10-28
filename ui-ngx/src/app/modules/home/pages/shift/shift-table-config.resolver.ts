import { Injectable } from '@angular/core';
import {
  Router, Resolve,
  RouterStateSnapshot,
  ActivatedRouteSnapshot
} from '@angular/router';
import { AppState } from '@app/core/core.state';
import { ShiftInfo } from '@app/shared/models/shift.models';
import { EntityType, entityTypeResources,entityTypeTranslations } from '@app/shared/public-api';
import { select, Store } from '@ngrx/store';
import { Observable, of } from 'rxjs';
import { map, mergeMap, take, tap } from 'rxjs/operators';
import { checkBoxCell, DateEntityTableColumn, EntityTableColumn, EntityTableConfig } from '../../models/entity/entities-table-config.models';
import { ShiftComponent } from './shift.component';
import { Authority } from '@app/shared/models/authority.enum';
import { CustomerService } from '@app/core/http/customer.service';
import { selectAuthUser } from '@app/core/auth/auth.selectors';
import { DatePipe } from '@angular/common';
import { Customer } from '@app/shared/models/customer.model';
import { TranslateService } from '@ngx-translate/core';
import { ShiftTableHeaderComponent } from './shift-table-header/shift-table-header.component';
import { ShiftService } from '@app/core/http/shift.service';

@Injectable({
  providedIn: 'root'
})
export class ShiftTableConfigResolver implements Resolve<EntityTableConfig<ShiftInfo>> {

  private readonly config: EntityTableConfig<ShiftInfo> = new EntityTableConfig<ShiftInfo>();



  private customerId:string;

  constructor(private store:Store<AppState>,
                private customerService: CustomerService,
                private translate: TranslateService,
                private datePipe: DatePipe,
                private shiftService : ShiftService
                ){

    this.config.entityType = EntityType.SHIFTS;
    this.config.entityComponent= ShiftComponent;
    this.config.entityTranslations = entityTypeTranslations.get(EntityType.SHIFTS);
    this.config.entityResources = entityTypeResources.get(EntityType.SHIFTS)

    this.config.detailsReadonly = () => (this.config.componentsData.shiftScope === 'customer' || this.config.componentsData.shiftScope === 'customer_user');
    this.config.headerComponent = ShiftTableHeaderComponent
  }


  configureColumns(shiftScope: string): Array<EntityTableColumn<ShiftInfo>> {
    const columns: Array<EntityTableColumn<ShiftInfo>> = [
      new DateEntityTableColumn<ShiftInfo>('createdTime', 'common.created-time', this.datePipe, '150px'),
      new EntityTableColumn<ShiftInfo>('name', 'shift.name', '25%'),
      new EntityTableColumn<ShiftInfo>('areaName', 'shift.areaName', '25%'),
      new EntityTableColumn<ShiftInfo>('startTime', 'shift.startTime', '25%'),
      new EntityTableColumn<ShiftInfo>('endTime','shift.endTime','25%')
    ];
    if (shiftScope === 'tenant') {
      columns.push(
        new EntityTableColumn<ShiftInfo>('customerTitle', 'customer.customer', '25%'),
      );
    }
    return columns;
  }



  resolve(route: ActivatedRouteSnapshot): Observable<EntityTableConfig<ShiftInfo>> {
    const routeParams = route.params;
    this.config.componentsData={
      shiftScope:route.data.shiftsType,
    };
    this.customerId = routeParams.customerId;
    return this.store.pipe(select(selectAuthUser), take(1)).pipe(
      tap((authUser) => {
        if(authUser.authority === Authority.TENANT_ADMIN){
          this.config.componentsData.shiftScope = 'tenant'
        }
        else if (authUser.authority === Authority.CUSTOMER_USER) {
          this.config.componentsData.shiftScope = 'customer_user';
          this.customerId = authUser.customerId;
        }
      }),
      mergeMap(() =>
          this.customerId ? this.customerService.getCustomer(this.customerId) : of(null as Customer)
      ),
      map((parentCustomer) => {
        if (parentCustomer) {
          if (parentCustomer.additionalInfo && parentCustomer.additionalInfo.isPublic) {
            this.config.tableTitle = this.translate.instant('customer.public-shifts');
          } else {
            this.config.tableTitle = parentCustomer.title + ': ' + this.translate.instant('shift.shifts');
          }
        }

        this.config.columns = this.configureColumns(this.config.componentsData.shiftScope);
        this.configureEntityFuncations(this.config.componentsData.shiftScope);

        this.config.addEnabled = !(this.config.componentsData.shiftScope === 'customer_user' || this.config.componentsData.shiftScope === 'customer');
        this.config.entitiesDeleteEnabled = this.config.componentsData.shiftScope === 'tenant';
        this.config.deleteEnabled = () => this.config.componentsData.shiftScope === 'tenant';

        return this.config;
      })
    );
  }

  configureEntityFuncations(shiftScope:string):void{
    if(shiftScope === 'tenant'){
      this.config.entitiesFetchFunction = pageLink =>
      this.shiftService.getTenantShiftInfos(pageLink,this.config.componentsData.shiftsType);
    }
    else{
      this.config.entitiesFetchFunction = pageLink =>
       this.shiftService.getCustomerShiftInfos(this.customerId,pageLink,this.config.componentsData.shiftsType);
    }
  }

}
