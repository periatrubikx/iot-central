import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DownloadCodesConfigComponent } from './download-codes-config.component';

describe('DownloadCodesConfigComponent', () => {
  let component: DownloadCodesConfigComponent;
  let fixture: ComponentFixture<DownloadCodesConfigComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DownloadCodesConfigComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DownloadCodesConfigComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
