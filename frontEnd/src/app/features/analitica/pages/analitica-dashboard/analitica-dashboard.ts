import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { CommonModule, CurrencyPipe, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
  LucideTrendingUp,
  LucideShoppingBag,
  LucideDollarSign,
  LucideClock,
  LucideCheckCircle2,
  LucideUsers,
  LucideAward,
  LucideAlertTriangle,
  LucideCreditCard,
  LucideTruck,
  LucideBarChart3,
  LucideUserX,
  LucideRefreshCw,
  LucideStar
} from '@lucide/angular';
import { AnaliticaService } from '../../../../core/services/analitica.service';
import { ResumenEjecutivoResponse } from '../../../../core/models/resumen-ejecutivo-response';
import { ClienteAnaliticaResponse } from '../../../../core/models/cliente-analitica-response';
import { EstadoPedidoAnaliticaResponse } from '../../../../core/models/estado-pedido-analitica-response';
import { ProductoAnaliticaResponse } from '../../../../core/models/producto-analitica-response';
import { VentaPeriodoResponse } from '../../../../core/models/venta-periodo-response';
import { ClienteInactivoResponse } from '../../../../core/models/cliente-inactivo-response';
import { VentaMetodoPagoResponse } from '../../../../core/models/venta-metodo-pago-response';
import { VentaTipoEntregaResponse } from '../../../../core/models/venta-tipo-entrega-response';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-analitica-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    CurrencyPipe,
    DatePipe,
    LucideTrendingUp,
    LucideShoppingBag,
    LucideDollarSign,
    LucideClock,
    LucideCheckCircle2,
    LucideUsers,
    LucideAward,
    LucideAlertTriangle,
    LucideCreditCard,
    LucideTruck,
    LucideBarChart3,
    LucideUserX,
    LucideRefreshCw,
    LucideStar
  ],
  templateUrl: './analitica-dashboard.html',
  styleUrl: './analitica-dashboard.scss'
})
export class AnaliticaDashboardComponent implements OnInit {

  private readonly analiticaService = inject(AnaliticaService);

  // Estados principales
  isLoading = signal<boolean>(true);
  hasError = signal<boolean>(false);

  // Datos
  resumen = signal<ResumenEjecutivoResponse | null>(null);
  mejoresClientes = signal<ClienteAnaliticaResponse[]>([]);
  pedidosEstado = signal<EstadoPedidoAnaliticaResponse[]>([]);
  masVendidos = signal<ProductoAnaliticaResponse[]>([]);
  menosVendidos = signal<ProductoAnaliticaResponse[]>([]);
  ventasPeriodo = signal<VentaPeriodoResponse[]>([]);
  clientesInactivos = signal<ClienteInactivoResponse[]>([]);
  ventasMetodoPago = signal<VentaMetodoPagoResponse[]>([]);
  ventasTipoEntrega = signal<VentaTipoEntregaResponse[]>([]);

  // Filtros interactivos
  diasVentas = signal<number>(30);
  diasInactivos = signal<number>(30);
  diasMenosVendidos = signal<number>(30);

  // Hover interactivo para gráfico
  hoveredPoint = signal<{ periodo: string; ventas: number; x: number; y: number } | null>(null);

  ngOnInit(): void {
    this.cargarDatos();
  }

  cargarDatos(): void {
    this.isLoading.set(true);
    this.hasError.set(false);

    forkJoin({
      resumen: this.analiticaService.obtenerResumen(),
      mejoresClientes: this.analiticaService.obtenerMejoresClientes(5),
      pedidosEstado: this.analiticaService.obtenerPedidosPorEstado(),
      masVendidos: this.analiticaService.obtenerProductosMasVendidos(5),
      menosVendidos: this.analiticaService.obtenerProductosMenosVendidos(this.diasMenosVendidos(), 5),
      ventasPeriodo: this.analiticaService.obtenerVentasPorPeriodo(this.diasVentas()),
      clientesInactivos: this.analiticaService.obtenerClientesInactivos(this.diasInactivos()),
      ventasMetodoPago: this.analiticaService.obtenerVentasPorMetodoPago(),
      ventasTipoEntrega: this.analiticaService.obtenerVentasPorTipoEntrega()
    }).subscribe({
      next: (data) => {
        this.resumen.set(data.resumen);
        this.mejoresClientes.set(data.mejoresClientes);
        this.pedidosEstado.set(data.pedidosEstado);
        this.masVendidos.set(data.masVendidos);
        this.menosVendidos.set(data.menosVendidos);
        this.ventasPeriodo.set(data.ventasPeriodo);
        this.clientesInactivos.set(data.clientesInactivos);
        this.ventasMetodoPago.set(data.ventasMetodoPago);
        this.ventasTipoEntrega.set(data.ventasTipoEntrega);
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Error al cargar métricas de analítica:', err);
        this.hasError.set(true);
        this.isLoading.set(false);
      }
    });
  }

  // Cambios de filtro individuales
  onDiasVentasChange(dias: number): void {
    this.diasVentas.set(dias);
    this.analiticaService.obtenerVentasPorPeriodo(dias).subscribe({
      next: (res) => this.ventasPeriodo.set(res),
      error: (err) => console.error('Error al actualizar ventas por periodo:', err)
    });
  }

  onDiasInactivosChange(dias: number): void {
    this.diasInactivos.set(dias);
    this.analiticaService.obtenerClientesInactivos(dias).subscribe({
      next: (res) => this.clientesInactivos.set(res),
      error: (err) => console.error('Error al actualizar clientes inactivos:', err)
    });
  }

  onDiasMenosVendidosChange(dias: number): void {
    this.diasMenosVendidos.set(dias);
    this.analiticaService.obtenerProductosMenosVendidos(dias, 5).subscribe({
      next: (res) => this.menosVendidos.set(res),
      error: (err) => console.error('Error al actualizar productos menos vendidos:', err)
    });
  }

  // Insight destacado: producto #1 más vendido
  productoEstrella = computed(() => {
    const lista = this.masVendidos();
    return lista.length > 0 ? lista[0] : null;
  });

  // Cálculo de totales para proporciones
  totalPedidosEnEstados = computed(() => {
    return this.pedidosEstado().reduce((acc, item) => acc + item.cantidad, 0);
  });

  totalVentasMetodoPagoSum = computed(() => {
    return this.ventasMetodoPago().reduce((acc, item) => acc + item.totalVentas, 0);
  });

  totalVentasTipoEntregaSum = computed(() => {
    return this.ventasTipoEntrega().reduce((acc, item) => acc + item.totalVentas, 0);
  });

  maxUnidadesMasVendidas = computed(() => {
    const lista = this.masVendidos();
    if (!lista.length) return 1;
    return Math.max(...lista.map(p => p.cantidadVendida), 1);
  });

  maxVentasPeriodo = computed(() => {
    const lista = this.ventasPeriodo();
    if (!lista.length) return 1;
    return Math.max(...lista.map(v => v.ventas), 1);
  });

  // Gráfico SVG interactivo de Tendencia de Ventas
  svgPoints = computed(() => {
    const list = this.ventasPeriodo();
    if (!list.length) return { path: '', area: '', points: [] };

    const width = 600;
    const height = 180;
    const padding = 20;

    const maxVal = this.maxVentasPeriodo();
    const count = list.length;

    const points = list.map((item, index) => {
      const x = count === 1 ? width / 2 : padding + (index / (count - 1)) * (width - padding * 2);
      const y = height - padding - (item.ventas / maxVal) * (height - padding * 2);
      return { x, y, item };
    });

    const pathD = points.reduce((acc, pt, i) => `${acc} ${i === 0 ? 'M' : 'L'} ${pt.x} ${pt.y}`, '');
    const areaD = `${pathD} L ${points[points.length - 1].x} ${height - padding} L ${points[0].x} ${height - padding} Z`;

    return { path: pathD, area: areaD, points };
  });

  // Helpers de color y formato
  getEstadoColor(estado: string): string {
    const e = estado.toUpperCase();
    if (e.includes('ENTREGADO') || e.includes('COMPLETADO')) return 'var(--color-success, #10b981)';
    if (e.includes('CANCELADO') || e.includes('RECHAZADO')) return 'var(--color-danger, #ef4444)';
    if (e.includes('PENDIENTE') || e.includes('EN_ESPERA')) return 'var(--color-warning, #f59e0b)';
    if (e.includes('EN_PREPARACION') || e.includes('EN_CAMINO')) return 'var(--color-info, #3b82f6)';
    return 'var(--color-primary, #6366f1)';
  }

  getMetodoPagoLabel(metodo: string): string {
    const m = metodo.toUpperCase();
    if (m === 'MERCADO_PAGO') return 'Mercado Pago';
    if (m === 'TRANSFERENCIA') return 'Transferencia Bancaria';
    if (m === 'EFECTIVO') return 'Efectivo';
    if (m === 'TARJETA_CREDITO') return 'Tarjeta de Crédito';
    if (m === 'TARJETA_DEBITO') return 'Tarjeta de Débito';
    return metodo;
  }

  getTipoEntregaLabel(tipo: string): string {
    const t = tipo.toUpperCase();
    if (t === 'ENVIO_DOMICILIO' || t === 'DOMICILIO') return 'Envío a Domicilio';
    if (t === 'RETIRO_LOCAL' || t === 'LOCAL') return 'Retiro en Local';
    if (t === 'PUNTO_RETIRO') return 'Punto de Retiro';
    return tipo;
  }

  formatEstadoLabel(estado: string): string {
    return estado.replace(/_/g, ' ').toLowerCase().replace(/\b\w/g, c => c.toUpperCase());
  }

  calcPorcentaje(valor: number, total: number): number {
    if (!total || total === 0) return 0;
    return Math.round((valor / total) * 100);
  }
}
