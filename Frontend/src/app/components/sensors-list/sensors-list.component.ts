import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';

interface DataSensor {
  id: number;
  heartRate: number;
  spo2?: number;
  sPO2?: number;
  bodyTemperature: number;
  time: string;
}

interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

@Component({
  selector: 'app-sensors-list',
  templateUrl: './sensors-list.component.html',
  styleUrls: ['./sensors-list.component.scss']
})
export class SensorsListComponent implements OnInit {
  // Pagination state
  page = 0;
  size = 10;
  sortBy = 'time';
  sortDir: 'asc' | 'desc' = 'desc';

  // Search state
  column: 'id' | 'heartRate' | 'SPO2' | 'bodyTemperature' | 'time' = 'time';
  minValue?: number;
  maxValue?: number;
  startTime?: string;
  endTime?: string;

  // Data
  rows: DataSensor[] = [];
  totalElements = 0;
  totalPages = 0;
  loading = false;
  errorMessage = '';
  isSearchMode = false;

  private readonly baseUrl = 'http://localhost:8080/api/sensors';

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.fetchPage();
  }

  fetchPage(): void {
    this.loading = true;
    this.errorMessage = '';
    const params = new HttpParams()
      .set('page', this.page.toString())
      .set('size', this.size.toString())
      .set('sortBy', this.sortBy)
      .set('sortDir', this.sortDir);

    this.http.get<PageResponse<DataSensor>>(this.baseUrl + '/page', { params }).subscribe({
      next: (res) => {
        this.rows = res.content ?? [];
        this.totalElements = res.totalElements ?? 0;
        this.totalPages = res.totalPages ?? 0;
        this.page = res.number ?? 0;
        this.size = res.size ?? this.size;
        this.loading = false;
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = 'Không tải được dữ liệu.';
        console.error(err);
      }
    });
  }

  search(): void {
    this.loading = true;
    this.errorMessage = '';
    this.isSearchMode = true;

    let params = new HttpParams()
      .set('column', this.column)
      .set('page', this.page.toString())
      .set('size', this.size.toString())
      .set('sortBy', this.sortBy)
      .set('sortDir', this.sortDir);

    if (this.column === 'time') {
      if (!this.startTime || !this.endTime) {
        this.errorMessage = 'Chọn khoảng thời gian.';
        this.loading = false;
        return;
      }
      const start = this.normalizeDateTimeLocal(this.startTime);
      const end = this.normalizeDateTimeLocal(this.endTime);
      if (!start || !end) {
        this.errorMessage = 'Định dạng thời gian không hợp lệ.';
        this.loading = false;
        return;
      }
      if (new Date(start) > new Date(end)) {
        this.errorMessage = 'Khoảng thời gian không hợp lệ: Từ > Đến';
        this.loading = false;
        return;
      }
      params = params.set('startTime', start).set('endTime', end);
    } else {
      // For numeric columns: if both empty = fetch all, if one filled = require both
      const hasMin = this.minValue != null && this.minValue !== undefined;
      const hasMax = this.maxValue != null && this.maxValue !== undefined;
      
      if (hasMin && !hasMax) {
        this.errorMessage = 'Nhập cả min và max hoặc để trống cả hai.';
        this.loading = false;
        return;
      }
      if (!hasMin && hasMax) {
        this.errorMessage = 'Nhập cả min và max hoặc để trống cả hai.';
        this.loading = false;
        return;
      }
      
      // If both have values, add them to params
      if (hasMin && hasMax) {
        params = params.set('minValue', String(this.minValue)).set('maxValue', String(this.maxValue));
      }
      // If both empty, don't add min/max params (backend will fetch all)
    }

    this.http.get<PageResponse<DataSensor>>(this.baseUrl + '/search', { params }).subscribe({
      next: (res) => {
        this.rows = res.content ?? [];
        this.totalElements = res.totalElements ?? 0;
        this.totalPages = res.totalPages ?? 0;
        this.page = res.number ?? 0;
        this.size = res.size ?? this.size;
        this.loading = false;
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = err?.error?.message || 'Lỗi khi tìm kiếm.';
        console.error(err);
      }
    });
  }

  resetSearchAndReload(): void {
    this.column = 'time';
    this.minValue = undefined;
    this.maxValue = undefined;
    this.startTime = undefined;
    this.endTime = undefined;
    this.page = 0;
    this.isSearchMode = false;
    this.fetchPage();
  }

  goToPage(target: number): void {
    if (target < 0 || target >= this.totalPages) return;
    this.page = target;
    if (this.isSearchMode) {
      this.search();
    } else {
      this.fetchPage();
    }
  }

  changePageSize(newSize: number): void {
    this.size = Number(newSize);
    this.page = 0;
    if (this.isSearchMode) {
      this.search();
    } else {
      this.fetchPage();
    }
  }

  sortByColumn(column: string): void {
    if (this.sortBy === column) {
      // Toggle direction if same column
      this.sortDir = this.sortDir === 'asc' ? 'desc' : 'asc';
    } else {
      // New column, default to desc
      this.sortBy = column;
      this.sortDir = 'desc';
    }
    
    this.page = 0; // Reset to first page when sorting
    
    if (this.isSearchMode) {
      this.search();
    } else {
      this.fetchPage();
    }
  }

  private normalizeDateTimeLocal(value: string | undefined): string | null {
    if (!value) return null;
    // value from <input type="datetime-local"> is usually 'YYYY-MM-DDTHH:mm' (length 16)
    // Backend expects 'yyyy-MM-dd\'T\'HH:mm:ss'
    const hasSeconds = value.length >= 19 && /\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}/.test(value);
    if (hasSeconds) return value;
    // Append ':00' seconds
    if (/\d{4}-\d{2}-\d{2}T\d{2}:\d{2}$/.test(value)) {
      return value + ':00';
    }
    // Fallback: try to parse and reformat
    const d = new Date(value);
    if (isNaN(d.getTime())) return null;
    const pad = (n: number) => (n < 10 ? '0' + n : '' + n);
    const yyyy = d.getFullYear();
    const MM = pad(d.getMonth() + 1);
    const dd = pad(d.getDate());
    const HH = pad(d.getHours());
    const mm = pad(d.getMinutes());
    const ss = pad(d.getSeconds());
    return `${yyyy}-${MM}-${dd}T${HH}:${mm}:${ss}`;
  }
}


