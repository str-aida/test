import { Injectable, signal } from "@angular/core";
import { Notification, NotificationType } from "../models/notification.model";

@Injectable({
  providedIn: 'root'
})
export class NotificationService {

    private readonly notifications = signal<Notification[]>([]);
    readonly notifications$ = this.notifications.asReadonly();
    private nextId = 0;

    show(
        type: NotificationType,
        message: string,
        duration: number
    ): void {
        const notification: Notification = {
            id: ++this.nextId,
            type,
            message,
            duration
        };

        this.notifications.update(current => [
            ...current,
            notification
        ]);

        setTimeout(() => {
            this.remove(notification.id);
        }, duration);
    }

    success(message: string): void {
        this.show('success', message, 4000);
    }

    error(message: string): void {
        this.show('error', message, 5000);
    }

    remove(id: number): void {
        this.notifications.update(current =>
            current.filter(notification => notification.id !== id)
        );
    }

    clear(): void {
        this.notifications.set([]);
    }

}