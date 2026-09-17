import { TipoAsignacionCupon } from './enums/tipo-asignacion-cupon.enum';
import { TipoDescuento } from './enums/tipo-descuento.enum';

export interface ReglaCuponResponse {
  id: number;
  tipoAsignacion: TipoAsignacionCupon;
  activo: boolean;
  tipoDescuento: TipoDescuento;
  valor: number;
  diasValidez: number;
  cantidadComprasRequeridas?: number | null;
  descripcion?: string | null;
}
