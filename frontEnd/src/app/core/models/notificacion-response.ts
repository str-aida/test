import { TipoNotificacion } from './enums/tipo-notificacion.enum';
import { TipoReferencia } from './enums/tipo-referencia.enum';

export interface NotificacionResponse {
  id: number;
  titulo: string;
  mensaje: string;
  leida: boolean;
  fecha: string;
  tipo: TipoNotificacion;
  tipoReferencia: TipoReferencia | null;
  referenciaId: number | null;
}
