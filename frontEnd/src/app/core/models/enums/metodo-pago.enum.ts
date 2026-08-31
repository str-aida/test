/*
 * Métodos de pago disponibles en el backend.
 * EFECTIVO: Pago en mano al momento de la entrega/retiro.
 * TARJETA: Pago con tarjeta (procesado externamente).
 */
export enum MetodoPago {
    EFECTIVO = 'EFECTIVO',
    TARJETA = 'TARJETA'
}
