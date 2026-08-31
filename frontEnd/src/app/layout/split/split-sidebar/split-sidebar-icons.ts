import { SidebarIcon, SidebarType } from './split-sidebar.model';

// Este archivo solamente dice:
// Si el sidebar es LOGIN, estas son las tarjetas.
export const SIDEBAR_ICONS: Record<SidebarType, SidebarIcon[]> = {

  establishment: [
    'store',
    'map-pin',
    'phone'
  ],

  admin: [
    'shield-check',
    'users',
    'key-round'
  ],

  login: [
    'shopping-cart',
    'store',
    'chart-column'
  ],

  'forgot-password': [],
  'register-client': []

};