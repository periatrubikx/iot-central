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
import { forkJoin, Observable, of } from 'rxjs';
import { map, mergeMap, take, tap } from 'rxjs/operators';
import { CellActionDescriptor, checkBoxCell, DateEntityTableColumn, EntityTableColumn, EntityTableConfig } from '../../models/entity/entities-table-config.models';
import { Authority } from '@app/shared/models/authority.enum';
import { CustomerService } from '@app/core/http/customer.service';
import { selectAuthUser } from '@app/core/auth/auth.selectors';
import { DatePipe } from '@angular/common';
import { Customer } from '@app/shared/models/customer.model';
import { TranslateService } from '@ngx-translate/core';
import { ShiftTableHeaderComponent } from './shift-table-header/shift-table-header.component';
import { ShiftService } from '@app/core/http/shift.service';
import { HomeDialogsService } from '../../dialogs/home-dialogs.service';
import { BroadcastService } from '@app/core/services/broadcast.service';
import { ShiftComponent } from './shift.component';
import { ShiftId } from '@app/shared/models/id/shift-id';
import { AssignToCustomerDialogComponent, AssignToCustomerDialogData } from '../../dialogs/assign-to-customer-dialog.component';
import { MatDialog } from '@angular/material/dialog';
import { NULL_UUID } from '@shared/models/id/has-uuid';
import { EntityAction } from '../../models/entity/entity-component.models';
import { DialogService } from '@app/core/services/dialog.service';

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
                private homeDialogs: HomeDialogsService,
                private broadcast: BroadcastService,
                private shiftService : ShiftService,
                private dialogService: DialogService,
                private dialog: MatDialog
                ){

    this.config.entityType = EntityType.SHIFTS;
    this.config.entityComponent= ShiftComponent;
    this.config.entityTranslations = entityTypeTranslations.get(EntityType.SHIFTS);
    this.config.entityResources = entityTypeResources.get(EntityType.SHIFTS);

    this.config.deleteEntityContent=()=> this.translate.instant('shift.delete-shift-text');
    this.config.deleteEntityTitle = count => this.translate.instant('shift.delete-shifts-title',{count});
    this.config.deleteEntityTitle = shift => this.translate.instant('shift.delete-shift-title', {shiftName: shift.name});
    this.config.deleteEntitiesContent = () => this.translate.instant('shift.delete-shifts-text');

    this.config.loadEntity =id => this.shiftService.getShiftInfo(id.id);
    this.config.saveEntity = shift =>{
      return this.shiftService.saveShift(shift).pipe(
        tap(()=>{
          this.broadcast.broadcast('shiftSaved');
        })
      )
    }
    this.config.onEntityAction = action => this.onAssetAction(action);
    this.config.detailsReadonly = () => (this.config.componentsData.shiftScope === 'customer' || this.config.componentsData.shiftScope === 'customer_user');
    this.config.headerComponent = ShiftTableHeaderComponent;
  }


  configureColumns(shiftScope: string): Array<EntityTableColumn<ShiftInfo>> {
    const columns: Array<EntityTableColumn<ShiftInfo>> = [
      new DateEntityTableColumn<ShiftInfo>('createdTime', 'common.created-time', this.datePipe, '150px'),
      new EntityTableColumn<ShiftInfo>('name', 'shift.name', '25%'),
      new EntityTableColumn<ShiftInfo>('areaName', 'shift.areaName', '25%'),
      new EntityTableColumn<ShiftInfo>('startTimeMs', 'shift.startTime', '25%'),
      new EntityTableColumn<ShiftInfo>('endTimeMs','shift.endTime','25%')
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
      shiftArea:''
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
        this.config.cellActionDescriptors= this.configureCellActions(this.config.componentsData.shiftScope)
        this.config.addEnabled = !(this.config.componentsData.shiftScope === 'customer_user' || this.config.componentsData.shiftScope === 'customer');
        this.config.entitiesDeleteEnabled = this.config.componentsData.shiftScope === 'tenant';
        this.config.deleteEnabled = () => this.config.componentsData.shiftScope === 'tenant';
        return this.config;
      })
    );
  }

  importShifts($event : Event){
    this.homeDialogs.importEntities(EntityType.SHIFTS).subscribe((res)=>{
      if(res){
        this.broadcast.broadcast('shiftSaved');
        this.config.table.updateData();
      }
    })
  }

  configureEntityFuncations(shiftScope:string):void{
    
    if(shiftScope === 'tenant'){
      this.config.entitiesFetchFunction = pageLink =>
      this.shiftService.getTenantShiftInfos(pageLink,this.config.componentsData.shiftsType);
      this.config.deleteEntity = id => this.shiftService.deleteShift(id.id);
    }
    // else if(shiftScope === 'customer' || shiftScope == 'customer_user'){}
    else{
      this.config.entitiesFetchFunction = pageLink =>
       this.shiftService.getCustomerShiftInfos(this.customerId,pageLink,this.config.componentsData.shiftsType);
      this.config.deleteEntity = id => this.shiftService.unassignShiftFromCustomer(id.id);
    }
  }

  configureCellActions(shiftScope:string):Array<CellActionDescriptor<ShiftInfo>>{
    const actions : Array<CellActionDescriptor<ShiftInfo>> = [];
    if(shiftScope == 'tenant'){
      actions.push(
        {
          name:this.translate.instant('device.manage-credentials'),
          icon:'security',
          isEnabled: () => true,
          onAction: ($event, entity) => ''
        },
        {
          name: this.translate.instant('shift.assign-to-customer'),
          icon: 'assignment_ind',
          isEnabled: (entity) => (!entity.customerId || entity.customerId.id === NULL_UUID),
          onAction: ($event, entity) => this.assignToCustomer($event, [entity.id])
        },
        {
          name: this.translate.instant('shift.unassign-from-customer'),
          icon: 'assignment_return',
          isEnabled: (entity) => (entity.customerId && entity.customerId.id !== NULL_UUID && !entity.customerIsPublic),
          onAction: ($event, entity) => this.unassignFromCustomer($event, entity)
        },
      )
    }
    if(shiftScope == 'customer'){
      actions.push(
        {
          name:this.translate.instant('device.manage-credentials'),
          icon:'security',
          isEnabled: () => true,
          onAction: ($event, entity) => ''
        },
        {
          name: this.translate.instant('shift.assign-to-customer'),
          icon: 'assignment_ind',
          isEnabled: (entity) => (!entity.customerId || entity.customerId.id === NULL_UUID),
          onAction: ($event, entity) => this.assignToCustomer($event, [entity.id])
        },
        {
          name: this.translate.instant('shift.unassign-from-customer'),
          icon: 'assignment_return',
          isEnabled: (entity) => (entity.customerId && entity.customerId.id !== NULL_UUID && !entity.customerIsPublic),
          onAction: ($event, entity) => this.unassignFromCustomer($event, entity)
        },
      )
    }
    if(shiftScope == 'customer_user'){
      actions.push(
        {
          name:this.translate.instant('device.manage-credentials'),
          icon:'security',
          isEnabled: () => true,
          onAction: ($event, entity) => ''
        },
        {
          name: this.translate.instant('asset.assign-to-customer'),
          icon: 'assignment_ind',
          isEnabled: (entity) => (!entity.customerId || entity.customerId.id === NULL_UUID),
          onAction: ($event, entity) => this.assignToCustomer($event, [entity.id])
        },
        {
          name: this.translate.instant('shift.unassign-from-customer'),
          icon: 'assignment_return',
          isEnabled: (entity) => (entity.customerId && entity.customerId.id !== NULL_UUID && !entity.customerIsPublic),
          onAction: ($event, entity) => this.unassignFromCustomer($event, entity)
        },
      )
    }
   return actions;
  }

  onAssetAction(action: EntityAction<ShiftInfo>): boolean {
    switch (action.action) {
      case 'assignToCustomer':
        this.assignToCustomer(action.event, [action.entity.id]);
        return true;
      // case 'unassignFromCustomer':
      //   this.unassignFromCustomer(action.event, action.entity);
      //   return true;
    }
    return false;
  }

  assignToCustomer($event: Event, shiftIds: Array<ShiftId>) {
    if ($event) {
      $event.stopPropagation();
    }
    this.dialog.open<AssignToCustomerDialogComponent, AssignToCustomerDialogData,
      boolean>(AssignToCustomerDialogComponent, {
      disableClose: true,
      panelClass: ['tb-dialog', 'tb-fullscreen-dialog'],
      data: {
        entityIds: shiftIds,
        entityType: EntityType.SHIFTS
      }
    }).afterClosed()
      .subscribe((res) => {
        if (res) {
          this.config.table.updateData();
        }
      });
  }


  unassignFromCustomer($event: Event, shift: ShiftInfo) {
    if ($event) {
      $event.stopPropagation();
    }
    const isPublic = shift.customerIsPublic;
    let title;
    let content;
    if (isPublic) {
      content = this.translate.instant('shift.make-private-asset-text');
      title = this.translate.instant('shift.make-private-asset-title', {shiftName: shift.name});
    } else {
      content = this.translate.instant('shift.unassign-shift-text');
      title = this.translate.instant('shift.unassign-shift-title', {shiftName: shift.name});
    }
    this.dialogService.confirm(
      title,
      content,
      this.translate.instant('action.no'),
      this.translate.instant('action.yes'),
      true
    ).subscribe((res) => {
        if (res) {
          this.shiftService.unassignShiftFromCustomer(shift.id.id).subscribe(
            () => {
              this.config.table.updateData();
            }
          );
        }
      }
    );
  }


  unassignShiftsFromCustomer($event: Event, shifts: Array<ShiftInfo>) {
    if ($event) {
      $event.stopPropagation();
    }
    this.dialogService.confirm(
      this.translate.instant('shift.unassign-shifts-title', {count: shifts.length}),
      this.translate.instant('shift.unassign-shifts-text'),
      this.translate.instant('shift.no'),
      this.translate.instant('shift.yes'),
      true
    ).subscribe((res) => {
        if (res) {
          const tasks: Observable<any>[] = [];
          shifts.forEach(
            (shift) => {
              tasks.push(this.shiftService.unassignShiftFromCustomer(shift.id.id));
            }
          );
          forkJoin(tasks).subscribe(
            () => {
              this.config.table.updateData();
            }
          );
        }
      }
    );
  }

}
