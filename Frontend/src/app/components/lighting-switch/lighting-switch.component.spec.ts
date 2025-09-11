import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LightSwitchComponent } from './lighting-switch.component';

describe('LightSwitchComponent', () => {
  let component: LightSwitchComponent;
  let fixture: ComponentFixture<LightSwitchComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [LightSwitchComponent]
    });
    fixture = TestBed.createComponent(LightSwitchComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
