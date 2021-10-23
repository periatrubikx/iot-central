import { ChangeDetectorRef, Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { AppState } from '@app/core/core.state';
import { ShiftInfo } from '@app/shared/models/shift.models';
import { Store } from '@ngrx/store';
import { EntityType } from '@shared/models/entity-type.models';
import { EntityComponent } from '../../components/entity/entity.component';
import { EntityTableConfig } from '../../models/entity/entities-table-config.models';

@Component({
  selector: 'tb-shift',
  templateUrl: './shift.component.html',
  styleUrls: ['./shift.component.scss']
})
export class ShiftComponent extends EntityComponent<ShiftInfo> {


  
  
  entityType = EntityType;
  shiftScope: 'tenant' | 'customer' | 'customer_user' ;
  
  
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
  }
  
  buildForm(entity: ShiftInfo): FormGroup {
    throw new Error('Method not implemented.');
  }
  updateForm(entity: ShiftInfo) {
    throw new Error('Method not implemented.');
  }
}
