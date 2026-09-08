import { TipoCampanaDescuento } from './enums/tipo-campana-descuento.enum';
import { EstadoDescuento } from './enums/estado-descuento.enum';
import { ProductoDescuentoResponse } from './producto-descuento-response';

export interface DescuentoResponse {
  id: number;
  nombre: string;
  tipo: TipoCampanaDescuento;
  fechaInicio: string; // ISO date YYYY-MM-DD
  fechaFin: string; // ISO date YYYY-MM-DD
  estado: EstadoDescuento;
  productos: ProductoDescuentoResponse[];
}
