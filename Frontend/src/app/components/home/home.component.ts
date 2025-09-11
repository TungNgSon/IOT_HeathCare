import { Component, OnInit } from '@angular/core';
import { WebsocketService } from '../../services/websocket.service';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  sensorData: any = null;
  lastUpdate: Date | null = null;

  constructor(private wsService: WebsocketService, private http: HttpClient) {}

  ngOnInit(): void {
    this.wsService.getSensorData().subscribe((data) => {
      this.sensorData = data;
      this.lastUpdate = new Date();
      // Log ra console mỗi lần nhận dữ liệu mới
      console.log('📥 Dữ liệu realtime:', data);
    });
  }

  controlLed(id: number, state: 'on' | 'off') {
    this.http.get(`http://localhost:8080/led/${id}/${state}`, { responseType: 'text' }).subscribe({
      next: (res) => {
        console.log(`LED ${id} ${state}:`, res);
        alert(`LED ${id} ${state.toUpperCase()}!`);
      },
      error: (err) => {
        console.error(err);
        alert('Có lỗi khi điều khiển LED!');
      }
    });
  }
}
