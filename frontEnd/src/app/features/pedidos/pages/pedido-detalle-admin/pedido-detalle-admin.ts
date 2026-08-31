import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule, DatePipe, DecimalPipe } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import {
  LucideArrowLeft,
  LucideTruck,
  LucideStore,
  LucideUser,
  LucidePhone,
  LucideCreditCard,
  LucideCheckCircle2,
  LucideXCircle,
  LucideAlertTriangle,
  LucideCheck,
  LucideX,
  LucideChefHat,
  LucidePackageCheck,
  LucideLoader,
  LucideClock,
  LucideRefreshCw
} from '@lucide/angular';
import { PedidoService } from '../../../../core/services/pedido.service';
import { NotificationService } from '../../../../core/services/notification.service';
import { TokenService } from '../../../../core/services/token.service';
import { PedidoDetalleResponse } from '../../../../core/models/pedido-detalle-response';
import { EstadoPedido } from '../../../../core/models/enums/estado-pedido.enum';
import { EstadoPago } from '../../../../core/models/enums/estado-pago.enum';
import { TipoEntrega } from '../../../../core/models/enums/tipo-entrega.enum';
import { MetodoPago } from '../../../../core/models/enums/metodo-pago.enum';
import { UserRole } from '../../../../core/models/enums/user-role.enum';

interface StepDef {
  estado: EstadoPedido;
  label: string;
}

@Component({
  selector: 'app-pedido-detalle-admin',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    DecimalPipe,
    DatePipe,
    LucideArrowLeft,
    LucideTruck,
    LucideStore,
    LucideUser,
    LucidePhone,
    LucideCreditCard,
    LucideCheckCircle2,
    LucideXCircle,
    LucideAlertTriangle,
    LucideCheck,
    LucideX,
    LucideChefHat,
    LucidePackageCheck,
    LucideLoader,
    LucideClock,
    LucideRefreshCw
  ],
  templateUrl: './pedido-detalle-admin.html',
  styleUrl: './pedido-detalle-admin.scss'
})
export class PedidoDetalleAdminComponent implements OnInit {

  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly pedidoService = inject(PedidoService);
  private readonly notificationService = inject(NotificationService);
  private readonly tokenService = inject(TokenService);

  protected readonly EstadoPedido = EstadoPedido;
  protected readonly EstadoPago = EstadoPago;
  protected readonly TipoEntrega = TipoEntrega;
  protected readonly MetodoPago = MetodoPago;

  pedido = signal<PedidoDetalleResponse | null>(null);
  isLoading = signal<boolean>(true);
  errorMsg = signal<string | null>(null);

  // Estado de carga por acción (evita doble clic)
  accionEnCurso = signal<string | null>(null);

  // Ruta de retorno según el rol
  private backRoute = '/admin/pedidos';

  // Stepper visual de estados (flujo principal sin RECHAZADO)
  readonly steps: StepDef[] = [
    { estado: EstadoPedido.PENDIENTE,      label: 'Pendiente' },
    { estado: EstadoPedido.ACEPTADO,       label: 'Aceptado' },
    { estado: EstadoPedido.EN_PREPARACION, label: 'En preparación' },
    { estado: EstadoPedido.LISTO,          label: 'Listo' },
    { estado: EstadoPedido.ENTREGADO,      label: 'Entregado' }
  ];

  ngOnInit(): void {
    const role = this.tokenService.getRole();
    this.backRoute = role === UserRole.EMPLEADO ? '/empleado/pedidos' : '/admin/pedidos';

    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.cargarDetalle(+idParam);
    } else {
      this.errorMsg.set('ID de pedido inválido.');
      this.isLoading.set(false);
    }
  }

  cargarDetalle(id: number): void {
    this.isLoading.set(true);
    this.errorMsg.set(null);
    this.pedidoService.obtenerPedidoPorId(id).subscribe({
      next: (data) => {
        this.pedido.set(data);
        this.isLoading.set(false);
      },
      error: (err) => {
        const msg = err.error?.message || err.error?.mensaje || 'No se pudo cargar el pedido.';
        this.errorMsg.set(msg);
        this.isLoading.set(false);
      }
    });
  }

  get backRouteValue(): string {
    return this.backRoute;
  }

  // ─── Acciones de transición de estado ───────────────────────

  /**
   * Ejecuta la acción de transición correspondiente.
   * El backend valida si la transición es permitida.
   * Si falla, se muestra el mensaje del backend.
   */
  aceptarPedido(): void {
    this.ejecutarAccion('aceptar', () =>
      this.pedidoService.aceptarPedido(this.pedido()!.id),
      'Pedido aceptado correctamente.'
    );
  }

  rechazarPedido(): void {
    this.ejecutarAccion('rechazar', () =>
      this.pedidoService.rechazarPedido(this.pedido()!.id),
      'Pedido rechazado.'
    );
  }

  pasarAEnPreparacion(): void {
    this.ejecutarAccion('en-preparacion', () =>
      this.pedidoService.pasarAEnPreparacion(this.pedido()!.id),
      'Pedido pasado a En preparación.'
    );
  }

  marcarComoListo(): void {
    this.ejecutarAccion('listo', () =>
      this.pedidoService.marcarComoListo(this.pedido()!.id),
      'Pedido marcado como Listo.'
    );
  }

  marcarComoEntregado(): void {
    this.ejecutarAccion('entregado', () =>
      this.pedidoService.marcarComoEntregado(this.pedido()!.id),
      'Pedido marcado como Entregado.'
    );
  }

  private ejecutarAccion(
    clave: string,
    accionFn: () => import('rxjs').Observable<PedidoDetalleResponse>,
    mensajeExito: string
  ): void {
    if (this.accionEnCurso()) return; // evitar doble clic
    this.accionEnCurso.set(clave);

    accionFn().subscribe({
      next: (pedidoActualizado) => {
        // Actualizar el pedido con la respuesta REAL del backend
        this.pedido.set(pedidoActualizado);
        this.accionEnCurso.set(null);
        this.notificationService.success(mensajeExito);
      },
      error: (err) => {
        this.accionEnCurso.set(null);
        const msg = err.error?.message || err.error?.mensaje || 'No se pudo ejecutar la acción.';
        this.notificationService.error(msg);
      }
    });
  }

  // ─── Helpers visuales ───────────────────────────────────────

  isStepCompleted(stepEstado: EstadoPedido): boolean {
    const p = this.pedido();
    if (!p || p.estado === EstadoPedido.RECHAZADO) return false;
    const order = [
      EstadoPedido.PENDIENTE,
      EstadoPedido.ACEPTADO,
      EstadoPedido.EN_PREPARACION,
      EstadoPedido.LISTO,
      EstadoPedido.ENTREGADO
    ];
    return order.indexOf(stepEstado) <= order.indexOf(p.estado);
  }

  isStepCurrent(stepEstado: EstadoPedido): boolean {
    return this.pedido()?.estado === stepEstado;
  }

  getEstadoLabel(estado: EstadoPedido): string {
    switch (estado) {
      case EstadoPedido.PENDIENTE:      return 'Pendiente de aprobación';
      case EstadoPedido.ACEPTADO:       return 'Pedido Aceptado';
      case EstadoPedido.EN_PREPARACION: return 'En preparación';
      case EstadoPedido.LISTO:          return 'Listo para entregar / retirar';
      case EstadoPedido.ENTREGADO:      return 'Entregado';
      case EstadoPedido.RECHAZADO:      return 'Pedido Rechazado';
      default:                          return estado;
    }
  }

  getEstadoBadgeClass(estado: EstadoPedido): string {
    switch (estado) {
      case EstadoPedido.PENDIENTE:       return 'badge--warning';
      case EstadoPedido.ACEPTADO:
      case EstadoPedido.EN_PREPARACION:  return 'badge--info';
      case EstadoPedido.LISTO:           return 'badge--purple';
      case EstadoPedido.ENTREGADO:       return 'badge--success';
      case EstadoPedido.RECHAZADO:       return 'badge--danger';
      default:                           return 'badge--neutral';
    }
  }
}
