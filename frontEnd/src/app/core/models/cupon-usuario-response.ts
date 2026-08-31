import { CuponResponse } from './cupon-response';

export interface CuponUsuarioResponse {
    id: number;
    cupon: CuponResponse;
    usado: boolean;
    fechaAsignacion: string;
    fechaUso: string | null;
}
