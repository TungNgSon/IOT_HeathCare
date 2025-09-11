import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-lighting-switch',
  templateUrl: './lighting-switch.component.html',
  styleUrls: ['./lighting-switch.component.scss']
})
export class LightingSwitchComponent {
  @Input() label: string = '';
  @Input() icon: string = '';
  @Input() id: number = 0;
  @Output() stateChange = new EventEmitter<boolean>();
  
  isOn: boolean = false;

  toggle() {
    this.isOn = !this.isOn;
    this.stateChange.emit(this.isOn);
  }
}