import { TipoAsignacionCupon } from './enums/tipo-asignacion-cupon.enum';
import { TipoDescuento } from './enums/tipo-descuento.enum';

export interface CreateCuponRequest {
  codigo: string;
  tipoDescuento: TipoDescuento;
  valor: number;
  fechaInicio: string;
  fechaFin: string;
  usoMaximo?: number | null;
  tipoAsignacion: TipoAsignacionCupon;
}
