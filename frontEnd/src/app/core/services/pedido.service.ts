import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { CreatePedidoRequest } from '../models/create-pedido-request';
import { PedidoDetalleResponse } from '../models/pedido-detalle-response';
import { PedidoResponse } from '../models/pedido-response';
import { AplicarCuponRequest } from '../models/aplicar-cupon-request';
import { ValidacionCuponResponse } from '../models/validacion-cupon-response';
import { PedidoFiltros } from '../models/pedido-filtros';
import { PageResponse } from '../models/page-response';

@Injectable({
  providedIn: 'root'
})
export class PedidoService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.baseUrl}/pedidos`;

  // ─── Cliente ────────────────────────────────────────────────

  crearPedido(request: CreatePedidoRequest): Observable<PedidoDetalleResponse> {
    return this.http.post<PedidoDetalleResponse>(this.apiUrl, request);
  }

  aplicarCupon(pedidoId: number, request: AplicarCuponRequest): Observable<ValidacionCuponResponse> {
    return this.http.put<ValidacionCuponResponse>(`${this.apiUrl}/${pedidoId}/aplicar-cupon`, request);
  }

  listarMisPedidos(page: number = 0, size: number = 20): Observable<PageResponse<PedidoResponse>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<PageResponse<PedidoResponse>>(this.apiUrl, { params });
  }

  obtenerPedidoPorId(id: number): Observable<PedidoDetalleResponse> {
    return this.http.get<PedidoDetalleResponse>(`${this.apiUrl}/${id}`);
  }

  // ─── Admin / Empleado ────────────────────────────────────────

  /**
   * Lista todos los pedidos con filtros opcionales y paginación real del backend.
   * Endpoint: GET /pedidos — ADMIN, EMPLEADO, CLIENTE.
   * Devuelve PageResponse<PedidoResponse> (paginación del backend).
   * page es 0-indexed. size por defecto 20.
   */
  listarPedidos(filtros?: PedidoFiltros, page: number = 0, size: number = 20): Observable<PageResponse<PedidoResponse>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (filtros) {
      if (filtros.estado)        params = params.set('estado', filtros.estado);
      if (filtros.tipoEntrega)   params = params.set('tipoEntrega', filtros.tipoEntrega);
      if (filtros.estadoPago)    params = params.set('estadoPago', filtros.estadoPago);
      if (filtros.metodoPago)    params = params.set('metodoPago', filtros.metodoPago);
      if (filtros.nombreCliente) params = params.set('nombreCliente', filtros.nombreCliente);
      if (filtros.numeroPedido)  params = params.set('numeroPedido', filtros.numeroPedido);
      if (filtros.fechaDesde)    params = params.set('fechaDesde', filtros.fechaDesde);
      if (filtros.fechaHasta)    params = params.set('fechaHasta', filtros.fechaHasta);
    }
    return this.http.get<PageResponse<PedidoResponse>>(this.apiUrl, { params });
  }

  /**
   * Lista pedidos en curso (PENDIENTE, ACEPTADO, EN_PREPARACION, LISTO).
   * Endpoint: GET /pedidos/en-curso — Solo ADMIN y EMPLEADO.
   * El backend determina qué pedidos están en curso. No filtrar en frontend.
   * page es 0-indexed. size por defecto 20.
   */
  listarPedidosEnCurso(page: number = 0, size: number = 20): Observable<PageResponse<PedidoResponse>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<PageResponse<PedidoResponse>>(`${this.apiUrl}/en-curso`, { params });
  }

  /** PUT /pedidos/{id}/aceptar — ADMIN, EMPLEADO */
  aceptarPedido(id: number): Observable<PedidoDetalleResponse> {
    return this.http.put<PedidoDetalleResponse>(`${this.apiUrl}/${id}/aceptar`, {});
  }

  /** PUT /pedidos/{id}/rechazar — ADMIN, EMPLEADO */
  rechazarPedido(id: number): Observable<PedidoDetalleResponse> {
    return this.http.put<PedidoDetalleResponse>(`${this.apiUrl}/${id}/rechazar`, {});
  }

  /** PUT /pedidos/{id}/en-preparacion — ADMIN, EMPLEADO */
  pasarAEnPreparacion(id: number): Observable<PedidoDetalleResponse> {
    return this.http.put<PedidoDetalleResponse>(`${this.apiUrl}/${id}/en-preparacion`, {});
  }

  /** PUT /pedidos/{id}/listo — ADMIN, EMPLEADO */
  marcarComoListo(id: number): Observable<PedidoDetalleResponse> {
    return this.http.put<PedidoDetalleResponse>(`${this.apiUrl}/${id}/listo`, {});
  }

  /** PUT /pedidos/{id}/entregado — ADMIN, EMPLEADO */
  marcarComoEntregado(id: number): Observable<PedidoDetalleResponse> {
    return this.http.put<PedidoDetalleResponse>(`${this.apiUrl}/${id}/entregado`, {});
  }
}
