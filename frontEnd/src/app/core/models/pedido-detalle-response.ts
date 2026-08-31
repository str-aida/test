import { DetallePedidoResponse } from './detalle-pedido-response';
import { EstadoPago } from './enums/estado-pago.enum';
import { EstadoPedido } from './enums/estado-pedido.enum';
import { MetodoPago } from './enums/metodo-pago.enum';
import { TipoEntrega } from './enums/tipo-entrega.enum';

export interface PedidoDetalleResponse {
    id: number;
    numeroPedido: string;
    estado: EstadoPedido;
    estadoPago: EstadoPago;
    metodoPago: MetodoPago;
    urlPago: string | null;
    fechaHora: string;
    total: number;
    tipoEntrega: TipoEntrega;
    nombreCliente: string;
    telefonoCliente: string;
    direccionCliente: string | null;
    detalles: DetallePedidoResponse[];
    codigoCuponAplicado: string | null;
    montoDescuento: number | null;
    totalConDescuento: number | null;
}
