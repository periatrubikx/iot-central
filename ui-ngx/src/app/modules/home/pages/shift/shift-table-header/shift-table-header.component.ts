import { Component, OnInit } from '@angular/core';
import { AppState } from '@app/core/core.state';
import { EntityTableHeaderComponent } from '@app/modules/home/components/entity/entity-table-header.component';
import { ShiftInfo } from '@app/shared/models/shift.models';
import { EntityType } from '@app/shared/public-api';
import { Store } from '@ngrx/store';

@Component({
  selector: 'tb-shift-table-header',
  templateUrl: './shift-table-header.component.html',
  styleUrls: ['./shift-table-header.component.scss']
})
export class ShiftTableHeaderComponent extends EntityTableHeaderComponent<ShiftInfo> {

  entityType = EntityType;

  constructor(protected store:Store<AppState>) {
    super(store)
   }


   shiftTypeChanged(shiftType: string) {
    this.entitiesTableConfig.componentsData.shiftsType = shiftType;
    this.entitiesTableConfig.table.resetSortAndFilter(true);
  }
}
