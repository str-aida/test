import { TipoCampanaDescuento } from './enums/tipo-campana-descuento.enum';
import { ProductoDescuentoRequest } from './producto-descuento-request';

export interface CreateDescuentoRequest {
  nombre: string;
  tipo: TipoCampanaDescuento;
  fechaInicio: string;
  fechaFin: string;
  productos: ProductoDescuentoRequest[];
}
