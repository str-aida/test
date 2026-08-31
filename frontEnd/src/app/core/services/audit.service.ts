import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { LogSistemaResponse } from '../models/log-sistema-response';
import { LogSistemaFilter } from '../models/log-sistema-filter';
import { PageResponse } from '../models/page-response';

@Injectable({
  providedIn: 'root'
})
export class AuditService {

  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.baseUrl}/logs`;

  listarLogs(page: number = 0, size: number = 20, filter?: LogSistemaFilter): Observable<PageResponse<LogSistemaResponse>> {
    let params = new HttpParams()
      .set('page', page)
      .set('size', size);

    if (filter) {
      if (filter.accion && filter.accion.trim()) {
        params = params.set('accion', filter.accion.trim());
      }
      if (filter.rol) {
        params = params.set('rol', filter.rol);
      }
      if (filter.usuario && filter.usuario.trim()) {
        params = params.set('usuario', filter.usuario.trim());
      }
    }

    return this.http.get<PageResponse<LogSistemaResponse>>(this.apiUrl, { params });
  }

  exportarPdf(filter?: LogSistemaFilter): Observable<Blob> {
    let params = new HttpParams();

    if (filter) {
      if (filter.accion && filter.accion.trim()) {
        params = params.set('accion', filter.accion.trim());
      }
      if (filter.rol) {
        params = params.set('rol', filter.rol);
      }
      if (filter.usuario && filter.usuario.trim()) {
        params = params.set('usuario', filter.usuario.trim());
      }
    }

    return this.http.get(`${this.apiUrl}/exportar-pdf`, {
      params,
      responseType: 'blob'
    });
  }

}

