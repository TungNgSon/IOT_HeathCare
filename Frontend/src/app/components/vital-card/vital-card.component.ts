import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { ChartConfiguration } from 'chart.js';

@Component({
  selector: 'app-vital-card',
  templateUrl: './vital-card.component.html',
  styleUrls: ['./vital-card.component.scss']
})
export class VitalCardComponent implements OnChanges {
  @Input() data: any;
  @Input() last24hData: any[] = [];

  // Khởi tạo các đối tượng dữ liệu biểu đồ
  temperatureData: ChartConfiguration['data'] = { datasets: [], labels: [] };
  heartRateData: ChartConfiguration['data'] = { datasets: [], labels: [] };
  spo2Data: ChartConfiguration['data'] = { datasets: [], labels: [] };

  // chartOptions: ChartConfiguration['options'] = {
  //   responsive: true,
  //   maintainAspectRatio: false,
  //   scales: {
  //     y: { beginAtZero: true, grid: { color: '#f0f0f0' } },
  //     x: { grid: { display: false } }
  //   },
  // };
  chartOptions: ChartConfiguration['options'] = {
  responsive: true,
  maintainAspectRatio: false,
  scales: {
    y: { 
      beginAtZero: true,
      grid: { color: '#f0f0f0' }
    },
    x: {
      grid: { display: false },
      ticks: {
        autoSkip: true, // Tự động bỏ qua các nhãn để tránh lộn xộn
        maxTicksLimit: 7 // Chỉ hiển thị tối đa 7 nhãn trên trục
      }
    }
  },
};

  ngOnChanges(changes: SimpleChanges): void {
    // Chỉ xử lý khi dữ liệu 24h được load lần đầu
    if (changes['last24hData'] && changes['last24hData'].currentValue.length > 0) {
      this.populateChartsWith24hData(changes['last24hData'].currentValue);
    }
  }

  private populateChartsWith24hData(data: any[]): void {
  const labels = data.map(item => {
    const date = new Date(item.time);
    return date.toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' });
  });

  // Tạo dữ liệu cho 3 biểu đồ
  this.temperatureData.datasets = [{
    data: data.map(item => item.bodyTemperature),
    label: 'Nhiệt độ',
    borderColor: '#ff4d4f',
    backgroundColor: 'rgba(255,77,79,0.1)',
    fill: true,
    pointRadius: 0, // Bỏ các chấm tròn
    tension: 0.4
  }];
  this.temperatureData.labels = labels;

  this.heartRateData.datasets = [{
    data: data.map(item => item.heartRate),
    label: 'Nhịp tim',
    borderColor: '#1890ff',
    backgroundColor: 'rgba(24,144,255,0.1)',
    fill: true,
    pointRadius: 0, // Bỏ các chấm tròn
    tension: 0.4
  }];
  this.heartRateData.labels = labels;

  this.spo2Data.datasets = [{
    data: data.map(item => item.spo2),
    label: 'SpO2',
    borderColor: '#52c41a',
    backgroundColor: 'rgba(82,196,26,0.1)',
    fill: true,
    pointRadius: 0, // Bỏ các chấm tròn
    tension: 0.4
  }];
  this.spo2Data.labels = labels;
}
}