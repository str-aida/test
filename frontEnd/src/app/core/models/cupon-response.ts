import { EstadoCupon } from './enums/estado-cupon.enum';
import { TipoDescuento } from './enums/tipo-descuento.enum';

export interface CuponResponse {
    id: number;
    codigo: string;
    tipoDescuento: TipoDescuento;
    valor: number;
    fechaInicio: string;
    fechaFin: string;
    usoMaximo: number | null;
    usosActuales: number;
    estado: EstadoCupon;
    /**
     * Cupos restantes para asignar a nuevos usuarios.
     * null = sin límite (usoMaximo es null).
     * 0   = agotado.
     */
    cuposDisponibles: number | null;
}
