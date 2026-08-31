export interface ValidacionCuponResponse {
    valido: boolean;
    mensaje: string;
    codigoCupon: string;
    montoOriginal: number;
    montoDescuento: number;
    totalConDescuento: number;
}
