import { ChangeDetectorRef, Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AppState } from '@app/core/core.state';
import { AssetService, DeviceService, EntityRelationService, RequestConfig } from '@app/core/public-api';
import { EntityComponent } from '@app/modules/home/components/entity/entity.component';
import { EntityTableConfig } from '@app/modules/home/models/entity/entities-table-config.models';
import { DowntimeEntryInfo } from '@app/shared/models/downtime-entry.models';
import { EntityType } from '@app/shared/models/entity-type.models';
import { PageLink } from '@app/shared/public-api';
import { Store } from '@ngrx/store';
import { Observable, of } from 'rxjs';
import { BaseData } from '@shared/models/base-data';
import { EntityId } from '@shared/models/id/entity-id';
import { map } from 'rxjs/operators';
import { DowntimeCodesConfigurationService } from '@app/core/http/downtime-codes-configuration.service';

@Component({
  selector: 'tb-downtime-entry',
  templateUrl: './downtime-entry.component.html',
  styleUrls: ['./downtime-entry.component.scss']
})
export class DowntimeEntryComponent extends EntityComponent<DowntimeEntryInfo> {

  entityType = EntityType;
  assetsIds = [];
  deviceIds = [];
  downtimeCodeConfigIds = []
  entitiesObservable: any;
  downtimeEntryScope: 'tenant' | 'customer' | 'customer_user' ;

  maxStartDateTimeMs: Observable<number | null>;
  minEndDateTimeMs: Observable<number | null>;

  constructor(
    protected store: Store<AppState>,
    @Inject('entity') protected entityValue: DowntimeEntryInfo,
    @Inject('entitiesTableConfig') protected entitiesTableConfigValue: EntityTableConfig<DowntimeEntryInfo>,
    public fb: FormBuilder,
    private assetService:AssetService,
    private deviceService : DeviceService,
    private downtimeCodeConfigService : DowntimeCodesConfigurationService,
    private entityRelationService : EntityRelationService,
    protected cd: ChangeDetectorRef
  ){
    super(store, fb, entityValue, entitiesTableConfigValue, cd);
  }

  ngOnInit(): void {
    const pageLink = new PageLink(10);
    this.getEntitiesByPageLinkAssetObservable(pageLink);
    this.getEntitiesByPageLinkDowntimeCodeConfigObservable(pageLink);
    this.downtimeEntryScope = this.entitiesTableConfigValue.componentsData.downtimeEntryScope;
    this.maxStartDateTimeMs = this.entityForm.get('startDateTimeMs').valueChanges;
    this.minEndDateTimeMs = this.entityForm.get('endDateTimeMs').valueChanges;
    super.ngOnInit();
  }

  getEntitiesByPageLinkAssetObservable(pageLink: PageLink, subType: string = '',
                      config?: RequestConfig): Observable<Array<BaseData<EntityId>>> {
    this.assetsIds = [];
    this.entitiesObservable =
          this.assetService.getTenantAssetInfos(pageLink,subType,config)
    if (this.entitiesObservable) {
      this.entitiesObservable.pipe(
            map((data) => {return data && data['data'].length ? this.assetsIds = data['data'] : null})).subscribe();
        } else {
          return of(null);
    }
  }

  AssetsChangeEvent(event){
    this.deviceIds = [];
   this.entitiesObservable = this.entityRelationService.findInfoByFrom(event.value)
    if (this.entitiesObservable) {
       this.entitiesObservable.pipe(
          map((data:Array<[]>) => {
            return data && data.length ? this.deviceIds = data : null})).subscribe();
        } else {
          return of(null);
    }
  }

  DeviceChangeEvent(event){
    if(event && this.deviceIds.length == 0){
    const pageLink = new PageLink(10);
    this.getEntitiesByPageLinkDeviceObservable(pageLink);
    }
  }

  DowntimeEntryChangeEvent(event){
    let name = "";
    if(event.level1) name = event.level1;
    if(event.level2) name = event.level2;
    if(event.level3) name= event.level3;
    this.entity.name = name;
  }

  getEntitiesByPageLinkDeviceObservable(pageLink: PageLink, subType: string = '',
                      config?: RequestConfig): Observable<Array<BaseData<EntityId>>> {
    this.deviceIds = [];
    this.entitiesObservable =
          this.deviceService.getTenantDeviceInfos(pageLink,subType,config)
    if (this.entitiesObservable) {
       this.entitiesObservable.pipe(
          map((data) => {return data && data['data'].length ? this.deviceIds = data['data'] : null})).subscribe();
        } else {
          return of(null);
    }
  }

  getEntitiesByPageLinkDowntimeCodeConfigObservable(pageLink: PageLink, subType: string = '',
                      config?: RequestConfig): Observable<Array<BaseData<EntityId>>> {
    this.downtimeCodeConfigIds = [];
    this.entitiesObservable =
          this.downtimeCodeConfigService.getTenantDowntimeCodesConfigurationInfos(pageLink,subType,config)
    if (this.entitiesObservable) {
       this.entitiesObservable.pipe(
          map((data) => {
            return data && data['data'].length ? this.downtimeCodeConfigIds = data['data'] : null})).subscribe();
        } else {
          return of(null);
    }
  }

  buildForm(entity: DowntimeEntryInfo): FormGroup {
    return this.fb.group(
      {
        assetId: [entity ? entity.assetId : null,[Validators.required]],
        deviceId: [entity ? entity.deviceId : null,[Validators.required]],
        startDateTimeMs: [entity ? entity.startDateTimeMs : null],
        endDateTimeMs: [entity ? entity.endDateTimeMs : null],
        downtimeCodeId: [entity ? entity.downtimeCodeId : null],
      }
    );
  }


  updateForm(entity: DowntimeEntryInfo) {
    this.entityForm.patchValue({asset:entity.assetId});
    this.entityForm.patchValue({device:entity.deviceId});
    this.entityForm.patchValue({startDateTimeMs:entity.startDateTimeMs});
    this.entityForm.patchValue({endDateTimeMs:entity.endDateTimeMs});
    this.entityForm.patchValue({downtimeCodeId:entity.downtimeCodeId});

  }


}
