import { inject, Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { TokenService } from './token.service';
import { NotificacionResponse } from '../models/notificacion-response';
import { TipoReferencia } from '../models/enums/tipo-referencia.enum';
import { UserRole } from '../models/enums/user-role.enum';

@Injectable({
  providedIn: 'root'
})
export class NotificacionNavigationService {

  private readonly tokenService = inject(TokenService);
  private readonly router = inject(Router);

  /**
   * Obtiene el prefijo de rol del usuario autenticado (admin, empleado, cliente).
   * Utiliza TokenService y cuenta con un mecanismo de respaldo según la URL actual.
   */
  obtenerRolePrefix(): 'admin' | 'empleado' | 'cliente' {
    const role = this.tokenService.getRole();
    const roleStr = role ? String(role).toUpperCase() : '';

    if (roleStr === UserRole.ADMIN || roleStr === 'ADMIN') {
      return 'admin';
    }
    if (roleStr === UserRole.EMPLEADO || roleStr === 'EMPLEADO') {
      return 'empleado';
    }
    if (roleStr === UserRole.CLIENTE || roleStr === 'CLIENTE') {
      return 'cliente';
    }

    // Respaldo por la URL actual en caso de que TokenService no retorne el rol
    const currentUrl = this.router.url;
    if (currentUrl.startsWith('/admin')) {
      return 'admin';
    }
    if (currentUrl.startsWith('/empleado')) {
      return 'empleado';
    }
    return 'cliente';
  }

  /**
   * Resuelve dinámicamente la ruta de destino para cualquier notificación según:
   * - tipoReferencia
   * - referenciaId
   * - rol del usuario autenticado
   * 
   * Retorna una URL válida del frontend o null si no existe una pantalla/ruta para ese recurso.
   */
  resolverDestino(notificacion: NotificacionResponse): string | null {
    if (!notificacion.tipoReferencia) {
      return null;
    }

    const tipoRef = String(notificacion.tipoReferencia).toUpperCase();
    const refId = notificacion.referenciaId != null ? Number(notificacion.referenciaId) : null;
    const rolePrefix = this.obtenerRolePrefix();

    switch (tipoRef) {
      case TipoReferencia.PEDIDO:
      case 'PEDIDO':
        if (refId !== null && !isNaN(refId)) {
          return `/${rolePrefix}/pedidos/${refId}`;
        }
        return `/${rolePrefix}/pedidos`;

      case TipoReferencia.CUPON:
      case 'CUPON':
        if (rolePrefix === 'admin') {
          return '/admin/cupones';
        }
        if (rolePrefix === 'cliente') {
          return '/cliente/cupones';
        }
        return null;

      case TipoReferencia.PRODUCTO:
      case 'PRODUCTO':
        if (rolePrefix === 'admin') {
          if (refId !== null && !isNaN(refId)) {
            return `/admin/productos/editar/${refId}`;
          }
          return '/admin/productos';
        }
        if (rolePrefix === 'empleado') {
          return '/empleado/productos';
        }
        if (rolePrefix === 'cliente') {
          return '/cliente/productos';
        }
        return null;

      case TipoReferencia.PAGO:
      case 'PAGO':
        // Actualmente no existe una ruta exclusiva de pagos en app.routes.ts.
        // Se marca como leída sin navegar para evitar errores 404.
        return null;

      default:
        return null;
    }
  }

  /**
   * Ejecuta la navegación hacia la ruta resuelta si esta existe.
   * Retorna true si navegó, false en caso contrario.
   */
  navegar(notificacion: NotificacionResponse): boolean {
    const destinoUrl = this.resolverDestino(notificacion);
    console.log(`[NotificationNavigation] Resolviendo notificación ID ${notificacion.id} (TipoRef: ${notificacion.tipoReferencia}, RefId: ${notificacion.referenciaId}) -> Destino resuelto: ${destinoUrl}`);
    if (destinoUrl) {
      this.router.navigateByUrl(destinoUrl).then(success => {
        console.log(`[NotificationNavigation] Resultado de navegación hacia ${destinoUrl}: ${success}`);
      });
      return true;
    }
    console.log(`[NotificationNavigation] Sin ruta de destino navegable para notificación ID ${notificacion.id}`);
    return false;
  }

}
