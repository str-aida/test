import { TipoCampanaDescuento } from './enums/tipo-campana-descuento.enum';
import { ProductoDescuentoRequest } from './producto-descuento-request';

export interface UpdateDescuentoRequest {
  nombre: string;
  tipo: TipoCampanaDescuento;
  fechaInicio: string;
  fechaFin: string;
  productos: ProductoDescuentoRequest[];
}
