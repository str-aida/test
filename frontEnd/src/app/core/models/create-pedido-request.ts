import { CreateDetallePedidoRequest } from './create-detalle-pedido-request';
import { MetodoPago } from './enums/metodo-pago.enum';
import { TipoEntrega } from './enums/tipo-entrega.enum';

export interface CreatePedidoRequest {
    tipoEntrega: TipoEntrega;
    direccionId?: number | null;
    detalles: CreateDetallePedidoRequest[];
    metodoPago: MetodoPago;
}
