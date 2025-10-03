import { Component, OnInit, OnDestroy } from '@angular/core';
import { WebsocketService, SensorData } from '../../services/websocket.service';
import { Subscription } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { DeviceStatusService, DeviceAction } from '../../services/device-status.service';
@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit, OnDestroy {
  sensorData: SensorData | null = null;
  last24hData: any[] = [];
  private subscription: Subscription | null = null;

  constructor(
    private websocketService: WebsocketService, 
    private http: HttpClient,
    private authService: AuthService,
    private router: Router,
    private deviceStatusService: DeviceStatusService
  ) {}

  ngOnInit() {
    // Kiểm tra authentication trước khi load data
    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['/login']);
      return;
    }
    
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
    
    // Đồng bộ trạng thái LED khi load dashboard
    this.syncLEDStatus();
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

  /**
   * Đồng bộ trạng thái LED từ database
   */
  syncLEDStatus() {
    console.log('🔄 Syncing LED status from database...');
    
    this.deviceStatusService.getAllLEDStatus().subscribe({
      next: (deviceActions: DeviceAction[]) => {
        console.log('📊 Received LED status from database:', deviceActions);
        
        // Gửi trạng thái đến smart-lighting component thông qua WebSocket
        deviceActions.forEach(deviceAction => {
          if (deviceAction && deviceAction.action) {
            console.log(`🔄 Syncing ${deviceAction.device} to ${deviceAction.action}`);
            
            // Nếu đèn đang ON trong database, gọi API để thực sự bật đèn
            if (deviceAction.action === 'ON') {
              const ledId = this.extractLedId(deviceAction.device);
              if (ledId) {
                console.log(`🔌 Actually turning ON LED${ledId} via API...`);
                this.http.get(`http://localhost:8080/api/device/${ledId}/on`, { responseType: 'text' })
                  .subscribe({
                    next: (res) => console.log(`✅ LED${ledId} turned ON:`, res),
                    error: (err) => console.error(`❌ Failed to turn ON LED${ledId}:`, err)
                  });
              }
            }
            
            // Gửi message qua WebSocket để smart-lighting component nhận được
            const message = {
              type: 'deviceState',
              device: deviceAction.device,
              action: deviceAction.action,
              timestamp: new Date().getTime(),
              source: 'database_sync'
            };
            
            // Trigger WebSocket message để smart-lighting component cập nhật UI
            this.websocketService.sendDeviceStateMessage(message);
          }
        });
      },
      error: (error) => {
        console.error('❌ Error syncing LED status:', error);
      }
    });
  }

  /**
   * Trích xuất LED ID từ tên device (VD: "LED1" -> 1)
   */
  private extractLedId(deviceName: string): number | null {
    const match = deviceName.match(/LED(\d+)/);
    return match ? parseInt(match[1]) : null;
  }
}