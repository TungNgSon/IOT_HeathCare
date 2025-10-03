import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { WebsocketService } from '../../services/websocket.service';

@Component({
  selector: 'app-smart-lighting-controll',
  templateUrl: './smart-lighting-controll.component.html',
  styleUrls: ['./smart-lighting-controll.component.scss']
})
export class SmartLightingControllComponent implements OnInit {
  lights = [
    { id: 1, name: 'LED1', status: 'OFF', isOn: false },
    { id: 2, name: 'LED2', status: 'OFF', isOn: false },
    { id: 3, name: 'LED3', status: 'OFF', isOn: false }
  ];

  constructor(private http: HttpClient, private wsService: WebsocketService) {}

  ngOnInit(): void {
    // Listen for device state messages from WebSocket
    this.wsService.getDeviceStateMessages().subscribe((message) => {
      this.handleDeviceStateMessage(message);
    });
  }

  onToggleLight(lightId: number, state: boolean) {
    const light = this.lights.find(l => l.id === lightId);
    if (!light) return;
    
    // DON'T change the UI state immediately - wait for MQTT confirmation
    const action = state ? 'on' : 'off';
    this.http.get(`http://localhost:8080/api/device/${light.id}/${action}`, { responseType: 'text' })
      .subscribe({
        next: (res) => {
          console.log(`LED ${light.id} ${action} command sent:`, res);
          // UI will be updated when we receive MQTT confirmation
        },
        error: (err) => {
          console.error('Error sending LED command:', err);
          // Optionally show error to user
        }
      });
  }

  private handleDeviceStateMessage(message: any) {
    console.log('📥 Received device state message:', message);
    
    // Parse message format: "LED1 ON" or "LED2 OFF"
    if (message && message.device && message.action) {
      const deviceName = message.device; // e.g., "LED1"
      const action = message.action; // e.g., "ON"
      
      // Find the corresponding light
      const lightId = this.extractLightId(deviceName);
      if (lightId) {
        const light = this.lights.find(l => l.id === lightId);
        if (light) {
          // Update UI state based on MQTT confirmation or database sync
          light.isOn = action === 'ON';
          light.status = action;
          
          if (message.source === 'database_sync') {
            console.log(`🔄 LED ${lightId} synced from database: ${action}`);
          } else {
            console.log(`✅ LED ${lightId} confirmed ${action}`);
          }
        }
      }
    }
  }

  private extractLightId(deviceName: string): number | null {
    const match = deviceName.match(/LED(\d+)/);
    return match ? parseInt(match[1]) : null;
  }
}
