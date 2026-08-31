import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { CuponUsuarioResponse } from '../models/cupon-usuario-response';
import { CuponResponse } from '../models/cupon-response';
import { CreateCuponRequest } from '../models/create-cupon-request';
import { UpdateCuponRequest } from '../models/update-cupon-request';
import { AsignarCuponRequest } from '../models/asignar-cupon-request';

@Injectable({
  providedIn: 'root'
})
export class CuponService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.baseUrl}/cupones`;

  // === ADMIN ===

  listarCupones(): Observable<CuponResponse[]> {
    return this.http.get<CuponResponse[]>(this.apiUrl);
  }

  obtenerCuponPorId(id: number): Observable<CuponResponse> {
    return this.http.get<CuponResponse>(`${this.apiUrl}/${id}`);
  }

  crearCupon(request: CreateCuponRequest): Observable<CuponResponse> {
    return this.http.post<CuponResponse>(this.apiUrl, request);
  }

  editarCupon(id: number, request: UpdateCuponRequest): Observable<CuponResponse> {
    return this.http.put<CuponResponse>(`${this.apiUrl}/${id}`, request);
  }

  desactivarCupon(id: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}/desactivar`, {});
  }

  asignarCupon(request: AsignarCuponRequest): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/asignar`, request);
  }

  // === CLIENTE ===

  misCupones(): Observable<CuponUsuarioResponse[]> {
    return this.http.get<CuponUsuarioResponse[]>(`${this.apiUrl}/mis-cupones`);
  }
}

