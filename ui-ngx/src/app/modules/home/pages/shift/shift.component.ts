import { ChangeDetectorRef, Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AppState } from '@app/core/core.state';
import { ShiftInfo } from '@app/shared/models/shift.models';
import { Store } from '@ngrx/store';
import { EntityType } from '@shared/models/entity-type.models';
import { EntityComponent } from '../../components/entity/entity.component';
import { EntityTableConfig } from '../../models/entity/entities-table-config.models';
import { NULL_UUID } from '@shared/models/id/has-uuid';
import { Observable } from 'rxjs';

@Component({
  selector: 'tb-shift',
  templateUrl: './shift.component.html',
  styleUrls: ['./shift.component.scss']
})
export class ShiftComponent extends EntityComponent<ShiftInfo> {


  entityType = EntityType;
  shiftScope: 'tenant' | 'customer' | 'customer_user' ;


  maxStartTimeMs: Observable<number | null>;
  minEndTimeMs: Observable<number | null>;

  constructor(
    protected store: Store<AppState>,
    @Inject('entity') protected entityValue: ShiftInfo,
    @Inject('entitiesTableConfig') protected entitiesTableConfigValue: EntityTableConfig<ShiftInfo>,
    public fb: FormBuilder,
    protected cd: ChangeDetectorRef) {
      super(store, fb, entityValue, entitiesTableConfigValue, cd);
    }

  ngOnInit(): void {
    this.shiftScope = this.entitiesTableConfigValue.componentsData.shiftScope;
    super.ngOnInit();
    this.maxStartTimeMs = this.entityForm.get('endTimeMs').valueChanges;
    this.minEndTimeMs = this.entityForm.get('startTimeMs').valueChanges;
  }

  isAssignedToCustomer(entity: ShiftInfo): boolean {
    return entity && entity.customerId && entity.customerId.id !== NULL_UUID;
  }


  buildForm(entity: ShiftInfo): FormGroup {
    return this.fb.group(
      {
        name: [entity ? entity.name : '', [Validators.required]],
        areaName: [entity ? entity.areaName : null, [Validators.required]],
        startTimeMs: [entity ? entity.startTimeMs : null],
        endTimeMs: [entity ? entity.endTimeMs : null],
      }
    );
  }
  updateForm(entity: ShiftInfo) {
    throw new Error('Method not implemented.');
  }
}
