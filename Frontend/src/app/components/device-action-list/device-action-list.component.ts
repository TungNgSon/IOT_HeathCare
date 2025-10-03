import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';

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

  // Search state - new optimized approach
  selectedDevice: string = '';
  selectedAction: string = '';
  searchId?: number;
  searchTime?: string;

  // Data
  rows: DeviceAction[] = [];
  totalPages = 0;
  totalElements = 0;
  loading = false;
  errorMessage = '';
  isSearchMode = false;

  private readonly baseUrl = 'http://localhost:8080/api/device';

  constructor(
    private http: HttpClient,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Kiểm tra authentication trước khi load data
    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['/login']);
      return;
    }
    this.fetchPage();
  }

  fetchPage(): void {
    this.loading = true;
    this.errorMessage = '';
    const params = new HttpParams()
      .set('page', this.page.toString())
      .set('size', this.size.toString())
      .set('sortBy', this.mapColumnName(this.sortBy))
      .set('sortDir', this.sortDir);

    this.http.get<PageResponse<DeviceAction>>(this.baseUrl + '/page', { params }).subscribe({
      next: (res) => {
        this.rows = res.content ?? [];
        this.totalElements = res.totalElements ?? 0;
        this.totalPages = res.totalPages ?? 0;
        this.page = res.number ?? 0;
        this.size = res.size ?? this.size;
        this.loading = false;
        console.log('Fetch successful:', { sortBy: this.sortBy, mappedSortBy: this.mapColumnName(this.sortBy), sortDir: this.sortDir });
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = 'Không tải được dữ liệu.';
        console.error('Fetch error:', err);
        console.error('Request params:', { sortBy: this.sortBy, mappedSortBy: this.mapColumnName(this.sortBy), sortDir: this.sortDir });
      }
    });
  }


  resetSearchAndReload(): void {
    this.selectedDevice = '';
    this.selectedAction = '';
    this.searchId = undefined;
    this.searchTime = undefined;
    this.page = 0;
    this.isSearchMode = false;
    this.fetchPage();
  }

  // New optimized search methods
  searchByDevice(): void {
    if (!this.selectedDevice) {
      this.resetSearchAndReload();
      return;
    }
    // Clear other fields
    this.selectedAction = '';
    this.searchId = undefined;
    this.searchTime = undefined;
    this.performSearch('device', this.selectedDevice);
  }

  searchByAction(): void {
    if (!this.selectedAction) {
      this.resetSearchAndReload();
      return;
    }
    // Clear other fields
    this.selectedDevice = '';
    this.searchId = undefined;
    this.searchTime = undefined;
    this.performSearch('action', this.selectedAction);
  }

  searchById(): void {
    if (!this.searchId || this.searchId === 0) {
      this.resetSearchAndReload();
      return;
    }
    // Clear other fields
    this.selectedDevice = '';
    this.selectedAction = '';
    this.searchTime = undefined;
    this.performSearch('id', this.searchId.toString());
  }

  searchByTime(): void {
    if (!this.searchTime || this.searchTime.trim() === '') {
      this.resetSearchAndReload();
      return;
    }
    // Clear other fields
    this.selectedDevice = '';
    this.selectedAction = '';
    this.searchId = undefined;
    this.performSearch('time', this.searchTime.trim());
  }

  private performSearch(column: string, value: string): void {
    this.loading = true;
    this.errorMessage = '';
    this.isSearchMode = true;
    this.page = 0;

    let params = new HttpParams()
      .set('column', column)
      .set('page', this.page.toString())
      .set('size', this.size.toString())
      .set('sortBy', this.mapColumnNameForNativeQuery(this.sortBy))
      .set('sortDir', this.sortDir);

    if (column === 'time') {
      params = params.set('timeValue', value);
    } else {
      params = params.set('value', value);
    }

    this.http.get<PageResponse<DeviceAction>>(this.baseUrl + '/search-exact', { params }).subscribe({
      next: (res) => {
        this.rows = res.content ?? [];
        this.totalElements = res.totalElements ?? 0;
        this.totalPages = res.totalPages ?? 0;
        this.page = res.number ?? 0;
        this.size = res.size ?? this.size;
        this.loading = false;
        console.log('Search successful:', { column, value, sortBy: this.sortBy, sortDir: this.sortDir });
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = err?.error?.message || 'Lỗi khi tìm kiếm.';
        console.error('Search error:', err);
      }
    });
  }

  private performSearchWithPage(column: string, value: string): void {
    this.loading = true;
    this.errorMessage = '';

    let params = new HttpParams()
      .set('column', column)
      .set('page', this.page.toString())
      .set('size', this.size.toString())
      .set('sortBy', this.mapColumnNameForNativeQuery(this.sortBy))
      .set('sortDir', this.sortDir);

    if (column === 'time') {
      params = params.set('timeValue', value);
    } else {
      params = params.set('value', value);
    }

    this.http.get<PageResponse<DeviceAction>>(this.baseUrl + '/search-exact', { params }).subscribe({
      next: (res) => {
        this.rows = res.content ?? [];
        this.totalElements = res.totalElements ?? 0;
        this.totalPages = res.totalPages ?? 0;
        this.page = res.number ?? 0;
        this.size = res.size ?? this.size;
        this.loading = false;
        console.log('Search with page successful:', { column, value, page: this.page, sortBy: this.sortBy, sortDir: this.sortDir });
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = err?.error?.message || 'Lỗi khi tìm kiếm.';
        console.error('Search with page error:', err);
      }
    });
  }

  goToPage(target: number): void {
    if (target < 0 || target >= this.totalPages) return;
    this.page = target;
    if (this.isSearchMode) {
      // Determine which search to perform based on current state
      if (this.selectedDevice && this.selectedDevice !== '') {
        this.performSearchWithPage('device', this.selectedDevice);
      } else if (this.selectedAction && this.selectedAction !== '') {
        this.performSearchWithPage('action', this.selectedAction);
      } else if (this.searchId && this.searchId > 0) {
        this.performSearchWithPage('id', this.searchId.toString());
      } else if (this.searchTime && this.searchTime.trim() !== '') {
        this.performSearchWithPage('time', this.searchTime.trim());
      } else {
        this.fetchPage();
      }
    } else {
      this.fetchPage();
    }
  }

  changePageSize(newSize: number): void {
    this.size = Number(newSize);
    this.page = 0;
    if (this.isSearchMode) {
      // Determine which search to perform based on current state
      if (this.selectedDevice && this.selectedDevice !== '') {
        this.performSearch('device', this.selectedDevice);
      } else if (this.selectedAction && this.selectedAction !== '') {
        this.performSearch('action', this.selectedAction);
      } else if (this.searchId && this.searchId > 0) {
        this.performSearch('id', this.searchId.toString());
      } else if (this.searchTime && this.searchTime.trim() !== '') {
        this.performSearch('time', this.searchTime.trim());
      } else {
        this.fetchPage();
      }
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
      // Determine which search to perform based on current state
      if (this.selectedDevice && this.selectedDevice !== '') {
        this.performSearch('device', this.selectedDevice);
      } else if (this.selectedAction && this.selectedAction !== '') {
        this.performSearch('action', this.selectedAction);
      } else if (this.searchId && this.searchId > 0) {
        this.performSearch('id', this.searchId.toString());
      } else if (this.searchTime && this.searchTime.trim() !== '') {
        this.performSearch('time', this.searchTime.trim());
      } else {
        this.fetchPage();
      }
    } else {
      this.fetchPage();
    }
  }

  // Map frontend column names to backend field names
  private mapColumnName(column: string): string {
    const columnMapping: { [key: string]: string } = {
      'id': 'id',
      'device': 'device',
      'action': 'action',
      'time': 'time'
    };
    return columnMapping[column] || column;
  }

  // Map frontend column names to database column names for native queries
  private mapColumnNameForNativeQuery(column: string): string {
    const columnMapping: { [key: string]: string } = {
      'id': 'id',
      'device': 'device',
      'action': 'action',
      'time': 'time'
    };
    return columnMapping[column] || column;
  }


  copyToClipboard(timeString: string): void {
    // Format time string to yyyy-MM-dd HH:mm:ss format
    const date = new Date(timeString);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    const seconds = String(date.getSeconds()).padStart(2, '0');
    
    const formattedTime = `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
    
    navigator.clipboard.writeText(formattedTime).then(() => {
      // You can add a toast notification here if needed
      console.log('Đã copy thời gian:', formattedTime);
    }).catch(err => {
      console.error('Lỗi khi copy:', err);
    });
  }

}


