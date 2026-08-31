import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { SidebarComponent } from '../sidebar/sidebar';
import { TopbarComponent } from '../topbar/topbar';
import { NotificationComponent } from '../../shared/components/notification/notification';

@Component({
  selector: 'app-app-layout',
  imports: [RouterOutlet, SidebarComponent, TopbarComponent, NotificationComponent],
  templateUrl: './app-layout.html',
  styleUrl: './app-layout.scss',
})
export class AppLayoutComponent {}
