import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';

// export interface SensorData {
//   id: number;
//   heartRate: number;
//   heartRateValid: boolean;
//   spO2: number;
//   spO2Valid: boolean;
//   temperature: number;
//   temperatureValid: boolean;
//   timestamp: string;
// }
export interface VitalData {
  valid: boolean;
  unit: string;
  value: number;
}

export interface SensorData {
  id: number;
  heartRate: VitalData;
  spO2: VitalData;
  temperature: VitalData;
  timestamp: string;
}

export interface DeviceStateMessage {
  type: string;
  device: string;
  action: string;
  timestamp: number;
}

@Injectable({
  providedIn: 'root'
})
export class WebsocketService {
  private socket: WebSocket;
  private sensorSubject = new Subject<SensorData>();
  private deviceStateSubject = new Subject<DeviceStateMessage>();

  constructor() {
    this.socket = new WebSocket('ws://localhost:8080/ws/sensor');
    this.setupWebSocket();
  }

  private setupWebSocket(): void {
    this.socket.onopen = () => {
      console.log('✅ WebSocket connected');
    };

    this.socket.onmessage = (event) => {
      try {
        const data = JSON.parse(event.data);
        
        // Check if it's sensor data
        if (data.type === 'sensorData' || (data.heartRate && data.temperature)) {
          this.sensorSubject.next(data as SensorData);
        }
        // Check if it's device state message
        else if (data.type === 'deviceState') {
          this.deviceStateSubject.next(data as DeviceStateMessage);
        }
      } catch (e) {
        console.error('❌ Lỗi parse WebSocket data:', e);
      }
    };

    this.socket.onclose = () => {
      console.warn('⚠️ WebSocket disconnected. Reconnecting...');
      setTimeout(() => {
        this.socket = new WebSocket('ws://localhost:8080/ws/sensor');
        this.setupWebSocket();
      }, 5000);
    };

    this.socket.onerror = (error) => {
      console.error('WebSocket error:', error);
    };
  }

  getSensorData(): Observable<SensorData> {
    return this.sensorSubject.asObservable();
  }

  getDeviceStateMessages(): Observable<DeviceStateMessage> {
    return this.deviceStateSubject.asObservable();
  }

  /**
   * Gửi device state message để trigger cập nhật UI
   * @param message Device state message
   */
  sendDeviceStateMessage(message: any): void {
    console.log('📤 Sending device state message:', message);
    this.deviceStateSubject.next(message);
  }

  closeConnection(): void {
    this.socket.close();
  }
}
