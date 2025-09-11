import { Component } from '@angular/core';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss']
})
export class ProfileComponent {
  user = {
    initials: 'AJ',
    name: 'Nguyen Son Tung',
    studentId: 'B22DCCN768',
    email: 'TungNS.B22CN768@stu.ptit.edu.vn',
    github: 'TungNgSon',
    pdfLabel: 'PDF'
  };
}


