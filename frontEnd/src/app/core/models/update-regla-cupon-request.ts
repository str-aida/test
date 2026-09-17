import { TipoDescuento } from './enums/tipo-descuento.enum';

export interface UpdateReglaCuponRequest {
  activo: boolean;
  tipoDescuento: TipoDescuento;
  valor: number;
  diasValidez: number;
  cantidadComprasRequeridas?: number | null;
  descripcion?: string | null;
}
