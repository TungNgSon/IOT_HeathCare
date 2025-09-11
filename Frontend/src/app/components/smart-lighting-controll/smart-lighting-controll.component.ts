import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-smart-lighting-controll',
  templateUrl: './smart-lighting-controll.component.html',
  styleUrls: ['./smart-lighting-controll.component.scss']
})
export class SmartLightingControllComponent {
  lights = [
    { id: 1, name: 'LED1', status: 'OFF', isOn: false },
    { id: 2, name: 'LED2', status: 'OFF', isOn: false }
  ];

  constructor(private http: HttpClient) {}

  onToggleLight(lightId: number, state: boolean) {
    const light = this.lights.find(l => l.id === lightId);
    if (!light) return;
    light.isOn = state;
    light.status = state ? 'ON' : 'OFF';
    const action = state ? 'on' : 'off';
    this.http.get(`http://localhost:8080/api/device/${light.id}/${action}`, { responseType: 'text' })
      .subscribe({
        next: (res) => console.log(`LED ${light.id} ${action}:`, res),
        error: (err) => console.error('Error:', err)
      });
  }
}
