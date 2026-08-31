import { EstadoCupon } from './enums/estado-cupon.enum';
import { TipoDescuento } from './enums/tipo-descuento.enum';

export interface UpdateCuponRequest {
  tipoDescuento: TipoDescuento;
  valor: number;
  fechaInicio: string;
  fechaFin: string;
  usoMaximo?: number | null;
  estado: EstadoCupon;
}
