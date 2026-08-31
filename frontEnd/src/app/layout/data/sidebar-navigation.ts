import { NavigationItem } from '../models/navigation-item.model';

export const ADMIN_NAVIGATION: NavigationItem[] = [
  {
    id: 'dashboard',
    label: 'Dashboard',
    route: '/admin/dashboard'
  },
  {
    id: 'pedidos-en-curso',
    label: 'Pedidos en Curso',
    route: '/admin/pedidos/en-curso'
  },
  {
    id: 'pedidos',
    label: 'Todos los Pedidos',
    route: '/admin/pedidos'
  },
  {
    id: 'productos',
    label: 'Productos',
    route: '/admin/productos'
  },
  {
    id: 'categorias',
    label: 'Categorías',
    route: '/admin/categorias'
  },
  {
    id: 'cupones',
    label: 'Cupones',
    route: '/admin/cupones'
  },
  {
    id: 'personal',
    label: 'Personal',
    route: '/admin/personal'
  },
  {
    id: 'configuracion',
    label: 'Configuración',
    route: '/admin/configuracion'
  },
  {
    id: 'auditoria',
    label: 'Auditoría',
    route: '/admin/auditoria'
  },
  {
    id: 'perfil',
    label: 'Perfil',
    route: '/admin/perfil'
  }
];

export const EMPLOYEE_NAVIGATION: NavigationItem[] = [
  {
    id: 'pedidos-en-curso',
    label: 'Pedidos en Curso',
    route: '/empleado/pedidos/en-curso'
  },
  {
    id: 'pedidos',
    label: 'Todos los Pedidos',
    route: '/empleado/pedidos'
  },
  {
    id: 'productos',
    label: 'Productos',
    route: '/empleado/productos'
  },
  {
    id: 'perfil',
    label: 'Perfil',
    route: '/empleado/perfil'
  }
];

export const CLIENT_NAVIGATION: NavigationItem[] = [
  {
    id: 'inicio',
    label: 'Inicio',
    route: '/cliente'
  },
  {
    id: 'mis-pedidos',
    label: 'Mis pedidos',
    route: '/cliente/pedidos'
  },
  {
    id: 'mis-cupones',
    label: 'Mis cupones',
    route: '/cliente/cupones'
  },
  {
    id: 'perfil',
    label: 'Perfil',
    route: '/cliente/perfil'
  },
  {
    id: 'cuenta',
    label: 'Cuenta',
    route: '/cliente/cuenta'
  }
];