import { Injectable } from "@angular/core";
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from "@angular/router";
import { AppState } from "@app/core/core.state";
import { CustomerService } from "@app/core/http/customer.service";
import { DownloadCodesConfiguration, DownloadCodesConfigurationInfo } from "@app/shared/models/download-codes-config";
import { EntityType, entityTypeResources, entityTypeTranslations, PageLink } from "@app/shared/public-api";
import { select, Store } from "@ngrx/store";
import { Observable, of } from "rxjs";
import { CellActionDescriptor, DateEntityTableColumn, EntityTableColumn, EntityTableConfig } from "../../models/entity/entities-table-config.models";
import { HomeDialogsService } from '../../dialogs/home-dialogs.service';
import { BroadcastService } from '@app/core/services/broadcast.service';
import { TranslateService } from '@ngx-translate/core';
import { DownloadCodesConfigComponent } from "./download-codes-config/download-codes-config.component";
import { DownloadCodesConfigTableHeaderComponent } from "./download-codes-config-table-header/download-codes-config-table-header.component";
import { DatePipe } from "@angular/common";
import { map, mergeMap, take, tap } from "rxjs/operators";
import { selectAuthUser } from "@app/core/auth/auth.selectors";
import { Authority } from '@app/shared/models/authority.enum';
import { Customer } from '@app/shared/models/customer.model';
import { DownloadCodesConfigurationService } from "@app/core/http/download-codes-configuration.service";

@Injectable({
    providedIn: 'root'
  })
  export class DownloadCodesConfigTableConfigResolver implements Resolve<EntityTableConfig<DownloadCodesConfigurationInfo>> {

    private readonly config : EntityTableConfig<DownloadCodesConfigurationInfo> = new EntityTableConfig<DownloadCodesConfigurationInfo>()

    private customerId : string;

    constructor(private store:Store<AppState>,
                private customerService: CustomerService,
                private translate: TranslateService,
                private datePipe: DatePipe,
                private homeDialogs: HomeDialogsService,
                private broadcast: BroadcastService,
                private downloadCodesConfigurationService : DownloadCodesConfigurationService
                ){

      this.config.entityType = EntityType.DOWNLOAD_CODES_CONFIGURATION;
      this.config.entityComponent = DownloadCodesConfigComponent;
      this.config.entityTranslations = entityTypeTranslations.get(EntityType.DOWNLOAD_CODES_CONFIGURATION);
      this.config.entityResources = entityTypeResources.get(EntityType.DOWNLOAD_CODES_CONFIGURATION);

      this.config.deleteEntityContent=()=> this.translate.instant('downloadCodesConfiguration.delete-download-codes-configuration-text');
      this.config.deleteEntityTitle = count => this.translate.instant('downloadCodesConfiguration.delete-download-codes-configurations-title',{count});
      this.config.deleteEntityTitle = downloadCodesConfiguration => this.translate.instant('downloadCodesConfiguration.delete-download-codes-configuration-title', {downloadCodesConfigurationName: downloadCodesConfiguration.level1});
      this.config.deleteEntitiesContent = () => this.translate.instant('downloadCodesConfiguration.delete-download-codes-configurations-text');
  
      this.config.loadEntity =id => this.downloadCodesConfigurationService.getDownloadCodesConfigurationInfo(id.id);
      this.config.saveEntity = downloadCodesConfig =>{
        return this.downloadCodesConfigurationService.saveDownloadCodesConfiguration(downloadCodesConfig).pipe(
          tap(()=>{
            this.broadcast.broadcast('downloadCodesConfigurationSaved');
          })
        )
      }
      this.config.detailsReadonly = () => (this.config.componentsData.downloadCodesConfigurationScope === 'customer' || this.config.componentsData.downloadCodesConfigurationScope === 'customer_user');
      
      this.config.headerComponent = DownloadCodesConfigTableHeaderComponent;
      }
      

      resolve(route: ActivatedRouteSnapshot): Observable<EntityTableConfig<DownloadCodesConfigurationInfo>> {
        const routeParams = route.params;
        this.config.componentsData={
          downloadCodesConfigurationScope:route.data.downloadCodesConfigurationsType,
          downloadCodesConfigurationType:''
        };
        this.customerId = routeParams.customerId;
        return this.store.pipe(select(selectAuthUser), take(1)).pipe(
          tap((authUser) => {
            if(authUser.authority === Authority.TENANT_ADMIN){
              this.config.componentsData.downloadCodesConfigurationScope = 'tenant'
            }
            else if (authUser.authority === Authority.CUSTOMER_USER) {
              this.config.componentsData.downloadCodesConfigurationScope = 'customer_user';
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
                this.config.tableTitle = parentCustomer.title + ': ' + this.translate.instant('downloadCodesConfiguration.downloadCodesConfigurations');
              }
            }

          this.config.columns = this.configureColumns(this.config.componentsData.downloadCodesConfigurationScope);
          this.configureEntityFuncations(this.config.componentsData.downloadCodesConfigurationScope);
          this.config.cellActionDescriptors= this.configureCellActions(this.config.componentsData.downloadCodesConfigurationScope)
          this.config.addEnabled = !(this.config.componentsData.downloadCodesConfigurationScope === 'customer_user' || this.config.componentsData.downloadCodesConfigurationScope === 'customer');
          this.config.entitiesDeleteEnabled = this.config.componentsData.downloadCodesConfigurationScope === 'tenant';
          this.config.deleteEnabled = () => this.config.componentsData.downloadCodesConfigurationScope === 'tenant';

            return this.config;
          })
        );
      }

    configureColumns(downloadCodesConfigurationScope: string): Array<EntityTableColumn<DownloadCodesConfigurationInfo>> {
      const columns: Array<EntityTableColumn<DownloadCodesConfigurationInfo>> = [
        new DateEntityTableColumn<DownloadCodesConfigurationInfo>('createdTime', 'common.created-time', this.datePipe, '150px'),
        new EntityTableColumn<DownloadCodesConfigurationInfo>('level1', 'downloadCodesConfiguration.level1', '25%'),
        new EntityTableColumn<DownloadCodesConfigurationInfo>('level2', 'downloadCodesConfiguration.level2', '25%'),
        new EntityTableColumn<DownloadCodesConfigurationInfo>('level3', 'downloadCodesConfiguration.level3', '25%'),
        new EntityTableColumn<DownloadCodesConfigurationInfo>('code','downloadCodesConfiguration.code','25%')
      ];
      if (downloadCodesConfigurationScope === 'tenant') {
        columns.push(
          new EntityTableColumn<DownloadCodesConfigurationInfo>('customerTitle', 'customer.customer', '25%'),
        );
      }
      return columns;
    }

    configureEntityFuncations(downloadCodesConfigurationScope:string):void{
      if(downloadCodesConfigurationScope === 'tenant'){
        this.config.entitiesFetchFunction = pageLink =>
        this.downloadCodesConfigurationService.getTenantDownloadCodesConfigurationInfos(pageLink,this.config.componentsData.shiftsType);
        this.config.deleteEntity = id => this.downloadCodesConfigurationService.deleteDownloadCodesConfiguration(id.id);
      }
      // else if(downloadCodesConfigurationScope === 'customer' || downloadCodesConfigurationScope == 'customer_user'){}
      else{
        this.config.entitiesFetchFunction = pageLink =>
         this.downloadCodesConfigurationService.getCustomerDownloadCodesConfigurationInfos(this.customerId,pageLink,this.config.componentsData.shiftsType);
      }
    }

    importShifts($event : Event){
      this.homeDialogs.importEntities(EntityType.DOWNLOAD_CODES_CONFIGURATION).subscribe((res)=>{
        if(res){
          this.broadcast.broadcast('downloadCodesConfigurationSaved');
          this.config.table.updateData();
        }
      })
    }

    configureCellActions(downloadCodesConfigurationScope:string):Array<CellActionDescriptor<DownloadCodesConfigurationInfo>>{
      const actions : Array<CellActionDescriptor<DownloadCodesConfigurationInfo>> = [];
      if(downloadCodesConfigurationScope == 'tenant'){
        actions.push(
          {
            name:this.translate.instant('downloadCodesConfiguration.manage-credentials'),
            icon:'security',
            isEnabled: () => true,
            onAction: ($event, entity) => ''
          }
        )
      }
      if(downloadCodesConfigurationScope == 'customer'){
        actions.push(
          {
            name:this.translate.instant('downloadCodesConfiguration.manage-credentials'),
            icon:'security',
            isEnabled: () => true,
            onAction: ($event, entity) => ''
          }
        )
      }
      if(downloadCodesConfigurationScope == 'customer_user'){
        actions.push(
          {
            name:this.translate.instant('downloadCodesConfiguration.manage-credentials'),
            icon:'security',
            isEnabled: () => true,
            onAction: ($event, entity) => ''
          }
        )
      }
     return actions;
    }
    
  }