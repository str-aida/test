import { EstadoPedido } from './enums/estado-pedido.enum';
import { EstadoPago } from './enums/estado-pago.enum';
import { MetodoPago } from './enums/metodo-pago.enum';
import { TipoEntrega } from './enums/tipo-entrega.enum';

/**
 * Filtros disponibles en GET /pedidos.
 * Todos opcionales — el backend devuelve la lista completa si no se envían.
 */
export interface PedidoFiltros {
    estado?: EstadoPedido;
    tipoEntrega?: TipoEntrega;
    estadoPago?: EstadoPago;
    metodoPago?: MetodoPago;
    nombreCliente?: string;
    numeroPedido?: string;
    fechaDesde?: string; // ISO date: YYYY-MM-DD
    fechaHasta?: string; // ISO date: YYYY-MM-DD
}
