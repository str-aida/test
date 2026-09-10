import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  ChatConversacionResponse,
  ChatMensajeResponse,
  CrearChatRequest,
  EstadoConversacion
} from '../models/chat.models';

export interface SpringPage<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  last: boolean;
  first: boolean;
  number: number;
  size: number;
  empty: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class ChatService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.baseUrl}/chats`;

  crearOReutilizarChat(pedidoId: number): Observable<ChatConversacionResponse> {
    const body: CrearChatRequest = { pedidoId };
    return this.http.post<ChatConversacionResponse>(this.apiUrl, body);
  }

  listar(
    estado?: EstadoConversacion,
    page: number = 0,
    size: number = 20
  ): Observable<SpringPage<ChatConversacionResponse>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    if (estado) {
      params = params.set('estado', estado);
    }
    return this.http.get<SpringPage<ChatConversacionResponse>>(this.apiUrl, { params });
  }

  asignar(conversacionId: number): Observable<ChatConversacionResponse> {
    return this.http.patch<ChatConversacionResponse>(
      `${this.apiUrl}/${conversacionId}/asignar`,
      {}
    );
  }

  obtenerDetalle(conversacionId: number): Observable<ChatConversacionResponse> {
    return this.http.get<ChatConversacionResponse>(`${this.apiUrl}/${conversacionId}`);
  }

  obtenerMensajes(
    conversacionId: number,
    page: number = 0,
    size: number = 20
  ): Observable<SpringPage<ChatMensajeResponse>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<SpringPage<ChatMensajeResponse>>(
      `${this.apiUrl}/${conversacionId}/mensajes`,
      { params }
    );
  }

  cerrar(conversacionId: number): Observable<ChatConversacionResponse> {
    return this.http.patch<ChatConversacionResponse>(
      `${this.apiUrl}/${conversacionId}/cerrar`,
      {}
    );
  }
}
