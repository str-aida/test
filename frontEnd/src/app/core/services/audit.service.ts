import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { LogSistemaResponse } from '../models/log-sistema-response';
import { LogSistemaFilter } from '../models/log-sistema-filter';

@Injectable({
  providedIn: 'root'
})
export class AuditService {

  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.baseUrl}/logs`;

  listarLogs(filter?: LogSistemaFilter): Observable<LogSistemaResponse[]> {
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

    return this.http.get<LogSistemaResponse[]>(this.apiUrl, { params });
  }

}
