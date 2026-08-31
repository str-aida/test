export type NotificationType = 'success' | 'error';

export interface Notification {

    id: number;
    type: NotificationType;
    message: string;
    duration: number;
  
}