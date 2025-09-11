import { Component } from '@angular/core';

@Component({
  selector: 'app-smart-lighting-control',
  template: `
    <mat-card class="smart-lighting-card">
      <div class="card-header">
        <mat-icon class="header-icon">lightbulb</mat-icon>
        Smart Lighting Control
      </div>
      <div class="lighting-switches">
        <app-lighting-switch
          [name]="'Bedroom Light'"
          [status]="false"
          (toggleChange)="onLightToggle('bedroom', $event)"
        ></app-lighting-switch>
        <app-lighting-switch
          [name]="'LivingRoom Light'"
          [status]="false"
          (toggleChange)="onLightToggle('living-room', $event)"
        ></app-lighting-switch>
      </div>
    </mat-card>
  `,
  styleUrls: ['./smart-lighting-control.component.css']
})
export class SmartLightingControlComponent {
  onLightToggle(room: string, status: boolean) {
    // Logic gọi LightingService ở đây
    console.log(`${room} light is now:`, status ? 'ON' : 'OFF');
  }
}