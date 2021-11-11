import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DowntimeEntryTableHeaderComponent } from './downtime-entry-table-header.component';

describe('DowntimeEntryTableHeaderComponent', () => {
  let component: DowntimeEntryTableHeaderComponent;
  let fixture: ComponentFixture<DowntimeEntryTableHeaderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DowntimeEntryTableHeaderComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DowntimeEntryTableHeaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
