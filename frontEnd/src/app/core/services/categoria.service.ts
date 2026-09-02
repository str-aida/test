import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { CategoriaResponse } from '../models/categoria-response';
import { CreateCategoriaRequest } from '../models/create-categoria-request';
import { UpdateCategoriaRequest } from '../models/update-categoria-request';

@Injectable({
  providedIn: 'root'
})
export class CategoriaService {

  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.baseUrl}/categorias`;

  listarCategorias(): Observable<CategoriaResponse[]> {
    return this.http.get<CategoriaResponse[]>(
      this.apiUrl
    );
  }

  obtenerCategoriaPorId(id: number): Observable<CategoriaResponse> {
    return this.http.get<CategoriaResponse>(
      `${this.apiUrl}/${id}`
    );
  }

  crearCategoria(request: CreateCategoriaRequest): Observable<CategoriaResponse> {
    return this.http.post<CategoriaResponse>(
      this.apiUrl,
      request
    );
  }

  editarCategoria(id: number, request: UpdateCategoriaRequest): Observable<CategoriaResponse> {
    return this.http.put<CategoriaResponse>(
      `${this.apiUrl}/${id}`,
      request
    );
  }

}