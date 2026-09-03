import { MetodoPago } from './enums/metodo-pago.enum';
import { EstadoPago } from './enums/estado-pago.enum';
import { EstadoPedido } from './enums/estado-pedido.enum';
import { TipoEntrega } from './enums/tipo-entrega.enum';

export interface PedidoResponse {
    id: number;
    nombreCliente: string;
    telefonoCliente: string;
    fechaHora: string;
    total: number;
    estado: EstadoPedido;
    tipoEntrega: TipoEntrega;
    metodoPago: MetodoPago;
    estadoPago: EstadoPago;
    montoDescuento?: number | null;
    totalConDescuento?: number | null;
    numeroPedido: string;
}
