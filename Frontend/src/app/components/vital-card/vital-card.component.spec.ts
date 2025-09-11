import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VitalCardComponent } from './vital-card.component';

describe('VitalCardComponent', () => {
  let component: VitalCardComponent;
  let fixture: ComponentFixture<VitalCardComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [VitalCardComponent]
    });
    fixture = TestBed.createComponent(VitalCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
