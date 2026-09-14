import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Observable, catchError, map, throwError, timeout } from 'rxjs';
import { TokenService } from './token.service';
import { environment } from '../../../environments/environment';

export interface CopilotMessage {
  id: string;
  sender: 'user' | 'assistant';
  text: string;
  timestamp: Date;
  isError?: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class CopilotService {
  private readonly http = inject(HttpClient);
  private readonly tokenService = inject(TokenService);
  private readonly rpcUrl = environment.copilotRpcUrl || '/a2a/app';
  private currentContextId: string | null = null;

  /**
   * Resets the conversation context ID for a fresh session.
   */
  resetContext(): void {
    this.currentContextId = null;
  }

  /**
   * Gets the current conversation context ID.
   */
  getContextId(): string | null {
    return this.currentContextId;
  }

  /**
   * Sends a user query to the A2A server.
   * Format: JSON-RPC 2.0 message/send payload.
   */
  sendMessage(userQuery: string): Observable<string> {
    const messageId = `msg-${Date.now()}-${Math.random().toString(36).substring(2, 9)}`;

    const params: any = {
      message: {
        role: 'user',
        parts: [
          {
            kind: 'text',
            text: userQuery
          }
        ],
        messageId: messageId
      },
      metadata: {}
    };

    if (this.currentContextId) {
      params.contextId = this.currentContextId;
      params.message.contextId = this.currentContextId;
    }

    const payload = {
      jsonrpc: '2.0',
      id: messageId,
      method: 'message/send',
      params: params
    };

    // Ensure Bearer JWT is attached
    let headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });

    const token = this.tokenService.getToken();
    if (token) {
      headers = headers.set('Authorization', `Bearer ${token}`);
    }

    return this.http.post<any>(this.rpcUrl, payload, { headers }).pipe(
      timeout(45000), // 45 second timeout for AI generation
      map((response) => {
        if (response.error) {
          throw new Error(response.error.message || 'Error en el servicio A2A');
        }

        const result = response.result;
        if (!result) {
          throw new Error('Respuesta inválida del asistente');
        }

        // Persist contextId for multi-turn conversation context
        if (result.contextId) {
          this.currentContextId = result.contextId;
        }

        // Extract response text from artifacts or message parts
        let extractedText = '';

        if (Array.isArray(result.artifacts)) {
          for (const artifact of result.artifacts) {
            if (Array.isArray(artifact.parts)) {
              for (const part of artifact.parts) {
                if (part.kind === 'text' && part.text) {
                  extractedText += part.text + '\n';
                }
              }
            }
          }
        }

        if (!extractedText.trim() && result.message?.parts) {
          for (const part of result.message.parts) {
            if (part.kind === 'text' && part.text) {
              extractedText += part.text + '\n';
            }
          }
        }

        if (!extractedText.trim()) {
          extractedText = 'El asistente respondió pero no se generó texto de salida.';
        }

        return extractedText.trim();
      }),
      catchError((error: unknown) => {
        let userFacingError = 'No pudimos comunicarnos con Gestia Copilot.';
        const err = error as { status?: number; name?: string; message?: string };

        if (err.status === 401) {
          userFacingError = 'Tu sesión no es válida o ha expirado. Por favor, vuelve a iniciar sesión.';
        } else if (err.status === 0 || err.name === 'TimeoutError') {
          userFacingError = 'El asistente está tardando demasiado en responder o el servidor no está disponible. Intentá nuevamente.';
        } else if (err.status && err.status >= 500) {
          userFacingError = 'No pudimos comunicarnos con Gestia Copilot. Intenta más tarde.';
        }

        return throwError(() => new Error(userFacingError));
      })
    );
  }
}
