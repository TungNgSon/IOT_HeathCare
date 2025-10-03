import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss']
})
export class SidebarComponent implements OnInit {
  menuItems = [
    {
      title: 'Dashboard',
      icon: '🏠',
      route: '/dashboard',
      active: false
    },
    {
      title: 'Sensors Data',
      icon: '📊',
      route: '/sensors',
      active: false
    },
    {
      title: 'Device Actions',
      icon: '⚡',
      route: '/device-actions',
      active: false
    },
    {
      title: 'Profile',
      icon: '👤',
      route: '/profile',
      active: false
    }
  ];

  constructor(
    private router: Router,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.updateActiveItem();
    this.router.events.subscribe(() => {
      this.updateActiveItem();
    });
  }

  updateActiveItem(): void {
    const currentRoute = this.router.url;
    this.menuItems.forEach(item => {
      item.active = item.route === currentRoute;
    });
  }

  navigateTo(route: string): void {
    this.router.navigate([route]);
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
