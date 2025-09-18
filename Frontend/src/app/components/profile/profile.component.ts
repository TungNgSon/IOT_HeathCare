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
    phone: '0329782218',
    email: 'TungNS.B22CN768@stu.ptit.edu.vn',
    github: 'https://github.com/TungNgSon/IOT_HeathCare',
    pdfLabel: 'https://drive.google.com/file/d/16MWyx2QluZn5Q4GyYlYvpsE5xflxw4iZ/view?usp=sharing'
  };
}


