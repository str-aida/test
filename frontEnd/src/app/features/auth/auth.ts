import { Component, inject } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { LOGIN_SIDEBAR } from './data/login-sidebar.data';
import { FORGOT_PASSWORD_SIDEBAR } from './data/forgot-password-sidebar.data';
import { REGISTER_CLIENT_SIDEBAR } from './data/register-client-sidebar.data';
import { SplitLayoutComponent } from "../../layout/split/split-layout/split-layout";
import { SplitSidebarComponent } from "../../layout/split/split-sidebar/split-sidebar";
import { NotificationComponent } from '../../shared/components/notification/notification';


@Component({
  selector: 'app-auth',
  standalone: true,
  imports: [SplitLayoutComponent, SplitSidebarComponent, RouterOutlet, NotificationComponent],
  templateUrl: './auth.html',
  styleUrl: './auth.scss',
})
export class AuthComponent {

  private readonly router = inject(Router);

  get sidebarInfo() {

    switch (this.router.url) {

      case '/login':
        return LOGIN_SIDEBAR;

      case '/forgot-password':
      case '/reset-password':
        return FORGOT_PASSWORD_SIDEBAR;
      
      case '/register':
      return REGISTER_CLIENT_SIDEBAR;

      default:
        return LOGIN_SIDEBAR;

    }

  }

}
