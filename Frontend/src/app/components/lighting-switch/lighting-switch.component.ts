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
  @Input() isOn: boolean = false; // Make it an input so parent can control it
  @Output() stateChange = new EventEmitter<boolean>();

  toggle() {
    // Don't change isOn immediately - let parent handle the state
    // Just emit the desired state
    this.stateChange.emit(!this.isOn);
  }
}