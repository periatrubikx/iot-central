import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DowntimeCodesConfigTableHeaderComponent } from './downtime-codes-config-table-header.component';

describe('DowntimeCodesConfigTableHeaderComponent', () => {
  let component: DowntimeCodesConfigTableHeaderComponent;
  let fixture: ComponentFixture<DowntimeCodesConfigTableHeaderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DowntimeCodesConfigTableHeaderComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DowntimeCodesConfigTableHeaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
