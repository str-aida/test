import { ChangeDetectorRef, Component, inject, OnInit, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser, CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { DescuentoService } from '../../../../core/services/descuento.service';
import { NotificationService } from '../../../../core/services/notification.service';
import { DescuentoResponse } from '../../../../core/models/descuento-response';
import { EstadoDescuento } from '../../../../core/models/enums/estado-descuento.enum';
import { TipoCampanaDescuento } from '../../../../core/models/enums/tipo-campana-descuento.enum';
import { DescuentoFormComponent } from '../descuento-form/descuento-form';
import {
  LucideTag,
  LucidePackage,
  LucideCalendar,
  LucidePlus,
  LucideSearch,
  LucidePencil,
  LucidePower,
  LucideUtensilsCrossed,
  LucidePartyPopper,
  LucideSun,
  LucideSnowflake,
  LucideMegaphone,
  LucideRefreshCw,
  LucideChevronLeft,
  LucideChevronRight,
  LucidePercent,
  LucideTriangleAlert,
  LucideX
} from '@lucide/angular';

@Component({
  selector: 'app-descuentos-list',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    DescuentoFormComponent,
    LucideTag,
    LucidePackage,
    LucideCalendar,
    LucidePlus,
    LucideSearch,
    LucidePencil,
    LucidePower,
    LucideUtensilsCrossed,
    LucidePartyPopper,
    LucideSun,
    LucideSnowflake,
    LucideMegaphone,
    LucideRefreshCw,
    LucideChevronLeft,
    LucideChevronRight,
    LucidePercent,
    LucideTriangleAlert,
    LucideX
  ],
  templateUrl: './descuentos-list.html',
  styleUrl: './descuentos-list.scss'
})
export class DescuentosListComponent implements OnInit {
  private readonly platformId = inject(PLATFORM_ID);
  private readonly descuentoService = inject(DescuentoService);
  private readonly notificationService = inject(NotificationService);
  private readonly router = inject(Router);
  private readonly cdr = inject(ChangeDetectorRef);

  descuentos: DescuentoResponse[] = [];
  filteredDescuentos: DescuentoResponse[] = [];

  searchTerm = '';
  statusTab: 'TODOS' | 'ACTIVO' | 'INACTIVO' = 'TODOS';

  isLoading = false;
  hasError = false;

  // Modales
  showCreateModal = false;
  showEditModal = false;
  showDeleteModal = false;
  selectedDescuento: DescuentoResponse | null = null;
  isProcessingAction = false;

  readonly EstadoDescuentoEnum = EstadoDescuento;
  readonly TipoCampanaEnum = TipoCampanaDescuento;

  ngOnInit(): void {
    if (isPlatformBrowser(this.platformId)) {
      this.loadDescuentos();
    }
  }

  loadDescuentos(): void {
    this.isLoading = true;
    this.hasError = false;
    this.cdr.markForCheck();

    this.descuentoService.listarDescuentos().subscribe({
      next: (data) => {
        this.descuentos = data || [];
        this.applyFilter();
        this.isLoading = false;
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Error al cargar descuentos:', err);
        this.isLoading = false;
        this.hasError = true;
        this.cdr.markForCheck();
      }
    });
  }

  applyFilter(): void {
    let result = [...this.descuentos];

    if (this.searchTerm?.trim()) {
      const term = this.searchTerm.trim().toLowerCase();
      result = result.filter((d) => d.nombre.toLowerCase().includes(term));
    }

    if (this.statusTab !== 'TODOS') {
      result = result.filter((d) => d.estado === this.statusTab);
    }

    this.filteredDescuentos = result;
  }

  setStatusTab(tab: 'TODOS' | 'ACTIVO' | 'INACTIVO'): void {
    this.statusTab = tab;
    this.applyFilter();
  }

  // Métricas calculadas para Stitch Dashboard Cards
  get totalActivos(): number {
    return this.descuentos.filter((d) => d.estado === EstadoDescuento.ACTIVO).length;
  }

  get totalDescuentos(): number {
    return this.descuentos.length;
  }

  get totalInactivos(): number {
    return this.descuentos.filter((d) => d.estado === EstadoDescuento.INACTIVO).length;
  }

  get totalProductosAlcanzados(): number {
    const productosMap = new Set<number>();
    this.descuentos
      .filter((d) => d.estado === EstadoDescuento.ACTIVO)
      .forEach((d) => {
        d.productos?.forEach((p) => productosMap.add(p.productoId));
      });
    return productosMap.size;
  }

  get proximoVencimiento(): { fecha: string; nombre: string } | null {
    const hoy = new Date();
    hoy.setHours(0, 0, 0, 0);

    const activosConFecha = this.descuentos
      .filter((d) => d.estado === EstadoDescuento.ACTIVO && d.fechaFin)
      .map((d) => ({
        nombre: d.nombre,
        fechaFin: d.fechaFin,
        dateObj: new Date(d.fechaFin)
      }))
      .filter((item) => !isNaN(item.dateObj.getTime()) && item.dateObj >= hoy)
      .sort((a, b) => a.dateObj.getTime() - b.dateObj.getTime());

    if (activosConFecha.length > 0) {
      return {
        fecha: activosConFecha[0].fechaFin,
        nombre: activosConFecha[0].nombre
      };
    }
    return null;
  }

  getTipoBadgeLabel(tipo: TipoCampanaDescuento): string {
    switch (tipo) {
      case TipoCampanaDescuento.DIA_FESTIVO:
        return 'Día Festivo';
      case TipoCampanaDescuento.TEMPORADA:
        return 'Temporada';
      case TipoCampanaDescuento.LIQUIDACION:
        return 'Liquidación';
      case TipoCampanaDescuento.GENERAL:
        return 'General';
      default:
        return tipo;
    }
  }

  openCreateModal(): void {
    this.showCreateModal = true;
    this.cdr.markForCheck();
  }

  closeCreateModal(): void {
    this.showCreateModal = false;
    this.cdr.markForCheck();
  }

  onDescuentoCreated(): void {
    this.closeCreateModal();
    this.loadDescuentos();
  }

  openEditModal(descuento: DescuentoResponse): void {
    this.selectedDescuento = descuento;
    this.showEditModal = true;
    this.cdr.markForCheck();
  }

  closeEditModal(): void {
    this.showEditModal = false;
    this.selectedDescuento = null;
    this.cdr.markForCheck();
  }

  onDescuentoUpdated(): void {
    this.closeEditModal();
    this.loadDescuentos();
  }

  openDeleteModal(descuento: DescuentoResponse): void {
    this.selectedDescuento = descuento;
    this.showDeleteModal = true;
    this.cdr.markForCheck();
  }

  closeDeleteModal(): void {
    this.showDeleteModal = false;
    this.selectedDescuento = null;
    this.isProcessingAction = false;
    this.cdr.markForCheck();
  }

  navegarCrear(): void {
    this.openCreateModal();
  }

  navegarEditar(descuento: DescuentoResponse): void {
    this.openEditModal(descuento);
  }

  toggleEstado(descuento: DescuentoResponse): void {
    if (descuento.estado === EstadoDescuento.ACTIVO) {
      this.openDeleteModal(descuento);
    } else {
      this.activar(descuento);
    }
  }

  activar(descuento: DescuentoResponse): void {
    this.isProcessingAction = true;
    this.descuentoService.activarDescuento(descuento.id).subscribe({
      next: (res) => {
        this.notificationService.success(res?.mensaje || `Descuento "${descuento.nombre}" activado.`);
        this.isProcessingAction = false;
        this.loadDescuentos();
      },
      error: (err) => {
        console.error('Error al activar descuento:', err);
        const msg = err?.error?.message || err?.error?.mensaje || 'Error al activar el descuento.';
        this.notificationService.error(msg);
        this.isProcessingAction = false;
        this.cdr.markForCheck();
      }
    });
  }

  confirmarDesactivar(): void {
    if (!this.selectedDescuento) return;

    const id = this.selectedDescuento.id;
    const nombre = this.selectedDescuento.nombre;
    this.isProcessingAction = true;

    this.descuentoService.desactivarDescuento(id).subscribe({
      next: (res) => {
        this.notificationService.success(res?.mensaje || `Descuento "${nombre}" desactivado.`);
        this.closeDeleteModal();
        this.loadDescuentos();
      },
      error: (err) => {
        console.error('Error al desactivar descuento:', err);
        const msg = err?.error?.message || err?.error?.mensaje || 'Error al desactivar el descuento.';
        this.notificationService.error(msg);
        this.isProcessingAction = false;
        this.cdr.markForCheck();
      }
    });
  }
}