import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ShiftTableHeaderComponent } from './shift-table-header.component';

describe('ShiftTableHeaderComponent', () => {
  let component: ShiftTableHeaderComponent;
  let fixture: ComponentFixture<ShiftTableHeaderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ShiftTableHeaderComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ShiftTableHeaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
