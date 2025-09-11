import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';

interface DeviceAction {
  id: number;
  device: string;
  action: string;
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
  selector: 'app-device-action-list',
  templateUrl: './device-action-list.component.html',
  styleUrls: ['./device-action-list.component.scss']
})
export class DeviceActionListComponent implements OnInit {
  // Pagination & sorting
  page = 0;
  size = 10;
  sortBy: 'time' | 'id' | 'device' | 'action' = 'time';
  sortDir: 'asc' | 'desc' = 'desc';

  // Search state
  column: 'id' | 'time' | 'device' | 'action' = 'time';
  minId?: number;
  maxId?: number;
  startTime?: string;
  endTime?: string;
  device?: string;
  action?: string;

  // Data
  rows: DeviceAction[] = [];
  totalPages = 0;
  totalElements = 0;
  loading = false;
  errorMessage = '';
  isSearchMode = false;

  private readonly baseUrl = 'http://localhost:8080/api/device';

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

    this.http.get<PageResponse<DeviceAction>>(this.baseUrl + '/page', { params }).subscribe({
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

    switch (this.column) {
      case 'id': {
        const hasMin = this.minId != null;
        const hasMax = this.maxId != null;
        if ((hasMin && !hasMax) || (!hasMin && hasMax)) {
          this.errorMessage = 'Nhập cả minId và maxId hoặc để trống cả hai.';
          this.loading = false;
          return;
        }
        if (hasMin && hasMax) {
          params = params.set('minId', String(this.minId)).set('maxId', String(this.maxId));
        }
        break;
      }
      case 'time': {
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
        params = params.set('startTime', start).set('endTime', end);
        break;
      }
      case 'device': {
        if (!this.device) {
          this.errorMessage = 'Nhập device để tìm kiếm';
          this.loading = false;
          return;
        }
        params = params.set('device', this.device);
        break;
      }
      case 'action': {
        if (!this.action) {
          this.errorMessage = 'Nhập action để tìm kiếm';
          this.loading = false;
          return;
        }
        params = params.set('action', this.action);
        break;
      }
    }

    this.http.get<PageResponse<DeviceAction>>(this.baseUrl + '/search', { params }).subscribe({
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
    this.minId = undefined;
    this.maxId = undefined;
    this.startTime = undefined;
    this.endTime = undefined;
    this.device = undefined;
    this.action = undefined;
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

  sortByColumn(column: 'time' | 'id' | 'device' | 'action'): void {
    if (this.sortBy === column) {
      this.sortDir = this.sortDir === 'asc' ? 'desc' : 'asc';
    } else {
      this.sortBy = column;
      this.sortDir = 'desc';
    }
    this.page = 0;
    if (this.isSearchMode) {
      this.search();
    } else {
      this.fetchPage();
    }
  }

  private normalizeDateTimeLocal(value: string | undefined): string | null {
    if (!value) return null;
    const hasSeconds = value.length >= 19 && /\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}/.test(value);
    if (hasSeconds) return value;
    if (/\d{4}-\d{2}-\d{2}T\d{2}:\d{2}$/.test(value)) {
      return value + ':00';
    }
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


