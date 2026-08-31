import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { CommonModule, DatePipe, DecimalPipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import {
  LucideCalendar,
  LucideTruck,
  LucideStore,
  LucideCreditCard,
  LucideBanknote,
  LucideWallet,
  LucideArrowRight,
  LucideChevronLeft,
  LucideChevronRight,
  LucideShoppingBag,
  LucidePackage
} from '@lucide/angular';
import { PedidoService } from '../../../../core/services/pedido.service';
import { PedidoResponse } from '../../../../core/models/pedido-response';
import { PageResponse } from '../../../../core/models/page-response';
import { EstadoPedido } from '../../../../core/models/enums/estado-pedido.enum';
import { TipoEntrega } from '../../../../core/models/enums/tipo-entrega.enum';
import { MetodoPago } from '../../../../core/models/enums/metodo-pago.enum';

@Component({
  selector: 'app-pedidos',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    DecimalPipe,
    DatePipe,
    LucideCalendar,
    LucideTruck,
    LucideStore,
    LucideCreditCard,
    LucideBanknote,
    LucideWallet,
    LucideArrowRight,
    LucideChevronLeft,
    LucideChevronRight,
    LucideShoppingBag,
    LucidePackage
  ],
  templateUrl: './pedidos.component.html',
  styleUrl: './pedidos.component.scss'
})
export class PedidosComponent implements OnInit {

  private readonly pedidoService = inject(PedidoService);

  protected readonly EstadoPedido = EstadoPedido;
  protected readonly TipoEntrega = TipoEntrega;
  protected readonly MetodoPago = MetodoPago;

  pageData = signal<PageResponse<PedidoResponse> | null>(null);
  pedidos = signal<PedidoResponse[]>([]);
  isLoading = signal<boolean>(true);

  // Paginación
  currentPage = signal<number>(1);
  pageSize = signal<number>(5);

  // Computados
  totalPedidos = computed(() => this.pedidos().length);
  totalPages = computed(() => Math.ceil(this.totalPedidos() / this.pageSize()) || 1);

  paginatedPedidos = computed(() => {
    const start = (this.currentPage() - 1) * this.pageSize();
    return this.pedidos().slice(start, start + this.pageSize());
  });

  ngOnInit(): void {
    this.cargarPedidos();
  }

  cargarPedidos(): void {
    this.isLoading.set(true);
    this.pedidoService.listarMisPedidos().subscribe({
      next: (data: PageResponse<PedidoResponse>) => {
        this.pageData.set(data);
        const list = data?.content || [];
        const ordenados = [...list].sort((a, b) => b.id - a.id);
        this.pedidos.set(ordenados);
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Error al cargar pedidos:', err);
        this.isLoading.set(false);
      }
    });
  }

  goToPage(page: number | string): void {
    if (typeof page === 'number' && page >= 1 && page <= this.totalPages()) {
      this.currentPage.set(page);
    }
  }

  getPageNumbers(): (number | string)[] {
    const total = this.totalPages();
    const current = this.currentPage();
    if (total <= 5) {
      return Array.from({ length: total }, (_, i) => i + 1);
    }

    const pages: (number | string)[] = [1];
    if (current > 3) pages.push('...');
    
    const start = Math.max(2, current - 1);
    const end = Math.min(total - 1, current + 1);

    for (let i = start; i <= end; i++) {
      if (!pages.includes(i)) pages.push(i);
    }

    if (current < total - 2) pages.push('...');
    if (!pages.includes(total)) pages.push(total);

    return pages;
  }

  formatNumeroPedido(pedido: PedidoResponse): string {
    if (pedido.numeroPedido) {
      return pedido.numeroPedido.startsWith('#') ? pedido.numeroPedido : `#${pedido.numeroPedido}`;
    }
    return `#PED-${String(pedido.id).padStart(7, '0')}`;
  }

  isActivo(estado: EstadoPedido): boolean {
    return (
      estado === EstadoPedido.PENDIENTE ||
      estado === EstadoPedido.ACEPTADO ||
      estado === EstadoPedido.EN_PREPARACION ||
      estado === EstadoPedido.LISTO
    );
  }

  getEstadoBadgeClass(estado: EstadoPedido): string {
    switch (estado) {
      case EstadoPedido.PENDIENTE:
        return 'badge--pendiente';
      case EstadoPedido.ACEPTADO:
      case EstadoPedido.EN_PREPARACION:
        return 'badge--preparacion';
      case EstadoPedido.LISTO:
        return 'badge--listo';
      case EstadoPedido.ENTREGADO:
        return 'badge--entregado';
      case EstadoPedido.RECHAZADO:
        return 'badge--rechazado';
      default:
        return 'badge--neutral';
    }
  }

  getEstadoLabel(estado: EstadoPedido): string {
    switch (estado) {
      case EstadoPedido.PENDIENTE:
        return 'PENDIENTE';
      case EstadoPedido.ACEPTADO:
        return 'ACEPTADO';
      case EstadoPedido.EN_PREPARACION:
        return 'EN PREPARACIÓN';
      case EstadoPedido.LISTO:
        return 'LISTO';
      case EstadoPedido.ENTREGADO:
        return 'ENTREGADO';
      case EstadoPedido.RECHAZADO:
        return 'RECHAZADO';
      default:
        return String(estado).toUpperCase();
    }
  }
}

