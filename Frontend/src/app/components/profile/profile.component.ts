import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss']
})
export class ProfileComponent implements OnInit {
  user = {
    initials: 'AJ',
    name: 'Nguyen Son Tung',
    studentId: 'B22DCCN768',
    phone: '0329782218',
    email: 'TungNS.B22CN768@stu.ptit.edu.vn',
    github: 'https://github.com/TungNgSon/IOT_HeathCare',
    pdfLabel: 'https://drive.google.com/file/d/1ig8YWbPDqdjyTWicjLVzSnNiQW59WK-R/view?usp=sharing',
    postman: 'https://www.postman.com/cloudy-escape-352770/workspace/iot-heathcare/request/42843361-541128fe-3e21-4c06-9848-8524cb22a1b7?action=share&creator=42843361&active-environment=42843361-c2707871-6843-4768-aa6d-e63bebecdb2e'
  };

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Kiểm tra authentication trước khi load data
    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['/login']);
      return;
    }
  }
}


