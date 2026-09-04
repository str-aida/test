export interface ClienteAnaliticaResponse {
  id: number;
  nombreCompleto: string;
  cantidadPedidos: number;
  totalGastado: number;
  ultimaCompra: string | null;
}
