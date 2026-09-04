import { Component, inject, OnDestroy, OnInit, signal } from '@angular/core';
import { CommonModule, DatePipe, DecimalPipe } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Subscription } from 'rxjs';
import {
  LucideArrowLeft,
  LucideStore,
  LucideMapPin,
  LucideUser,
  LucideCreditCard,
  LucideBanknote,
  LucideTicket,
  LucideExternalLink,
  LucideXCircle,
  LucideAlertTriangle,
  LucideCheck,
  LucideClock,
  LucideUtensils,
  LucideShoppingBag,
  LucideMessageSquare
} from '@lucide/angular';
import { PedidoService } from '../../../../core/services/pedido.service';
import { PedidoDetalleResponse } from '../../../../core/models/pedido-detalle-response';
import { EstadoPedido } from '../../../../core/models/enums/estado-pedido.enum';
import { TipoEntrega } from '../../../../core/models/enums/tipo-entrega.enum';
import { MetodoPago } from '../../../../core/models/enums/metodo-pago.enum';

interface Step {
  estado: EstadoPedido;
  label: string;
}

@Component({
  selector: 'app-pedido-detalle',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    DecimalPipe,
    DatePipe,
    LucideArrowLeft,
    LucideStore,
    LucideMapPin,
    LucideUser,
    LucideCreditCard,
    LucideBanknote,
    LucideTicket,
    LucideExternalLink,
    LucideXCircle,
    LucideAlertTriangle,
    LucideCheck,
    LucideClock,
    LucideUtensils,
    LucideShoppingBag,
    LucideMessageSquare
  ],
  templateUrl: './pedido-detalle.component.html',
  styleUrl: './pedido-detalle.component.scss'
})
export class PedidoDetalleComponent implements OnInit, OnDestroy {

  private readonly route = inject(ActivatedRoute);
  private readonly pedidoService = inject(PedidoService);
  private routeSub?: Subscription;

  protected readonly EstadoPedido = EstadoPedido;
  protected readonly TipoEntrega = TipoEntrega;
  protected readonly MetodoPago = MetodoPago;

  pedido = signal<PedidoDetalleResponse | null>(null);
  isLoading = signal<boolean>(true);
  errorMsg = signal<string | null>(null);

  // Stepper de estados
  steps: Step[] = [
    { estado: EstadoPedido.PENDIENTE, label: 'Pendiente' },
    { estado: EstadoPedido.ACEPTADO, label: 'Aceptado' },
    { estado: EstadoPedido.EN_PREPARACION, label: 'En Preparación' },
    { estado: EstadoPedido.LISTO, label: 'Listo' },
    { estado: EstadoPedido.ENTREGADO, label: 'Entregado' }
  ];

  ngOnInit(): void {
    this.routeSub = this.route.paramMap.subscribe(params => {
      const idParam = params.get('id');
      if (idParam && !isNaN(+idParam)) {
        this.errorMsg.set(null);
        this.cargarDetalle(+idParam);
      } else {
        this.errorMsg.set('ID de pedido inválido');
        this.isLoading.set(false);
      }
    });
  }

  ngOnDestroy(): void {
    this.routeSub?.unsubscribe();
  }

  cargarDetalle(id: number): void {
    this.isLoading.set(true);
    this.pedidoService.obtenerPedidoPorId(id).subscribe({
      next: (data) => {
        this.pedido.set(data);
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Error al cargar detalle del pedido:', err);
        this.errorMsg.set(err.error?.message || err.error?.mensaje || 'No se pudo cargar el pedido especificado.');
        this.isLoading.set(false);
      }
    });
  }

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

    const currentIndex = order.indexOf(p.estado);
    const stepIndex = order.indexOf(stepEstado);

    return stepIndex <= currentIndex;
  }

  isStepCurrent(stepEstado: EstadoPedido): boolean {
    const p = this.pedido();
    if (!p) return false;
    return p.estado === stepEstado;
  }

  getEstadoLabel(estado: EstadoPedido): string {
    switch (estado) {
      case EstadoPedido.PENDIENTE: return 'Pendiente de aprobación';
      case EstadoPedido.ACEPTADO: return 'Pedido Aceptado';
      case EstadoPedido.EN_PREPARACION: return 'En preparación en cocina';
      case EstadoPedido.LISTO: return 'Listo para entregar / retirar';
      case EstadoPedido.ENTREGADO: return 'Entregado con éxito';
      case EstadoPedido.RECHAZADO: return 'Pedido Rechazado';
      default: return estado;
    }
  }

  getEstadoBadgeClass(estado: EstadoPedido): string {
    switch (estado) {
      case EstadoPedido.PENDIENTE: return 'badge--warning';
      case EstadoPedido.ACEPTADO:
      case EstadoPedido.EN_PREPARACION: return 'badge--info';
      case EstadoPedido.LISTO: return 'badge--purple';
      case EstadoPedido.ENTREGADO: return 'badge--success';
      case EstadoPedido.RECHAZADO: return 'badge--danger';
      default: return 'badge--neutral';
    }
  }
}
