import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { DescuentoResponse } from '../models/descuento-response';
import { CreateDescuentoRequest } from '../models/create-descuento-request';
import { UpdateDescuentoRequest } from '../models/update-descuento-request';

export interface MensajeResponse {
  mensaje: string;
}

@Injectable({
  providedIn: 'root'
})
export class DescuentoService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.baseUrl}/descuentos`;

  listarDescuentos(): Observable<DescuentoResponse[]> {
    return this.http.get<DescuentoResponse[]>(this.apiUrl);
  }

  obtenerDescuentoPorId(id: number): Observable<DescuentoResponse> {
    return this.http.get<DescuentoResponse>(`${this.apiUrl}/${id}`);
  }

  crearDescuento(request: CreateDescuentoRequest): Observable<DescuentoResponse> {
    return this.http.post<DescuentoResponse>(this.apiUrl, request);
  }

  editarDescuento(id: number, request: UpdateDescuentoRequest): Observable<DescuentoResponse> {
    return this.http.put<DescuentoResponse>(`${this.apiUrl}/${id}`, request);
  }

  activarDescuento(id: number): Observable<MensajeResponse> {
    return this.http.patch<MensajeResponse>(`${this.apiUrl}/${id}/activar`, null);
  }

  desactivarDescuento(id: number): Observable<MensajeResponse> {
    return this.http.patch<MensajeResponse>(`${this.apiUrl}/${id}/desactivar`, null);
  }
}
