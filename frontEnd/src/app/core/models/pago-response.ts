import { EstadoPago } from './enums/estado-pago.enum';
import { MetodoPago } from './enums/metodo-pago.enum';

export interface PagoResponse {
    id: number;
    pedidoId: number;
    monto: number;
    metodoPago: MetodoPago;
    estado: EstadoPago;
    fechaCreacion: string;
    referenciaExterna?: string | null;
    urlPago?: string | null;
    idTransaccionExterna?: string | null;
}
