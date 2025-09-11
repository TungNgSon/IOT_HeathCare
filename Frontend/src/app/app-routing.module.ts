import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { DashboardComponent } from './components/dashboard/dashboard.component';
import { HomeComponent } from './components/home/home.component';
import { SensorsListComponent } from './components/sensors-list/sensors-list.component';
import { DeviceActionListComponent } from './components/device-action-list/device-action-list.component';
import { ProfileComponent } from './components/profile/profile.component';

const routes: Routes = [
  // Định nghĩa route cho dashboard
  { path: 'dashboard', component: DashboardComponent },
  { path: 'home', component: HomeComponent },
  { path: 'sensors', component: SensorsListComponent },
  { path: 'device-actions', component: DeviceActionListComponent },
  { path: 'profile', component: ProfileComponent },

  // Route mặc định, chuyển hướng đến 'dashboard' khi người dùng truy cập trang chủ
  { path: '', redirectTo: '/dashboard', pathMatch: 'full' },

  // Route wildcard cho các URL không tồn tại, có thể hiển thị trang 404
  { path: '**', redirectTo: '' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }