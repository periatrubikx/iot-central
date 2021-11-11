import { ChangeDetectorRef, Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AppState } from '@app/core/core.state';
import { AssetService } from '@app/core/public-api';
import { EntityComponent } from '@app/modules/home/components/entity/entity.component';
import { EntityTableConfig } from '@app/modules/home/models/entity/entities-table-config.models';
import { AssetInfo } from '@app/shared/models/asset.models';
import { DowntimeEntryInfo } from '@app/shared/models/downtime-entry.models';
import { EntityType } from '@app/shared/models/entity-type.models';
import { PageLink } from '@app/shared/public-api';
import { Store } from '@ngrx/store';

@Component({
  selector: 'tb-downtime-entry',
  templateUrl: './downtime-entry.component.html',
  styleUrls: ['./downtime-entry.component.scss']
})
export class DowntimeEntryComponent extends EntityComponent<DowntimeEntryInfo> {
  
  allowedEntityTypes = [EntityType.ASSET];

  private readonly config: EntityTableConfig<AssetInfo> = new EntityTableConfig<AssetInfo>();

  entityType = EntityType;
  assetsType = [];
  assetsIds: any;
  downtimeEntryScope: 'tenant' | 'customer' | 'customer_user' ;
  
  constructor(
    protected store: Store<AppState>,
    @Inject('entity') protected entityValue: DowntimeEntryInfo,
    @Inject('entitiesTableConfig') protected entitiesTableConfigValue: EntityTableConfig<DowntimeEntryInfo>,
    public fb: FormBuilder,
    private assetService:AssetService,
    protected cd: ChangeDetectorRef
  ){
    super(store, fb, entityValue, entitiesTableConfigValue, cd);
  }

  ngOnInit(): void {
    this.downtimeEntryScope = this.entitiesTableConfigValue.componentsData.downtimeEntryScope;
    super.ngOnInit();
  }

  buildForm(entity: DowntimeEntryInfo): FormGroup {
    return this.fb.group(
      {
        asset: [entity ? entity.asset : null,[Validators.required]],
        device: [entity ? entity.device : null,[Validators.required]],
        startDateTimeMs: [entity ? entity.startDateTimeMs : null],
        endDateTimeMs: [entity ? entity.endDateTimeMs : null],
        reason: [entity ? entity.reason : null],
      }
    );
  }


  updateForm(entity: DowntimeEntryInfo) {
    this.entityForm.patchValue({asset:entity.asset});
    this.entityForm.patchValue({device:entity.device});
    this.entityForm.patchValue({startDateTimeMs:entity.startDateTimeMs});
    this.entityForm.patchValue({endDateTimeMs:entity.endDateTimeMs});
    this.entityForm.patchValue({reason:entity.reason});

  }


}
