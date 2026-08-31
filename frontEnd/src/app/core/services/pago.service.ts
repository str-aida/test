import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { PagoResponse } from '../models/pago-response';

@Injectable({
  providedIn: 'root'
})
export class PagoService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.baseUrl}/pagos`;

  crearPago(pedidoId: number): Observable<PagoResponse> {
    return this.http.post<PagoResponse>(`${this.apiUrl}/${pedidoId}`, {});
  }
}
