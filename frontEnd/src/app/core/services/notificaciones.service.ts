import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { NotificacionResponse } from '../models/notificacion-response';

@Injectable({
  providedIn: 'root'
})
export class NotificacionesService {

  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.baseUrl}/notificaciones`;

  /**
   * Obtiene las últimas 10 notificaciones del usuario autenticado.
   * GET /notificaciones
   */
  obtenerMisNotificaciones(): Observable<NotificacionResponse[]> {
    return this.http.get<NotificacionResponse[]>(this.apiUrl);
  }

  /**
   * Obtiene el conteo de notificaciones no leídas.
   * GET /notificaciones/no-leidas
   */
  contarNoLeidas(): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/no-leidas`);
  }

  /**
   * Marca una notificación específica como leída.
   * PATCH /notificaciones/{id}/leida
   */
  marcarComoLeida(id: number): Observable<void> {
    return this.http.patch<void>(`${this.apiUrl}/${id}/leida`, {});
  }

}
