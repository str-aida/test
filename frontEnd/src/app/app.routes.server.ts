import { RenderMode, ServerRoute } from '@angular/ssr';

export const serverRoutes: ServerRoute[] = [
  {
    path: 'cliente/pedidos/:id',
    renderMode: RenderMode.Server
  },
  {
    path: 'admin/pedidos/:id',
    renderMode: RenderMode.Server
  },
  {
    path: 'empleado/pedidos/:id',
    renderMode: RenderMode.Server
  },
  {
    path: '**',
    renderMode: RenderMode.Prerender
  }
];

