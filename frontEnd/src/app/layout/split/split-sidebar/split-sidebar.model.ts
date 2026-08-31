// Este archivo define qué información necesita cualquier sidebar.

export interface SplitSidebarCard {

  title: string;
  description: string;

}

export interface SplitSidebarInfo {

  type: SidebarType;
  eyebrow?: string;
  title: string;
  description: string;
  cards: SplitSidebarCard[];
  currentStep?: number;
  totalSteps?: number;
  footer?: string;

}

export type SidebarType =
| 'establishment'
| 'admin'
| 'login'
| 'forgot-password'
| 'register-client';

export type SidebarIcon =
| 'store'
| 'map-pin'
| 'phone'
| 'shield-check'
| 'users'
| 'key-round'
| 'shopping-cart'
| 'chart-column';