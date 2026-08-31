import { TipoOperacion } from './enums/tipo-operacion.enum';

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
  fecha: string;
  descripcion: string;
  tipoOperacion: TipoOperacion;
}
