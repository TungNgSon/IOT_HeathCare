import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { environment } from '../../environments/environment';

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  password: string;
  confirmPassword: string;
}

export interface AuthResponse {
  id: number;
  username: string;
  token?: string; // Token có thể được trả về từ backend hoặc frontend tự tạo
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/users'; // Thay đổi URL theo backend của bạn

  constructor(private http: HttpClient) { }

  login(credentials: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, credentials)
      .pipe(
        catchError(this.handleError)
      );
  }

  register(userData: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/register`, userData)
      .pipe(
        catchError(this.handleError)
      );
  }

  private handleError(error: HttpErrorResponse): Observable<never> {
    let errorMessage = 'An error occurred';
    
    if (error.error instanceof ErrorEvent) {
      // Client-side error
      errorMessage = `Error: ${error.error.message}`;
    } else {
      // Server-side error
      if (error.status === 0) {
        errorMessage = 'Unable to connect to server. Please check your connection.';
      } else if (error.status === 401) {
        errorMessage = 'Invalid username or password';
      } else if (error.status === 400) {
        errorMessage = error.error || 'Bad request';
      } else if (error.status === 409) {
        errorMessage = 'Username already exists';
      } else {
        errorMessage = `Server error: ${error.status} - ${error.error || error.message}`;
      }
    }
    
    return throwError(() => new Error(errorMessage));
  }

  // Lưu user info vào localStorage
  setUserInfo(userInfo: AuthResponse): void {
    localStorage.setItem('userInfo', JSON.stringify(userInfo));
    // Tạo một token đơn giản cho demo (trong thực tế backend sẽ tạo JWT)
    const token = btoa(`${userInfo.username}:${Date.now()}`);
    localStorage.setItem('token', token);
  }

  // Lấy user info từ localStorage
  getUserInfo(): AuthResponse | null {
    const userInfo = localStorage.getItem('userInfo');
    return userInfo ? JSON.parse(userInfo) : null;
  }

  // Lấy token từ localStorage
  getToken(): string | null {
    return localStorage.getItem('token');
  }

  // Xóa user info và token khỏi localStorage
  removeUserInfo(): void {
    localStorage.removeItem('userInfo');
    localStorage.removeItem('token');
  }

  // Kiểm tra xem user đã đăng nhập chưa
  isLoggedIn(): boolean {
    return !!this.getToken() && !!this.getUserInfo();
  }

  // Đăng xuất
  logout(): void {
    this.removeUserInfo();
  }
}
