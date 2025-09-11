import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VitalsSummaryComponent } from './vitals-summary.component';

describe('VitalsSummaryComponent', () => {
  let component: VitalsSummaryComponent;
  let fixture: ComponentFixture<VitalsSummaryComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [VitalsSummaryComponent]
    });
    fixture = TestBed.createComponent(VitalsSummaryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
