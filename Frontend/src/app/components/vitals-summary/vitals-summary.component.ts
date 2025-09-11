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
}