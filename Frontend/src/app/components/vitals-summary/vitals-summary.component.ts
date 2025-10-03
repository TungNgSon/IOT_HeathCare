// vitals-summary.component.ts
import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { SensorData } from '../../services/websocket.service';

@Component({
  selector: 'app-vitals-summary',
  templateUrl: './vitals-summary.component.html',
  styleUrls: ['./vitals-summary.component.scss']
})
export class VitalsSummaryComponent implements OnChanges { // Thêm implements OnChanges
  @Input() sensorData: SensorData | null = null;

  ngOnChanges(changes: SimpleChanges) {
    // Kiểm tra xem thuộc tính sensorData có thay đổi không
    if (changes['sensorData']) {
      console.log('VitalsSummaryComponent: Data received via @Input:', this.sensorData);
    }
  }

  // Tính toán trạng thái màu sắc cho Temperature
  getTemperatureStatus(): string {
    if (!this.sensorData?.temperature?.value) return 'normal';
    const temp = this.sensorData.temperature.value;
    if (temp < 36.1) return 'low';
    if (temp > 37.2) return 'high';
    return 'normal';
  }

  // Tính toán trạng thái màu sắc cho Heart Rate
  getHeartRateStatus(): string {
    if (!this.sensorData?.heartRate?.value) return 'normal';
    const hr = this.sensorData.heartRate.value;
    if (hr < 60) return 'low';
    if (hr > 100) return 'high';
    return 'normal';
  }

  // Tính toán trạng thái màu sắc cho SpO2
  getSpO2Status(): string {
    if (!this.sensorData?.spO2?.value) return 'normal';
    const spo2 = this.sensorData.spO2.value;
    if (spo2 < 95) return 'low';
    if (spo2 > 100) return 'high';
    return 'normal';
  }
}