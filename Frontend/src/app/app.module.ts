import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgChartsModule } from 'ng2-charts';
// Angular Material Modules
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';

// Routing
import { AppRoutingModule } from './app-routing.module';
import { RouterModule } from '@angular/router';

// Components
import { AppComponent } from './app.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { VitalsSummaryComponent } from './components/vitals-summary/vitals-summary.component';
import { VitalCardComponent } from './components/vital-card/vital-card.component';
import { SmartLightingControllComponent } from './components/smart-lighting-controll/smart-lighting-controll.component';
import { LightingSwitchComponent } from './components/lighting-switch/lighting-switch.component';
import { SensorsListComponent } from './components/sensors-list/sensors-list.component';
import { DeviceActionListComponent } from './components/device-action-list/device-action-list.component';
import { ProfileComponent } from './components/profile/profile.component';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { HeaderComponent } from './components/header/header.component';


@NgModule({
  declarations: [
    AppComponent,
    DashboardComponent,
    VitalsSummaryComponent,
    VitalCardComponent,
    SmartLightingControllComponent,
    LightingSwitchComponent,
    SensorsListComponent,
    DeviceActionListComponent,
    ProfileComponent,
    LoginComponent,
    RegisterComponent,
    HeaderComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    RouterModule,
    HttpClientModule,
    BrowserAnimationsModule,
    MatCardModule,
    MatIconModule,
    MatSlideToggleModule,
    NgChartsModule,
    FormsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }