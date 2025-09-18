import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService, LoginRequest } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  loginData: LoginRequest = {
    username: '',
    password: ''
  };
  
  showPassword = false;
  rememberMe = false;
  isLoading = false;
  errorMessage = '';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
  }

  onLogin(): void {
    if (this.isLoading) return;
    
    this.isLoading = true;
    this.errorMessage = '';

    this.authService.login(this.loginData).subscribe({
      next: (response) => {
        this.authService.setUserInfo(response);
        this.router.navigate(['/dashboard']);
        this.isLoading = false;
      },
      error: (error) => {
        this.errorMessage = error.message || 'Invalid username or password';
        this.isLoading = false;
      }
    });
  }
}