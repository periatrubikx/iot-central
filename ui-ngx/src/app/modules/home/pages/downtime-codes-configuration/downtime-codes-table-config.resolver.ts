import { Injectable } from "@angular/core";
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from "@angular/router";
import { AppState } from "@app/core/core.state";
import { CustomerService } from "@app/core/http/customer.service";
import { DowntimeCodesConfiguration, DowntimeCodesConfigurationInfo } from "@app/shared/models/downtime-codes-config";
import { EntityType, entityTypeResources, entityTypeTranslations, PageLink } from "@app/shared/public-api";
import { select, Store } from "@ngrx/store";
import { Observable, of } from "rxjs";
import { CellActionDescriptor, DateEntityTableColumn, EntityTableColumn, EntityTableConfig } from "../../models/entity/entities-table-config.models";
import { HomeDialogsService } from '../../dialogs/home-dialogs.service';
import { BroadcastService } from '@app/core/services/broadcast.service';
import { TranslateService } from '@ngx-translate/core';
import { DowntimeCodesConfigComponent } from "./downtime-codes-config/downtime-codes-config.component";
import { DowntimeCodesConfigTableHeaderComponent } from "./downtime-codes-config-table-header/downtime-codes-config-table-header.component";
import { DatePipe } from "@angular/common";
import { map, mergeMap, take, tap } from "rxjs/operators";
import { selectAuthUser } from "@app/core/auth/auth.selectors";
import { Authority } from '@app/shared/models/authority.enum';
import { Customer } from '@app/shared/models/customer.model';
import { DowntimeCodesConfigurationService } from "@app/core/http/downtime-codes-configuration.service";

@Injectable({
    providedIn: 'root'
  })
  export class DowntimeCodesConfigTableConfigResolver implements Resolve<EntityTableConfig<DowntimeCodesConfigurationInfo>> {

    private readonly config : EntityTableConfig<DowntimeCodesConfigurationInfo> = new EntityTableConfig<DowntimeCodesConfigurationInfo>()

    private customerId : string;

    constructor(private store:Store<AppState>,
                private customerService: CustomerService,
                private translate: TranslateService,
                private datePipe: DatePipe,
                private homeDialogs: HomeDialogsService,
                private broadcast: BroadcastService,
                private downtimeCodesConfigurationService : DowntimeCodesConfigurationService
                ){

      this.config.entityType = EntityType.DOWNTIME_CODES_CONFIGURATION;
      this.config.entityComponent = DowntimeCodesConfigComponent;
      this.config.entityTranslations = entityTypeTranslations.get(EntityType.DOWNTIME_CODES_CONFIGURATION);
      this.config.entityResources = entityTypeResources.get(EntityType.DOWNTIME_CODES_CONFIGURATION);

      this.config.deleteEntityContent=()=> this.translate.instant('downtimeCodesConfiguration.delete-downtime-codes-configuration-text');
      this.config.deleteEntityTitle = count => this.translate.instant('downtimeCodesConfiguration.delete-downtime-codes-configurations-title',{count});
      this.config.deleteEntityTitle = downtimeCodesConfiguration => this.translate.instant('downtimeCodesConfiguration.delete-downtime-codes-configuration-title', {downtimeCodesConfigurationName: downtimeCodesConfiguration.level1});
      this.config.deleteEntitiesContent = () => this.translate.instant('downtimeCodesConfiguration.delete-downtime-codes-configurations-text');

      this.config.loadEntity =id => this.downtimeCodesConfigurationService.getDowntimeCodesConfigurationInfo(id.id);
      this.config.saveEntity = downtimeCodesConfig =>{
        return this.downtimeCodesConfigurationService.saveDowntimeCodesConfiguration(downtimeCodesConfig).pipe(
          tap(()=>{
            this.broadcast.broadcast('downtimeCodesConfigurationSaved');
          })
        )
      }
      this.config.detailsReadonly = () => (this.config.componentsData.downtimeCodesConfigurationScope === 'customer' || this.config.componentsData.downtimeCodesConfigurationScope === 'customer_user');

      this.config.headerComponent = DowntimeCodesConfigTableHeaderComponent;
      }


      resolve(route: ActivatedRouteSnapshot): Observable<EntityTableConfig<DowntimeCodesConfigurationInfo>> {
        const routeParams = route.params;
        this.config.componentsData={
          downtimeCodesConfigurationScope:route.data.downtimeCodesConfigurationsType,
          downtimeCodesConfigurationType:''
        };
        this.customerId = routeParams.customerId;
        return this.store.pipe(select(selectAuthUser), take(1)).pipe(
          tap((authUser) => {
            if(authUser.authority === Authority.TENANT_ADMIN){
              this.config.componentsData.downtimeCodesConfigurationScope = 'tenant'
            }
            else if (authUser.authority === Authority.CUSTOMER_USER) {
              this.config.componentsData.downtimeCodesConfigurationScope = 'customer_user';
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
                this.config.tableTitle = parentCustomer.title + ': ' + this.translate.instant('downtimeCodesConfiguration.downtimeCodesConfigurations');
              }
            }

          this.config.columns = this.configureColumns(this.config.componentsData.downtimeCodesConfigurationScope);
          this.configureEntityFuncations(this.config.componentsData.downtimeCodesConfigurationScope);
          this.config.cellActionDescriptors= this.configureCellActions(this.config.componentsData.downtimeCodesConfigurationScope)
          this.config.addEnabled = !(this.config.componentsData.downtimeCodesConfigurationScope === 'customer_user' || this.config.componentsData.downtimeCodesConfigurationScope === 'customer');
          this.config.entitiesDeleteEnabled = this.config.componentsData.downtimeCodesConfigurationScope === 'tenant';
          this.config.deleteEnabled = () => this.config.componentsData.downtimeCodesConfigurationScope === 'tenant';

            return this.config;
          })
        );
      }

    configureColumns(downtimeCodesConfigurationScope: string): Array<EntityTableColumn<DowntimeCodesConfigurationInfo>> {
      const columns: Array<EntityTableColumn<DowntimeCodesConfigurationInfo>> = [
        new DateEntityTableColumn<DowntimeCodesConfigurationInfo>('createdTime', 'common.created-time', this.datePipe, '150px'),
        new EntityTableColumn<DowntimeCodesConfigurationInfo>('level1', 'downtimeCodesConfiguration.level1', '25%'),
        new EntityTableColumn<DowntimeCodesConfigurationInfo>('level2', 'downtimeCodesConfiguration.level2', '25%'),
        new EntityTableColumn<DowntimeCodesConfigurationInfo>('level3', 'downtimeCodesConfiguration.level3', '25%'),
        new EntityTableColumn<DowntimeCodesConfigurationInfo>('code','downtimeCodesConfiguration.code','25%')
      ];
      if (downtimeCodesConfigurationScope === 'tenant') {
        columns.push(
          new EntityTableColumn<DowntimeCodesConfigurationInfo>('customerTitle', 'customer.customer', '25%'),
        );
      }
      return columns;
    }

    configureEntityFuncations(downtimeCodesConfigurationScope:string):void{
      if(downtimeCodesConfigurationScope === 'tenant'){
        this.config.entitiesFetchFunction = pageLink =>
        this.downtimeCodesConfigurationService.getTenantDowntimeCodesConfigurationInfos(pageLink,this.config.componentsData.shiftsType);
        this.config.deleteEntity = id => this.downtimeCodesConfigurationService.deleteDowntimeCodesConfiguration(id.id);
      }
      // else if(downtimeCodesConfigurationScope === 'customer' || downtimeCodesConfigurationScope == 'customer_user'){}
      else{
        this.config.entitiesFetchFunction = pageLink =>
         this.downtimeCodesConfigurationService.getCustomerDowntimeCodesConfigurationInfos(this.customerId,pageLink,this.config.componentsData.shiftsType);
      }
    }

    importShifts($event : Event){
      this.homeDialogs.importEntities(EntityType.DOWNTIME_CODES_CONFIGURATION).subscribe((res)=>{
        if(res){
          this.broadcast.broadcast('downtimeCodesConfigurationSaved');
          this.config.table.updateData();
        }
      })
    }

    configureCellActions(downtimeCodesConfigurationScope:string):Array<CellActionDescriptor<DowntimeCodesConfigurationInfo>>{
      const actions : Array<CellActionDescriptor<DowntimeCodesConfigurationInfo>> = [];
      if(downtimeCodesConfigurationScope == 'tenant'){
        actions.push(
          {
            name:this.translate.instant('downtimeCodesConfiguration.manage-credentials'),
            icon:'security',
            isEnabled: () => true,
            onAction: ($event, entity) => ''
          }
        )
      }
      if(downtimeCodesConfigurationScope == 'customer'){
        actions.push(
          {
            name:this.translate.instant('downtimeCodesConfiguration.manage-credentials'),
            icon:'security',
            isEnabled: () => true,
            onAction: ($event, entity) => ''
          }
        )
      }
      if(downtimeCodesConfigurationScope == 'customer_user'){
        actions.push(
          {
            name:this.translate.instant('downtimeCodesConfiguration.manage-credentials'),
            icon:'security',
            isEnabled: () => true,
            onAction: ($event, entity) => ''
          }
        )
      }
     return actions;
    }

  }
