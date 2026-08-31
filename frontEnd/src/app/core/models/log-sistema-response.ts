import { TipoOperacion } from './enums/tipo-operacion.enum';
import { UserRole } from './enums/user-role.enum';

export interface LogSistemaResponse {
  id: number;
  tablaAfectada: string;
  idRegistro: number;
  referencia: string;
  accion: string;
  campoModificado: string;
  valorAnterior: string;
  valorNuevo: string;
  usuario: string;
  rol?: UserRole;
  fecha: string;
  descripcion: string;
  tipoOperacion: TipoOperacion;
}

