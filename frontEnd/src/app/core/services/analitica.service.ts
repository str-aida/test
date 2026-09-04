import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ResumenEjecutivoResponse } from '../models/resumen-ejecutivo-response';
import { ClienteAnaliticaResponse } from '../models/cliente-analitica-response';
import { EstadoPedidoAnaliticaResponse } from '../models/estado-pedido-analitica-response';
import { ProductoAnaliticaResponse } from '../models/producto-analitica-response';
import { VentaPeriodoResponse } from '../models/venta-periodo-response';
import { ClienteInactivoResponse } from '../models/cliente-inactivo-response';
import { VentaMetodoPagoResponse } from '../models/venta-metodo-pago-response';
import { VentaTipoEntregaResponse } from '../models/venta-tipo-entrega-response';

@Injectable({
  providedIn: 'root'
})
export class AnaliticaService {

  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.baseUrl}/analitica`;

  /** GET /analitica/resumen */
  obtenerResumen(): Observable<ResumenEjecutivoResponse> {
    return this.http.get<ResumenEjecutivoResponse>(`${this.apiUrl}/resumen`);
  }

  /** GET /analitica/clientes/mejores?limite={limite} */
  obtenerMejoresClientes(limite: number = 10): Observable<ClienteAnaliticaResponse[]> {
    const params = new HttpParams().set('limite', limite.toString());
    return this.http.get<ClienteAnaliticaResponse[]>(`${this.apiUrl}/clientes/mejores`, { params });
  }

  /** GET /analitica/pedidos/estados */
  obtenerPedidosPorEstado(): Observable<EstadoPedidoAnaliticaResponse[]> {
    return this.http.get<EstadoPedidoAnaliticaResponse[]>(`${this.apiUrl}/pedidos/estados`);
  }

  /** GET /analitica/productos/mas-vendidos?limite={limite} */
  obtenerProductosMasVendidos(limite: number = 10): Observable<ProductoAnaliticaResponse[]> {
    const params = new HttpParams().set('limite', limite.toString());
    return this.http.get<ProductoAnaliticaResponse[]>(`${this.apiUrl}/productos/mas-vendidos`, { params });
  }

  /** GET /analitica/ventas/periodo?dias={dias} */
  obtenerVentasPorPeriodo(dias: number = 30): Observable<VentaPeriodoResponse[]> {
    const params = new HttpParams().set('dias', dias.toString());
    return this.http.get<VentaPeriodoResponse[]>(`${this.apiUrl}/ventas/periodo`, { params });
  }

  /** GET /analitica/clientes/inactivos?dias={dias} */
  obtenerClientesInactivos(dias: number = 30): Observable<ClienteInactivoResponse[]> {
    const params = new HttpParams().set('dias', dias.toString());
    return this.http.get<ClienteInactivoResponse[]>(`${this.apiUrl}/clientes/inactivos`, { params });
  }

  /** GET /analitica/ventas/metodos-pago */
  obtenerVentasPorMetodoPago(): Observable<VentaMetodoPagoResponse[]> {
    return this.http.get<VentaMetodoPagoResponse[]>(`${this.apiUrl}/ventas/metodos-pago`);
  }

  /** GET /analitica/productos/menos-vendidos?dias={dias}&limite={limite} */
  obtenerProductosMenosVendidos(dias: number = 30, limite: number = 10): Observable<ProductoAnaliticaResponse[]> {
    const params = new HttpParams()
      .set('dias', dias.toString())
      .set('limite', limite.toString());
    return this.http.get<ProductoAnaliticaResponse[]>(`${this.apiUrl}/productos/menos-vendidos`, { params });
  }

  /** GET /analitica/ventas/tipos-entrega */
  obtenerVentasPorTipoEntrega(): Observable<VentaTipoEntregaResponse[]> {
    return this.http.get<VentaTipoEntregaResponse[]>(`${this.apiUrl}/ventas/tipos-entrega`);
  }
}
