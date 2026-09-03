import { TipoServicio } from './enums/tipo-servicio.enum';

export interface EstablecimientoClienteResponse {
  id: number;
  nombre: string;
  tipoServicio: TipoServicio;
}
