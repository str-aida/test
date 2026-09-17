import { ChangeDetectorRef, Component, inject, OnInit, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser, CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CuponService } from '../../../../core/services/cupon.service';
import { NotificationService } from '../../../../core/services/notification.service';
import { CuponResponse } from '../../../../core/models/cupon-response';
import { ReglaCuponResponse } from '../../../../core/models/regla-cupon-response';
import { EstadoCupon } from '../../../../core/models/enums/estado-cupon.enum';
import { TipoDescuento } from '../../../../core/models/enums/tipo-descuento.enum';
import { TipoAsignacionCupon } from '../../../../core/models/enums/tipo-asignacion-cupon.enum';
import { CuponFormComponent } from '../../components/cupon-form/cupon-form';
import { CuponAsignarModalComponent } from '../../components/cupon-asignar-modal/cupon-asignar-modal';
import { ReglaCuponFormComponent } from '../../components/regla-cupon-form/regla-cupon-form';
import {
  LucideTicket,
  LucidePlus,
  LucideUserCheck,
  LucideUserPlus,
  LucidePencil,
  LucideBan,
  LucideX,
  LucideTriangleAlert,
  LucideRefreshCw,
  LucideSearch,
  LucideListFilter,
  LucideChevronLeft,
  LucideChevronRight,
  LucideSlidersHorizontal,
  LucideSparkles,
  LucideGift,
  LucideShoppingBag
} from '@lucide/angular';

@Component({
  selector: 'app-cupones-list',
  imports: [
    CommonModule,
    FormsModule,
    CuponFormComponent,
    CuponAsignarModalComponent,
    ReglaCuponFormComponent,
    LucideTicket,
    LucidePlus,
    LucideUserCheck,
    LucideUserPlus,
    LucidePencil,
    LucideBan,
    LucideX,
    LucideTriangleAlert,
    LucideRefreshCw,
    LucideSearch,
    LucideListFilter,
    LucideChevronLeft,
    LucideChevronRight,
    LucideSlidersHorizontal,
    LucideSparkles,
    LucideGift,
    LucideShoppingBag
  ],
  templateUrl: './cupones-list.html',
  styleUrl: './cupones-list.scss',
})
export class CuponesListComponent implements OnInit {
  private readonly platformId = inject(PLATFORM_ID);
  private readonly cuponService = inject(CuponService);
  private readonly notificationService = inject(NotificationService);
  private readonly cdr = inject(ChangeDetectorRef);

  // Cupones manuales / emitidos
  cupones: CuponResponse[] = [];
  filteredCupones: CuponResponse[] = [];
  searchTerm = '';
  statusFilter = 'TODOS';

  isLoading = false;
  hasError = false;

  showFormModal = false;
  showAssignModal = false;
  showDeactivateModal = false;

  selectedCupon: CuponResponse | null = null;
  isDeactivating = false;

  // Reglas de Estrategias Automáticas
  reglas: ReglaCuponResponse[] = [];
  isLoadingReglas = false;
  hasErrorReglas = false;

  showReglaModal = false;
  selectedRegla: ReglaCuponResponse | null = null;

  readonly EstadoCuponEnum = EstadoCupon;
  readonly TipoDescuentoEnum = TipoDescuento;
  readonly TipoAsignacionEnum = TipoAsignacionCupon;

  ngOnInit(): void {
    if (isPlatformBrowser(this.platformId)) {
      this.loadCupones();
      this.loadReglas();
    }
  }

  loadCupones(): void {
    this.isLoading = true;
    this.hasError = false;
    this.cdr.markForCheck();

    this.cuponService.listarCupones().subscribe({
      next: (data) => {
        this.cupones = data || [];
        this.applyFilter();
        this.isLoading = false;
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Error al cargar cupones:', err);
        this.isLoading = false;
        this.hasError = true;
        this.cdr.markForCheck();
      }
    });
  }

  loadReglas(): void {
    this.isLoadingReglas = true;
    this.hasErrorReglas = false;
    this.cdr.markForCheck();

    this.cuponService.listarReglas().subscribe({
      next: (data) => {
        this.reglas = data || [];
        this.isLoadingReglas = false;
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Error al cargar reglas de cupones:', err);
        this.isLoadingReglas = false;
        this.hasErrorReglas = true;
        this.cdr.markForCheck();
      }
    });
  }

  applyFilter(): void {
    let result = [...this.cupones];

    if (this.searchTerm?.trim()) {
      const term = this.searchTerm.trim().toLowerCase();
      result = result.filter((c) => c.codigo.toLowerCase().includes(term));
    }

    if (this.statusFilter !== 'TODOS') {
      result = result.filter((c) => c.estado === this.statusFilter);
    }

    this.filteredCupones = result;
  }

  openCreateModal(): void {
    this.selectedCupon = null;
    this.showFormModal = true;
  }

  openEditModal(cupon: CuponResponse): void {
    this.selectedCupon = cupon;
    this.showFormModal = true;
  }

  closeFormModal(): void {
    this.showFormModal = false;
    this.selectedCupon = null;
    this.cdr.markForCheck();
  }

  onFormSubmitted(): void {
    this.closeFormModal();
    this.loadCupones();
  }

  openAssignModal(cupon?: CuponResponse): void {
    this.selectedCupon = cupon || null;
    this.showAssignModal = true;
    this.cdr.markForCheck();
  }

  closeAssignModal(): void {
    this.showAssignModal = false;
    this.selectedCupon = null;
    this.cdr.markForCheck();
  }

  onAssigned(): void {
    this.closeAssignModal();
    this.loadCupones();
  }

  openDeactivateModal(cupon: CuponResponse): void {
    this.selectedCupon = cupon;
    this.showDeactivateModal = true;
    this.cdr.markForCheck();
  }

  closeDeactivateModal(): void {
    this.showDeactivateModal = false;
    this.selectedCupon = null;
    this.cdr.markForCheck();
  }

  confirmDeactivate(): void {
    if (!this.selectedCupon) return;

    const cuponId = this.selectedCupon.id;
    this.closeDeactivateModal();

    this.cuponService.desactivarCupon(cuponId).subscribe({
      next: () => {
        this.notificationService.success('Cupón desactivado correctamente.');
        this.loadCupones();
      },
      error: (err) => {
        const msg = err?.error?.message || 'Error al desactivar el cupón';
        this.notificationService.error(msg);
      }
    });
  }

  // === GESTIÓN DE REGLAS / STRATEGIES ===

  openEditReglaModal(regla: ReglaCuponResponse): void {
    this.selectedRegla = regla;
    this.showReglaModal = true;
    this.cdr.markForCheck();
  }

  closeReglaModal(): void {
    this.showReglaModal = false;
    this.selectedRegla = null;
    this.cdr.markForCheck();
  }

  onReglaSubmitted(): void {
    this.closeReglaModal();
    this.loadReglas();
  }

  formatValor(cupon: CuponResponse): string {
    if (cupon.tipoDescuento === TipoDescuento.PORCENTAJE) {
      return `${cupon.valor}%`;
    }
    return `$${cupon.valor}`;
  }

  formatReglaValor(regla: ReglaCuponResponse): string {
    if (regla.tipoDescuento === TipoDescuento.PORCENTAJE) {
      return `${regla.valor}%`;
    }
    return `$${regla.valor}`;
  }

  getReglaNombre(tipo: TipoAsignacionCupon): string {
    switch (tipo) {
      case TipoAsignacionCupon.CUMPLEANOS:
        return 'Cupón de Cumpleaños';
      case TipoAsignacionCupon.CANTIDAD_COMPRAS:
        return 'Fidelidad por Cantidad de Compras';
      case TipoAsignacionCupon.BIENVENIDA:
        return 'Cupón de Bienvenida';
      case TipoAsignacionCupon.REFERIDO:
        return 'Cupón por Referido';
      default:
        return tipo;
    }
  }

  getReglaCondicion(regla: ReglaCuponResponse): string {
    switch (regla.tipoAsignacion) {
      case TipoAsignacionCupon.CUMPLEANOS:
        return 'Se otorga automáticamente el día del cumpleaños';
      case TipoAsignacionCupon.CANTIDAD_COMPRAS:
        const cada = regla.cantidadComprasRequeridas || 3;
        return `Se otorga cada ${cada} ${cada === 1 ? 'compra entregada' : 'compras entregadas'}`;
      case TipoAsignacionCupon.BIENVENIDA:
        return 'Se otorga en el primer registro o primera compra';
      case TipoAsignacionCupon.REFERIDO:
        return 'Se otorga cuando un amigo invitado realiza su compra';
      default:
        return regla.descripcion || 'Asignación automática por regla';
    }
  }

  esCuponAsignable(cupon: CuponResponse): boolean {
    if (cupon.estado !== EstadoCupon.ACTIVO) return false;

    if (cupon.cuposDisponibles !== null && cupon.cuposDisponibles === 0) return false;

    const hoy = new Date();
    hoy.setHours(0, 0, 0, 0);

    if (cupon.fechaInicio) {
      const inicio = new Date(cupon.fechaInicio);
      if (inicio > hoy) return false;
    }

    if (cupon.fechaFin) {
      const fin = new Date(cupon.fechaFin);
      if (fin < hoy) return false;
    }

    return true;
  }

  getTooltipAsignar(cupon: CuponResponse): string {
    if (cupon.cuposDisponibles !== null && cupon.cuposDisponibles === 0) {
      return 'El cupón no tiene cupos disponibles para asignar';
    }

    if (cupon.estado !== EstadoCupon.ACTIVO) return 'El cupón está inactivo';

    const hoy = new Date();
    hoy.setHours(0, 0, 0, 0);

    if (cupon.fechaFin && new Date(cupon.fechaFin) < hoy) {
      return 'El cupón está vencido';
    }

    if (cupon.fechaInicio && new Date(cupon.fechaInicio) > hoy) {
      return 'El cupón aún no está vigente';
    }

    return 'Asignar cupón a usuario';
  }
}
