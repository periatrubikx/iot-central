import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DowntimeCodesConfigComponent } from './downtime-codes-config.component';

describe('DowntimeCodesConfigComponent', () => {
  let component: DowntimeCodesConfigComponent;
  let fixture: ComponentFixture<DowntimeCodesConfigComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DowntimeCodesConfigComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DowntimeCodesConfigComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
