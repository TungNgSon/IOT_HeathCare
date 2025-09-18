import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { DashboardComponent } from './components/dashboard/dashboard.component';
import { HomeComponent } from './components/home/home.component';
import { SensorsListComponent } from './components/sensors-list/sensors-list.component';
import { DeviceActionListComponent } from './components/device-action-list/device-action-list.component';
import { ProfileComponent } from './components/profile/profile.component';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { AuthGuard } from './guards/auth.guard';
import { GuestGuard } from './guards/guest.guard';

const routes: Routes = [
  // Auth routes - chỉ cho phép guest (chưa đăng nhập)
  { path: 'login', component: LoginComponent, canActivate: [GuestGuard] },
  { path: 'register', component: RegisterComponent, canActivate: [GuestGuard] },
  
  // Protected routes - chỉ cho phép user đã đăng nhập
  { path: 'dashboard', component: DashboardComponent, canActivate: [AuthGuard] },
  { path: 'home', component: HomeComponent, canActivate: [AuthGuard] },
  { path: 'sensors', component: SensorsListComponent, canActivate: [AuthGuard] },
  { path: 'device-actions', component: DeviceActionListComponent, canActivate: [AuthGuard] },
  { path: 'profile', component: ProfileComponent, canActivate: [AuthGuard] },

  // Route mặc định, chuyển hướng đến 'login' khi người dùng truy cập trang chủ
  { path: '', redirectTo: '/login', pathMatch: 'full' },

  // Route wildcard cho các URL không tồn tại, chuyển hướng đến login
  { path: '**', redirectTo: '/login' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }