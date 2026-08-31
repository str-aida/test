import { Component, inject } from '@angular/core';
import { NotificationService } from '../../../core/services/notification.service';
import { LucideCircleCheck, LucideCircleX, LucideX } from '@lucide/angular';

@Component({
  selector: 'app-notification',
  imports: [LucideCircleCheck, LucideCircleX, LucideX],
  templateUrl: './notification.html',
  styleUrl: './notification.scss',
})
export class NotificationComponent {

  private readonly notificationService = inject(NotificationService);

  readonly notifications = this.notificationService.notifications$;

  remove(id: number): void {
    this.notificationService.remove(id);
  }

}
