import { Component, OnInit, OnDestroy } from '@angular/core';
import { WebsocketService, SensorData } from '../../services/websocket.service';
import { Subscription } from 'rxjs';
import { HttpClient } from '@angular/common/http';
@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit, OnDestroy {
  sensorData: SensorData | null = null;
  last24hData: any[] = [];
  private subscription: Subscription | null = null;

  constructor(private websocketService: WebsocketService, private http: HttpClient) {}

  ngOnInit() {
    console.log('Dashboard: Subscribing to WebSocket');
    this.subscription = this.websocketService.getSensorData().subscribe({
      next: (data) => {
        console.log('Dashboard: Received new sensor data:', data);
        this.sensorData = data;
        console.log('Dashboard: Updated sensorData is now:', this.sensorData); 
      },
      error: (error) => console.error('Dashboard: WebSocket error:', error)
    });
    // Gọi phương thức fetchLast24hData() ở đây, sau khi khởi tạo component
    this.fetchLast24hData(); 
  }

  ngOnDestroy() {
    console.log('Dashboard: Cleaning up subscription');
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
  }

  // Phương thức fetchLast24hData() phải nằm bên trong class
  fetchLast24hData() {
    this.http.get<any[]>('http://localhost:8080/api/sensors/last24h').subscribe({
      next: (data) => {
        this.last24hData = data;
        console.log('Fetched 24h data:', this.last24hData);
      },
      error: (error) => console.error('Failed to fetch 24h data:', error)
    });
  }
}