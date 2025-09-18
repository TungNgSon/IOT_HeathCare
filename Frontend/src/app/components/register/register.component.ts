import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService, RegisterRequest } from '../../services/auth.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent {
  registerData: RegisterRequest = {
    username: '',
    password: '',
    confirmPassword: ''
  };
  
  showPassword = false;
  showConfirmPassword = false;
  isLoading = false;
  errorMessage = '';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
  }

  toggleConfirmPasswordVisibility(): void {
    this.showConfirmPassword = !this.showConfirmPassword;
  }

  passwordsMatch(): boolean {
    return this.registerData.password === this.registerData.confirmPassword;
  }

  onRegister(): void {
    if (this.isLoading) return;
    
    this.isLoading = true;
    this.errorMessage = '';

    // Validate passwords match
    if (!this.passwordsMatch()) {
      this.errorMessage = 'Passwords do not match';
      this.isLoading = false;
      return;
    }

    this.authService.register(this.registerData).subscribe({
      next: (response) => {
        this.authService.setUserInfo(response);
        this.router.navigate(['/dashboard']);
        this.isLoading = false;
      },
      error: (error) => {
        this.errorMessage = error.message || 'Registration failed. Please try again.';
        this.isLoading = false;
      }
    });
  }
}