import { inject, Injectable, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { Client, StompSubscription } from '@stomp/stompjs';
import { BehaviorSubject, Observable, Subject } from 'rxjs';
import { environment } from '../../../environments/environment';
import { TokenService } from './token.service';
import {
  ChatConversacionResponse,
  ChatErrorPayloadResponse,
  ChatLecturaResponse,
  ChatMensajeResponse,
  EnviarMensajeRequest
} from '../models/chat.models';

@Injectable({
  providedIn: 'root'
})
export class ChatSocketService {
  private readonly platformId = inject(PLATFORM_ID);
  private readonly tokenService = inject(TokenService);

  private client: Client | null = null;
  private currentConversacionId: number | null = null;

  private mensajeSub: StompSubscription | null = null;
  private estadoSub: StompSubscription | null = null;
  private lecturaSub: StompSubscription | null = null;
  private errorSub: StompSubscription | null = null;

  // Subjects para eventos
  private readonly nuevoMensaje$ = new Subject<ChatMensajeResponse>();
  private readonly cambioEstado$ = new Subject<ChatConversacionResponse>();
  private readonly lectura$ = new Subject<ChatLecturaResponse>();
  private readonly errorNegocio$ = new Subject<ChatErrorPayloadResponse>();
  private readonly conectado$ = new BehaviorSubject<boolean>(false);
  private readonly reconectado$ = new Subject<void>();

  private isReconnecting = false;

  get mensajes$(): Observable<ChatMensajeResponse> {
    return this.nuevoMensaje$.asObservable();
  }

  get estado$(): Observable<ChatConversacionResponse> {
    return this.cambioEstado$.asObservable();
  }

  get lecturas$(): Observable<ChatLecturaResponse> {
    return this.lectura$.asObservable();
  }

  get errores$(): Observable<ChatErrorPayloadResponse> {
    return this.errorNegocio$.asObservable();
  }

  get conectado(): Observable<boolean> {
    return this.conectado$.asObservable();
  }

  get reconectado(): Observable<void> {
    return this.reconectado$.asObservable();
  }

  conectar(conversacionId: number): void {
    if (!isPlatformBrowser(this.platformId)) {
      return;
    }

    this.currentConversacionId = conversacionId;

    if (this.client && this.client.active) {
      // Ya conectado, solo renovar suscripciones para la conversación actual
      this.suscribirTopicos(conversacionId);
      return;
    }

    const token = this.tokenService.getToken() || '';
    const wsUrl = this.obtenerWebSocketUrl();

    this.client = new Client({
      brokerURL: wsUrl,
      connectHeaders: {
        token: token
      },
      reconnectDelay: 4000,
      heartbeatIncoming: 10000,
      heartbeatOutgoing: 10000,
      debug: (msg: string) => {
        console.log('[STOMP]', msg);
      },
      beforeConnect: () => {
        const freshToken = this.tokenService.getToken() || '';
        if (this.client) {
          this.client.connectHeaders = { token: freshToken };
        }
      },
      onConnect: () => {
        this.conectado$.next(true);

        if (this.isReconnecting) {
          this.reconectado$.next();
        }
        this.isReconnecting = false;

        if (this.currentConversacionId != null) {
          this.suscribirTopicos(this.currentConversacionId);
        }
      },
      onDisconnect: () => {
        this.conectado$.next(false);
        this.isReconnecting = true;
      },
      onWebSocketClose: () => {
        this.conectado$.next(false);
        this.isReconnecting = true;
      },
      onStompError: (frame) => {
        console.error('STOMP error:', frame.headers['message'], frame.body);
        this.errorNegocio$.next({
          mensaje: frame.headers['message'] || 'Error de conexión con el chat',
          fecha: new Date().toISOString()
        });
      }
    });

    this.client.activate();
  }

  private suscribirTopicos(conversacionId: number): void {
    if (!this.client || !this.client.connected) {
      return;
    }

    // Cancelar suscripciones previas si existieran
    this.desuscribirTopicos();

    // 1. Mensajes
    this.mensajeSub = this.client.subscribe(
      `/topic/chat/${conversacionId}/mensajes`,
      (message) => {
        try {
          const data: ChatMensajeResponse = JSON.parse(message.body);
          this.nuevoMensaje$.next(data);
        } catch (e) {
          console.error('Error parseando mensaje:', e);
        }
      }
    );

    // 2. Cambio de estado
    this.estadoSub = this.client.subscribe(
      `/topic/chat/${conversacionId}/estado`,
      (message) => {
        try {
          const data: ChatConversacionResponse = JSON.parse(message.body);
          this.cambioEstado$.next(data);
        } catch (e) {
          console.error('Error parseando cambio de estado:', e);
        }
      }
    );

    // 3. Confirmaciones de lectura en tiempo real
    this.lecturaSub = this.client.subscribe(
      `/topic/chat/${conversacionId}/leidos`,
      (message) => {
        try {
          const data: ChatLecturaResponse = JSON.parse(message.body);
          this.lectura$.next(data);
        } catch (e) {
          console.error('Error parseando evento de lectura:', e);
        }
      }
    );

    // 4. Errores de negocio
    this.errorSub = this.client.subscribe(
      '/user/queue/errors',
      (message) => {
        try {
          const data: ChatErrorPayloadResponse = JSON.parse(message.body);
          this.errorNegocio$.next(data);
        } catch (e) {
          console.error('Error parseando error de negocio:', e);
        }
      }
    );
  }

  enviarMensaje(conversacionId: number, contenido: string): boolean {
    if (!this.client || !this.client.connected) {
      return false;
    }

    const payload: EnviarMensajeRequest = { contenido };
    this.client.publish({
      destination: `/app/chat/${conversacionId}/enviar`,
      body: JSON.stringify(payload)
    });
    return true;
  }

  marcarLeido(conversacionId: number): boolean {
    if (!this.client || !this.client.connected) {
      return false;
    }

    this.client.publish({
      destination: `/app/chat/${conversacionId}/leer`,
      body: '{}'
    });
    return true;
  }

  private desuscribirTopicos(): void {
    if (this.mensajeSub) {
      this.mensajeSub.unsubscribe();
      this.mensajeSub = null;
    }
    if (this.estadoSub) {
      this.estadoSub.unsubscribe();
      this.estadoSub = null;
    }
    if (this.lecturaSub) {
      this.lecturaSub.unsubscribe();
      this.lecturaSub = null;
    }
    if (this.errorSub) {
      this.errorSub.unsubscribe();
      this.errorSub = null;
    }
  }

  desconectar(): void {
    this.desuscribirTopicos();
    this.currentConversacionId = null;
    if (this.client) {
      this.client.deactivate();
      this.client = null;
    }
    this.conectado$.next(false);
    this.isReconnecting = false;
  }

  private obtenerWebSocketUrl(): string {
    const base = environment.baseUrl || 'http://localhost:8080';
    const wsBase = base.replace(/^http:/, 'ws:').replace(/^https:/, 'wss:');
    return `${wsBase}/ws-chat`;
  }
}
