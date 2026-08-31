import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { DireccionResponse } from '../models/direccion-response';

import { CreateDireccionRequest } from '../models/create-direccion-request';

@Injectable({
  providedIn: 'root'
})
export class DireccionService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.baseUrl}/direcciones`;

  listarDirecciones(): Observable<DireccionResponse[]> {
    return this.http.get<DireccionResponse[]>(this.apiUrl);
  }

  crearDireccion(request: CreateDireccionRequest): Observable<DireccionResponse> {
    return this.http.post<DireccionResponse>(this.apiUrl, request);
  }
}

