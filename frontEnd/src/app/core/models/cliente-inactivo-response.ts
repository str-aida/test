export interface ClienteInactivoResponse {
  idCliente: number;
  nombreCompleto: string;
  email: string;
  ultimaCompra: string | null;
  montoUltimaCompra: number | null;
  totalGastado: number;
  cantidadPedidos: number;
}
