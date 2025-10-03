import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { ChartConfiguration } from 'chart.js';

@Component({
  selector: 'app-combined-chart',
  templateUrl: './combined-chart.component.html',
  styleUrls: ['./combined-chart.component.scss']
})
export class CombinedChartComponent implements OnChanges {
  @Input() data: any;
  @Input() last24hData: any[] = [];

  // Combined chart data
  combinedChartData: ChartConfiguration['data'] = { datasets: [], labels: [] };

  chartOptions: ChartConfiguration['options'] = {
    responsive: true,
    maintainAspectRatio: false,
    interaction: {
      mode: 'index',
      intersect: false,
    },
    scales: {
      y: {
        type: 'linear',
        display: true,
        position: 'left',
        title: {
          display: true,
          text: 'Temperature (°C) & Heart Rate (BPM)'
        },
        grid: { color: '#f0f0f0' },
        min: 0,
        max: 120,
        ticks: {
          stepSize: 20,
          callback: function(value) {
            const numValue = typeof value === 'string' ? parseFloat(value) : value;
            if (numValue <= 40) {
              return numValue + '°C';
            } else {
              return numValue + ' BPM';
            }
          }
        }
      },
      y1: {
        type: 'linear',
        display: true,
        position: 'right',
        title: {
          display: true,
          text: 'SpO2 (%)'
        },
        grid: {
          drawOnChartArea: false,
        },
        min: 0,
        max: 100,
        ticks: {
          stepSize: 20,
          callback: function(value) {
            return value + '%';
          }
        }
      },
      x: {
        grid: { display: false },
        ticks: {
          autoSkip: true,
          maxTicksLimit: 7
        }
      }
    },
    plugins: {
      legend: {
        display: true,
        position: 'top',
      },
      tooltip: {
        mode: 'index',
        intersect: false,
      }
    }
  };

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['last24hData'] && changes['last24hData'].currentValue.length > 0) {
      this.populateCombinedChart(changes['last24hData'].currentValue);
    }
  }

  private populateCombinedChart(data: any[]): void {
    const labels = data.map(item => {
      const date = new Date(item.time);
      return date.toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' });
    });

    this.combinedChartData = {
      labels: labels,
      datasets: [
        {
          label: 'Temperature (°C)',
          data: data.map(item => item.bodyTemperature),
          borderColor: '#ff4d4f',
          backgroundColor: 'rgba(255,77,79,0.1)',
          fill: false,
          pointRadius: 2,
          tension: 0.4,
          yAxisID: 'y'
        },
        {
          label: 'Heart Rate (BPM)',
          data: data.map(item => item.heartRate),
          borderColor: '#1890ff',
          backgroundColor: 'rgba(24,144,255,0.1)',
          fill: false,
          pointRadius: 2,
          tension: 0.4,
          yAxisID: 'y'
        },
        {
          label: 'SpO2 (%)',
          data: data.map(item => item.spo2),
          borderColor: '#52c41a',
          backgroundColor: 'rgba(82,196,26,0.1)',
          fill: false,
          pointRadius: 2,
          tension: 0.4,
          yAxisID: 'y1'
        }
      ]
    };
  }
}
