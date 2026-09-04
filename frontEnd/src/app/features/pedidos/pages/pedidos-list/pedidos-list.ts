import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule, DatePipe, DecimalPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import {
  LucideSearch,
  LucideX,
  LucideEye,
  LucideAlertTriangle,
  LucideInbox,
  LucideTruck,
  LucideStore,
  LucideSlidersHorizontal,
  LucideChevronLeft,
  LucideChevronRight,
  LucideShoppingBag
} from '@lucide/angular';
import { PedidoService } from '../../../../core/services/pedido.service';
import { NotificationService } from '../../../../core/services/notification.service';
import { TokenService } from '../../../../core/services/token.service';
import { EstablecimientoService } from '../../../../core/services/establecimiento.service';
import { PedidoResponse } from '../../../../core/models/pedido-response';
import { PageResponse } from '../../../../core/models/page-response';
import { PedidoFiltros } from '../../../../core/models/pedido-filtros';
import { EstadoPedido } from '../../../../core/models/enums/estado-pedido.enum';
import { EstadoPago } from '../../../../core/models/enums/estado-pago.enum';
import { MetodoPago } from '../../../../core/models/enums/metodo-pago.enum';
import { TipoEntrega } from '../../../../core/models/enums/tipo-entrega.enum';
import { TipoServicio } from '../../../../core/models/enums/tipo-servicio.enum';
import { UserRole } from '../../../../core/models/enums/user-role.enum';

interface EstadoCard {
  label: string;
  estado: EstadoPedido | null; // null = TODOS
}

@Component({
  selector: 'app-pedidos-list',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    DecimalPipe,
    DatePipe,
    LucideSearch,
    LucideX,
    LucideEye,
    LucideAlertTriangle,
    LucideInbox,
    LucideTruck,
    LucideStore,
    LucideSlidersHorizontal,
    LucideChevronLeft,
    LucideChevronRight,
    LucideShoppingBag
  ],
  templateUrl: './pedidos-list.html',
  styleUrl: './pedidos-list.scss'
})
export class PedidosListComponent implements OnInit {

  private readonly pedidoService = inject(PedidoService);
  private readonly notificationService = inject(NotificationService);
  private readonly tokenService = inject(TokenService);
  private readonly establecimientoService = inject(EstablecimientoService);
  private readonly router = inject(Router);

  protected readonly EstadoPedido = EstadoPedido;
  protected readonly TipoEntrega = TipoEntrega;
  protected readonly TipoServicio = TipoServicio;
  protected readonly MetodoPago = MetodoPago;
  protected readonly EstadoPago = EstadoPago;

  tipoServicioEstablecimiento: TipoServicio | null = null;

  // ─── Estado principal ────────────────────────────────────────
  pageData = signal<PageResponse<PedidoResponse> | null>(null);
  isLoading = signal<boolean>(false);
  hasError = signal<boolean>(false);

  // Tamaño de página fijo
  readonly PAGE_SIZE = 20;
  // Página actual 0-indexed (para el backend)
  currentPage = signal<number>(0);

  // ─── Filtros de estado (cards) — solo filtro, sin conteos ────
  estadoActivoCard: EstadoPedido | null = null;

  // ─── Búsqueda rápida (se envía al backend) ──────────────────
  busquedaRapida = '';

  // ─── Filtros avanzados ───────────────────────────────────────
  mostrarFiltrosAvanzados = false;
  filtroTipoEntrega: TipoEntrega | '' = '';
  filtroMetodoPago: MetodoPago | '' = '';;
  filtroFechaDesde = '';
  filtroFechaHasta = '';

  // ─── Cards de estado como filtros rápidos ───────────────────
  readonly estadoCards: EstadoCard[] = [
    { label: 'TODOS',           estado: null },
    { label: 'PENDIENTES',      estado: EstadoPedido.PENDIENTE },
    { label: 'ACEPTADOS',       estado: EstadoPedido.ACEPTADO },
    { label: 'EN PREPARACIÓN',  estado: EstadoPedido.EN_PREPARACION },
    { label: 'LISTOS',          estado: EstadoPedido.LISTO },
    { label: 'ENTREGADOS',      estado: EstadoPedido.ENTREGADO },
    { label: 'RECHAZADOS',      estado: EstadoPedido.RECHAZADO }
  ];

  ngOnInit(): void {
    this.cargarEstablecimiento();
    this.cargarPedidos();
  }

  cargarEstablecimiento(): void {
    this.establecimientoService.obtenerInfoClienteActual().subscribe({
      next: (est) => {
        this.tipoServicioEstablecimiento = est.tipoServicio;
      },
      error: () => {
        this.tipoServicioEstablecimiento = null;
      }
    });
  }

  // ─── Carga principal ─────────────────────────────────────────

  cargarPedidos(): void {
    this.isLoading.set(true);
    this.hasError.set(false);

    const filtros: PedidoFiltros = {};
    if (this.estadoActivoCard)   filtros.estado       = this.estadoActivoCard;
    if (this.filtroTipoEntrega)  filtros.tipoEntrega  = this.filtroTipoEntrega;
    if (this.filtroMetodoPago)   filtros.metodoPago   = this.filtroMetodoPago;
    if (this.filtroFechaDesde)   filtros.fechaDesde   = this.filtroFechaDesde;
    if (this.filtroFechaHasta)   filtros.fechaHasta   = this.filtroFechaHasta;
    // Búsqueda rápida: se envía al backend como nombreCliente o numeroPedido
    const q = this.busquedaRapida.trim();
    if (q) {
      // Si parece número de pedido (empieza con #, letras/números sin espacios)
      if (/^[A-Za-z0-9#-]+$/.test(q)) {
        filtros.numeroPedido  = q.replace(/^#/, '');
        filtros.nombreCliente = q;
      } else {
        filtros.nombreCliente = q;
      }
    }

    this.pedidoService.listarPedidos(filtros, this.currentPage(), this.PAGE_SIZE).subscribe({
      next: (page) => {
        this.pageData.set(page);
        this.isLoading.set(false);
      },
      error: (err) => {
        this.hasError.set(true);
        this.isLoading.set(false);
        const msg = err.error?.message || err.error?.mensaje || 'Error al cargar los pedidos.';
        this.notificationService.error(msg);
      }
    });
  }

  // ─── Helpers de paginación desde PageResponse ────────────────

  get pedidos(): PedidoResponse[] {
    return this.pageData()?.content ?? [];
  }

  get totalElementos(): number {
    return this.pageData()?.totalElementos ?? 0;
  }

  get totalPaginas(): number {
    return this.pageData()?.totalPaginas ?? 1;
  }

  get esPrimeraPagina(): boolean {
    return this.pageData()?.primera ?? true;
  }

  get esUltimaPagina(): boolean {
    return this.pageData()?.ultima ?? true;
  }

  get mostrandoDesde(): number {
    if (this.totalElementos === 0) return 0;
    return this.currentPage() * this.PAGE_SIZE + 1;
  }

  get mostrandoHasta(): number {
    return Math.min((this.currentPage() + 1) * this.PAGE_SIZE, this.totalElementos);
  }

  prevPage(): void {
    if (!this.esPrimeraPagina) {
      this.currentPage.update(p => p - 1);
      this.cargarPedidos();
    }
  }

  nextPage(): void {
    if (!this.esUltimaPagina) {
      this.currentPage.update(p => p + 1);
      this.cargarPedidos();
    }
  }

  // ─── Filtros ─────────────────────────────────────────────────

  seleccionarCard(estado: EstadoPedido | null): void {
    this.estadoActivoCard = estado;
    this.currentPage.set(0);
    this.cargarPedidos();
  }

  buscar(): void {
    this.currentPage.set(0);
    this.cargarPedidos();
  }

  limpiarBusqueda(): void {
    this.busquedaRapida = '';
    this.currentPage.set(0);
    this.cargarPedidos();
  }

  toggleFiltrosAvanzados(): void {
    this.mostrarFiltrosAvanzados = !this.mostrarFiltrosAvanzados;
  }

  aplicarFiltrosAvanzados(): void {
    this.estadoActivoCard = null;
    this.currentPage.set(0);
    this.cargarPedidos();
    this.mostrarFiltrosAvanzados = false;
  }

  limpiarFiltrosAvanzados(): void {
    this.filtroTipoEntrega = '';
    this.filtroMetodoPago = '';
    this.filtroFechaDesde = '';
    this.filtroFechaHasta = '';
    this.currentPage.set(0);
    this.cargarPedidos();
    this.mostrarFiltrosAvanzados = false;
  }

  get hayFiltrosAvanzadosActivos(): boolean {
    return !!(this.filtroTipoEntrega || this.filtroMetodoPago || this.filtroFechaDesde || this.filtroFechaHasta);
  }

  verDetalle(id: number): void {
    const role = this.tokenService.getRole();
    if (role === UserRole.EMPLEADO) {
      this.router.navigate(['/empleado/pedidos', id]);
    } else {
      this.router.navigate(['/admin/pedidos', id]);
    }
  }

  // ─── Helpers visuales ────────────────────────────────────────

  getEstadoClass(estado: EstadoPedido): string {
    switch (estado) {
      case EstadoPedido.PENDIENTE:      return 'estado--pendiente';
      case EstadoPedido.ACEPTADO:       return 'estado--aceptado';
      case EstadoPedido.EN_PREPARACION: return 'estado--preparacion';
      case EstadoPedido.LISTO:          return 'estado--listo';
      case EstadoPedido.ENTREGADO:      return 'estado--entregado';
      case EstadoPedido.RECHAZADO:      return 'estado--rechazado';
      default:                          return '';
    }
  }

  getEstadoLabel(estado: EstadoPedido): string {
    switch (estado) {
      case EstadoPedido.PENDIENTE:      return 'PENDIENTE';
      case EstadoPedido.ACEPTADO:       return 'ACEPTADO';
      case EstadoPedido.EN_PREPARACION: return 'EN PREPARACIÓN';
      case EstadoPedido.LISTO:          return 'LISTO';
      case EstadoPedido.ENTREGADO:      return 'ENTREGADO';
      case EstadoPedido.RECHAZADO:      return 'RECHAZADO';
      default:                          return estado;
    }
  }

  getPagoClass(estadoPago: EstadoPago | string): string {
    switch (estadoPago) {
      case EstadoPago.APROBADO:     return 'pago--aprobado';
      case EstadoPago.PENDIENTE:    return 'pago--pendiente';
      case EstadoPago.ANULADO:      return 'pago--rechazado';
      case EstadoPago.REEMBOLSADO:  return 'pago--reembolsado';
      default:                      return 'pago--pendiente';
    }
  }

  getPagoLabel(estadoPago: EstadoPago | string): string {
    switch (estadoPago) {
      case EstadoPago.APROBADO:     return 'APROBADO';
      case EstadoPago.PENDIENTE:    return 'PENDIENTE';
      case EstadoPago.ANULADO:      return 'ANULADO';
      case EstadoPago.REEMBOLSADO:  return 'REEMBOLSADO';
      default:                      return estadoPago || 'PENDIENTE';
    }
  }
}
