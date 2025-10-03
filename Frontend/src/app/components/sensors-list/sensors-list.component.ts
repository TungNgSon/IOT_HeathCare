import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';

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
  searchValue?: string | number;

  // Data
  rows: DataSensor[] = [];
  totalElements = 0;
  totalPages = 0;
  loading = false;
  errorMessage = '';
  isSearchMode = false;

  private readonly baseUrl = 'http://localhost:8080/api/sensors';

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

    this.http.get<PageResponse<DataSensor>>(this.baseUrl + '/page', { params }).subscribe({
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

  search(resetPage: boolean = true): void {
    this.loading = true;
    this.errorMessage = '';
    this.isSearchMode = true;
    if (resetPage) {
      this.page = 0; // Reset to first page when starting new search
    }

    // Kiểm tra nếu không có giá trị tìm kiếm thì load tất cả
    if (!this.searchValue || this.searchValue === '') {
      this.resetSearchAndReload();
      return;
    }

    let params = new HttpParams()
      .set('column', this.column)
      .set('page', this.page.toString())
      .set('size', this.size.toString())
      .set('sortBy', this.mapColumnNameForNativeQuery(this.sortBy))
      .set('sortDir', this.sortDir);

    if (this.column === 'time') {
      params = params.set('timeValue', String(this.searchValue));
    } else {
      params = params.set('numericValue', String(this.searchValue));
    }

    this.http.get<PageResponse<DataSensor>>(this.baseUrl + '/search-exact', { params }).subscribe({
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
    this.searchValue = undefined;
    this.page = 0;
    this.isSearchMode = false;
    this.fetchPage();
  }

  goToPage(target: number): void {
    if (target < 0 || target >= this.totalPages) return;
    this.page = target;
    if (this.isSearchMode) {
      this.search(false); // Don't reset page when paginating
    } else {
      this.fetchPage();
    }
  }

  changePageSize(newSize: number): void {
    this.size = Number(newSize);
    this.page = 0;
    if (this.isSearchMode) {
      this.search(false); // Don't reset page when changing page size (already reset above)
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
      this.search(false); // Don't reset page when sorting (already reset above)
    } else {
      this.fetchPage();
    }
  }

  // Map frontend column names to backend field names
  private mapColumnName(column: string): string {
    const columnMapping: { [key: string]: string } = {
      'heartRate': 'heartRate',
      'bodyTemperature': 'bodyTemperature', 
      'SPO2': 'SPO2',
      'id': 'id',
      'time': 'time'
    };
    return columnMapping[column] || column;
  }

  // Map frontend column names to database column names for native queries
  private mapColumnNameForNativeQuery(column: string): string {
    const columnMapping: { [key: string]: string } = {
      'heartRate': 'heart_rate',
      'bodyTemperature': 'body_temperature', 
      'SPO2': 'SPO2',
      'id': 'id',
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


