import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule, DatePipe, DecimalPipe } from '@angular/common';
import { Router } from '@angular/router';
import {
  LucideAlertTriangle,
  LucideInbox,
  LucideTruck,
  LucideStore,
  LucideEye,
  LucideChevronLeft,
  LucideChevronRight,
  LucideRefreshCw,
  LucideActivity
} from '@lucide/angular';
import { PedidoService } from '../../../../core/services/pedido.service';
import { NotificationService } from '../../../../core/services/notification.service';
import { TokenService } from '../../../../core/services/token.service';
import { PedidoResponse } from '../../../../core/models/pedido-response';
import { PageResponse } from '../../../../core/models/page-response';
import { EstadoPedido } from '../../../../core/models/enums/estado-pedido.enum';
import { EstadoPago } from '../../../../core/models/enums/estado-pago.enum';
import { TipoEntrega } from '../../../../core/models/enums/tipo-entrega.enum';
import { UserRole } from '../../../../core/models/enums/user-role.enum';

@Component({
  selector: 'app-pedidos-en-curso',
  standalone: true,
  imports: [
    CommonModule,
    DecimalPipe,
    DatePipe,
    LucideAlertTriangle,
    LucideInbox,
    LucideTruck,
    LucideStore,
    LucideEye,
    LucideChevronLeft,
    LucideChevronRight,
    LucideRefreshCw,
    LucideActivity
  ],
  templateUrl: './pedidos-en-curso.html',
  styleUrl: './pedidos-en-curso.scss'
})
export class PedidosEnCursoComponent implements OnInit {

  private readonly pedidoService = inject(PedidoService);
  private readonly notificationService = inject(NotificationService);
  private readonly tokenService = inject(TokenService);
  private readonly router = inject(Router);

  protected readonly EstadoPedido = EstadoPedido;
  protected readonly EstadoPago = EstadoPago;
  protected readonly TipoEntrega = TipoEntrega;

  // ─── Estado principal ────────────────────────────────────────
  pageData = signal<PageResponse<PedidoResponse> | null>(null);
  isLoading = signal<boolean>(false);
  hasError = signal<boolean>(false);

  readonly PAGE_SIZE = 20;
  currentPage = signal<number>(0);

  ngOnInit(): void {
    this.cargarPedidosEnCurso();
  }

  // ─── Carga desde backend ─────────────────────────────────────

  cargarPedidosEnCurso(): void {
    this.isLoading.set(true);
    this.hasError.set(false);

    this.pedidoService.listarPedidosEnCurso(this.currentPage(), this.PAGE_SIZE).subscribe({
      next: (page) => {
        this.pageData.set(page);
        this.isLoading.set(false);
      },
      error: (err) => {
        this.hasError.set(true);
        this.isLoading.set(false);
        const msg = err.error?.message || err.error?.mensaje || 'Error al cargar los pedidos en curso.';
        this.notificationService.error(msg);
      }
    });
  }

  // ─── Helpers de paginación ───────────────────────────────────

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
      this.cargarPedidosEnCurso();
    }
  }

  nextPage(): void {
    if (!this.esUltimaPagina) {
      this.currentPage.update(p => p + 1);
      this.cargarPedidosEnCurso();
    }
  }

  // ─── Acciones ────────────────────────────────────────────────

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
      default:                          return '';
    }
  }

  getEstadoLabel(estado: EstadoPedido): string {
    switch (estado) {
      case EstadoPedido.PENDIENTE:      return 'PENDIENTE';
      case EstadoPedido.ACEPTADO:       return 'ACEPTADO';
      case EstadoPedido.EN_PREPARACION: return 'EN PREPARACIÓN';
      case EstadoPedido.LISTO:          return 'LISTO';
      default:                          return estado;
    }
  }

  getPagoClass(estadoPago: EstadoPago | string): string {
    switch (estadoPago) {
      case EstadoPago.APROBADO:    return 'pago--aprobado';
      case EstadoPago.PENDIENTE:   return 'pago--pendiente';
      case EstadoPago.ANULADO:     return 'pago--rechazado';
      case EstadoPago.REEMBOLSADO: return 'pago--reembolsado';
      default:                     return 'pago--pendiente';
    }
  }

  getPagoLabel(estadoPago: EstadoPago | string): string {
    switch (estadoPago) {
      case EstadoPago.APROBADO:    return 'APROBADO';
      case EstadoPago.PENDIENTE:   return 'PENDIENTE';
      case EstadoPago.ANULADO:     return 'ANULADO';
      case EstadoPago.REEMBOLSADO: return 'REEMBOLSADO';
      default:                     return estadoPago || 'PENDIENTE';
    }
  }
}
