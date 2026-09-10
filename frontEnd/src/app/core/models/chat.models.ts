export type EstadoConversacion = 'ABIERTA' | 'EN_ATENCION' | 'CERRADA';
export type CerradoPor = 'CLIENTE' | 'AGENTE';

export interface ChatConversacionResponse {
  id: number;
  pedidoId: number;
  numeroPedido: string;
  clienteId: number;
  nombreCliente: string;
  agenteId: number | null;
  nombreAgente: string | null;
  estado: EstadoConversacion;
  cerradoPor: CerradoPor | null;
  fechaCreacion: string;
  mensajesNoLeidos: number;
}

export interface ChatMensajeResponse {
  id: number;
  remitenteId: number;
  nombreRemitente: string;
  rolRemitente: string;
  esSistema: boolean;
  contenido: string;
  fechaEnvio: string;
  leido: boolean;
}

export interface CrearChatRequest {
  pedidoId: number;
}

export interface EnviarMensajeRequest {
  contenido: string;
}

export interface ChatErrorPayloadResponse {
  mensaje: string;
  fecha: string;
}

export interface PedidoSnapshot {
  numeroPedido: string;
  total: number;
  estado: string;
  cantidadItems: number;
}

export interface ChatLecturaResponse {
  conversacionId: number;
  lectorId: number;
  fechaLectura: string;
}

