import { ChangeDetectorRef, Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AppState } from '@app/core/core.state';
import { EntityComponent } from '@app/modules/home/components/entity/entity.component';
import { EntityTableConfig } from '@app/modules/home/models/entity/entities-table-config.models';
import { DownloadCodesConfigurationInfo } from '@app/shared/models/download-codes-config';
import { EntityType } from '@app/shared/models/entity-type.models';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';

@Component({
  selector: 'tb-download-codes-config',
  templateUrl: './download-codes-config.component.html',
  styleUrls: ['./download-codes-config.component.scss']
})
export class DownloadCodesConfigComponent extends EntityComponent<DownloadCodesConfigurationInfo>  {
  
  entityType = EntityType;
  downloadCodesConfigurationScope: 'tenant' | 'customer' | 'customer_user' ;


  maxStartTimeMs: Observable<number | null>;
  minEndTimeMs: Observable<number | null>;

  constructor(protected store: Store<AppState>,
    @Inject('entity') protected entityValue: DownloadCodesConfigurationInfo,
    @Inject('entitiesTableConfig') protected entitiesTableConfigValue: EntityTableConfig<DownloadCodesConfigurationInfo>,
    public fb: FormBuilder,
    protected cd: ChangeDetectorRef) {
      super(store, fb, entityValue, entitiesTableConfigValue, cd);
    }

  ngOnInit(): void {
    this.downloadCodesConfigurationScope = this.entitiesTableConfigValue.componentsData.downloadCodesConfigurationScope;
    super.ngOnInit();
  }

  buildForm(entity: DownloadCodesConfigurationInfo): FormGroup {
    return this.fb.group(
      {
        level1: [entity ? entity.level1 : null, [Validators.required]],
        level2: [entity ? entity.level2 : null],
        level3: [entity ? entity.level3 : null],
        code: [entity ? entity.code : '', [Validators.required]],
      }
    );
  }

  updateForm(entity: DownloadCodesConfigurationInfo) {
    this.entityForm.patchValue({level1:entity.level1});
    this.entityForm.patchValue({level2:entity.level2});
    this.entityForm.patchValue({level3:entity.level3});
    this.entityForm.patchValue({code:entity.code});
  }

}
