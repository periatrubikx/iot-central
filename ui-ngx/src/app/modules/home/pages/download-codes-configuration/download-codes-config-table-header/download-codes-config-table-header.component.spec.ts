import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DownloadCodesConfigTableHeaderComponent } from './download-codes-config-table-header.component';

describe('DownloadCodesConfigTableHeaderComponent', () => {
  let component: DownloadCodesConfigTableHeaderComponent;
  let fixture: ComponentFixture<DownloadCodesConfigTableHeaderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DownloadCodesConfigTableHeaderComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DownloadCodesConfigTableHeaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
