import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface DeviceAction {
  id: number;
  device: string;
  action: string;
  time: string;
}

@Injectable({
  providedIn: 'root'
})
export class DeviceStatusService {
  private baseUrl = 'http://localhost:8080/api/device';

  constructor(private http: HttpClient) { }

  /**
   * Lấy trạng thái gần nhất của một device
   * @param deviceName Tên device (VD: LED1, LED2, LED3)
   * @returns Observable<DeviceAction | null>
   */
  getLatestDeviceStatus(deviceName: string): Observable<DeviceAction | null> {
    return this.http.get<DeviceAction | null>(`${this.baseUrl}/latest?device=${deviceName}`);
  }

  /**
   * Lấy trạng thái gần nhất của tất cả LED
   * @returns Observable<DeviceAction[]>
   */
  getAllLEDStatus(): Observable<DeviceAction[]> {
    const ledDevices = ['LED1', 'LED2', 'LED3'];
    const requests = ledDevices.map(device => 
      this.getLatestDeviceStatus(device)
    );
    
    // Sử dụng forkJoin để gọi tất cả API cùng lúc
    return new Observable(observer => {
      Promise.all(requests.map(req => req.toPromise()))
        .then(results => {
          // Lọc ra những device có dữ liệu
          const validResults = results.filter(result => result !== null) as DeviceAction[];
          observer.next(validResults);
          observer.complete();
        })
        .catch(error => {
          console.error('Error fetching LED status:', error);
          observer.error(error);
        });
    });
  }
}
