import { DatePipe } from "@angular/common";
import { Injectable } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from "@angular/router";
import { AppState } from "@app/core/core.state";
import { CustomerService } from "@app/core/http/customer.service";
import { DowntimeEntryService } from "@app/core/http/downtime-entry.service";
import { BroadcastService } from "@app/core/services/broadcast.service";
import { DialogService } from "@app/core/services/dialog.service";
import { DowntimeEntryInfo } from "@app/shared/models/downtime-entry.models";
import { EntityType, entityTypeResources, entityTypeTranslations, PageLink } from "@app/shared/public-api";
import { select, Store } from "@ngrx/store";
import { TranslateService } from '@ngx-translate/core';
import { Observable, of } from "rxjs";
import { map, mergeMap, take, tap } from "rxjs/operators";
import { HomeDialogsService } from "../../dialogs/home-dialogs.service";
import { DateEntityTableColumn, EntityTableColumn, EntityTableConfig } from "../../models/entity/entities-table-config.models";
import { DowntimeEntryTableHeaderComponent } from "./downtime-entry-table-header/downtime-entry-table-header.component";
import { DowntimeEntryComponent } from "./downtime-entry.component";
import { selectAuthUser } from '@app/core/auth/auth.selectors';
import { Authority } from '@app/shared/models/authority.enum';
import { Customer } from '@app/shared/models/customer.model';

@Injectable({
    providedIn: 'root'
  })
export class DowntimeEntryConfigResolver implements Resolve<EntityTableConfig<DowntimeEntryInfo>> {

    private readonly config : EntityTableConfig<DowntimeEntryInfo> = new EntityTableConfig<DowntimeEntryInfo>();
    
    private customerId : string;

    constructor(private store:Store<AppState>,
        private downtimeEntryService : DowntimeEntryService,
        private customerService: CustomerService,
        private translate: TranslateService,
        private datePipe: DatePipe,
        private homeDialogs: HomeDialogsService,
        private broadcast: BroadcastService,
        private dialogService: DialogService, 
        private dialog: MatDialog
        ){

    this.config.entityType = EntityType.DOWNTIME_ENTRY;
    this.config.entityComponent = DowntimeEntryComponent;
    this.config.entityTranslations = entityTypeTranslations.get(EntityType.DOWNTIME_ENTRY);
    this.config.entityResources = entityTypeResources.get(EntityType.DOWNTIME_ENTRY);

    this.config.deleteEntityContent=()=> this.translate.instant('downtimeEntry.delete-downtime-entry-text');
    this.config.deleteEntityTitle = count => this.translate.instant('downtimeEntry.delete-downtime-entrys-title',{count});
    this.config.deleteEntityTitle = downtimeEntry => this.translate.instant('downtimeEntry.delete-downtime-entry-title', {downtimeEntryName: downtimeEntry.name});
    this.config.deleteEntitiesContent = () => this.translate.instant('downtimeEntry.delete-downtime-entrys-text');

    this.config.loadEntity =id => this.downtimeEntryService.getDowntimeEntryInfo(id.id);
    this.config.saveEntity = downtimeEntry =>{
      return this.downtimeEntryService.saveDowntimeEntry(downtimeEntry).pipe(
        tap(()=>{
          this.broadcast.broadcast('downtimeEntrySaved');
        })
      )
    }
    // this.config.onEntityAction = action => this.onAssetAction(action);
    this.config.detailsReadonly = () => (this.config.componentsData.downtimeEntryScope === 'customer' || this.config.componentsData.downtimeEntryScope === 'customer_user');
    this.config.headerComponent = DowntimeEntryTableHeaderComponent;
    }

    configureColumns(downtimeEntryScope: string): Array<EntityTableColumn<DowntimeEntryInfo>> {
        const columns: Array<EntityTableColumn<DowntimeEntryInfo>> = [
          new DateEntityTableColumn<DowntimeEntryInfo>('createdTime', 'common.created-time', this.datePipe, '150px'),
          new EntityTableColumn<DowntimeEntryInfo>('asset', 'downtimeEntry.asset', '25%'),
          new EntityTableColumn<DowntimeEntryInfo>('device', 'downtimeEntry.device', '25%'),
          new EntityTableColumn<DowntimeEntryInfo>('startDateTimeMs', 'downtimeEntry.startDateTime', '25%'),
          new EntityTableColumn<DowntimeEntryInfo>('endDateTimeMs','downtimeEntry.endDateTime','25%'),
          new EntityTableColumn<DowntimeEntryInfo>('name','downtimeEntry.reason','25%')
        ];
        if (downtimeEntryScope === 'tenant') {
          columns.push(
            new EntityTableColumn<DowntimeEntryInfo>('customerTitle', 'customer.customer', '25%'),
          );
        }
        return columns;
      }


    resolve(route: ActivatedRouteSnapshot): Observable<EntityTableConfig<DowntimeEntryInfo>> {
        const routeParams = route.params;
        this.config.componentsData={
            downtimeEntryScope:route.data.downtimeEntrysType,
        };
    this.customerId = routeParams.customerId;
    return this.store.pipe(select(selectAuthUser), take(1)).pipe(
        tap((authUser) => {
          if(authUser.authority === Authority.TENANT_ADMIN){
            this.config.componentsData.downtimeEntryScope = 'tenant'
          }
          else if (authUser.authority === Authority.CUSTOMER_USER) {
            this.config.componentsData.downtimeEntryScope = 'customer_user';
            this.customerId = authUser.customerId;
          }
        }),
        mergeMap(() =>
            this.customerId ? this.customerService.getCustomer(this.customerId) : of(null as Customer)
        ),
        map((parentCustomer) => {
          if (parentCustomer) {
            if (parentCustomer.additionalInfo && parentCustomer.additionalInfo.isPublic) {
              this.config.tableTitle = this.translate.instant('customer.public-downtime-entrys');
            } else {
              this.config.tableTitle = parentCustomer.title + ': ' + this.translate.instant('downtimeEntry.downtime-entrys');
            }
          }

          this.config.columns = this.configureColumns(this.config.componentsData.downtimeEntryScope);
          this.configureEntityFuncations(this.config.componentsData.downtimeEntryScope);
        //   this.config.cellActionDescriptors= this.configureCellActions(this.config.componentsData.downtimeEntryScope)
          this.config.addEnabled = !(this.config.componentsData.downtimeEntryScope === 'customer_user' || this.config.componentsData.downtimeEntryScope === 'customer');
          this.config.entitiesDeleteEnabled = this.config.componentsData.downtimeEntryScope === 'tenant';
          this.config.deleteEnabled = () => this.config.componentsData.downtimeEntryScope === 'tenant';
        return this.config;
    
        })
        );
    }

    importDowntimeEntrys($event : Event){
        this.homeDialogs.importEntities(EntityType.DOWNTIME_ENTRY).subscribe((res)=>{
          if(res){
            this.broadcast.broadcast('downtimeEntrySaved');
            this.config.table.updateData();
          }
        })
    }

    configureEntityFuncations(downtimeEntryScope:string):void{
    
        if(downtimeEntryScope === 'tenant'){
          this.config.entitiesFetchFunction = pageLink =>
          this.downtimeEntryService.getTenantDowntimeEntryInfos(pageLink,this.config.componentsData.downtimeEntrysType);
          this.config.deleteEntity = id => this.downtimeEntryService.deleteDowntimeEntry(id.id);
        }
        // else if(downtimeEntryScope === 'customer' || downtimeEntryScope == 'customer_user'){}
        else{
          this.config.entitiesFetchFunction = pageLink =>
           this.downtimeEntryService.getCustomerDowntimeEntryInfos(this.customerId,pageLink,this.config.componentsData.downtimeEntrysType);
        //   this.config.deleteEntity = id => this.downtimeEntryService.unassignShiftFromCustomer(id.id);
        }
      }
    
    
}