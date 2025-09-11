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

@Injectable({
  providedIn: 'root'
})
export class WebsocketService {
  private socket: WebSocket;
  private sensorSubject = new Subject<SensorData>();

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
        const data: SensorData = JSON.parse(event.data);
        this.sensorSubject.next(data);
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

  closeConnection(): void {
    this.socket.close();
  }
}
