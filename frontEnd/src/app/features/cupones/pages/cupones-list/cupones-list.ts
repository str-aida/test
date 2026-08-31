import { ChangeDetectorRef, Component, inject, OnInit, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { CuponService } from '../../../../core/services/cupon.service';
import { NotificationService } from '../../../../core/services/notification.service';
import { CuponResponse } from '../../../../core/models/cupon-response';
import { EstadoCupon } from '../../../../core/models/enums/estado-cupon.enum';
import { TipoDescuento } from '../../../../core/models/enums/tipo-descuento.enum';
import { CuponFormComponent } from '../../components/cupon-form/cupon-form';
import { CuponAsignarModalComponent } from '../../components/cupon-asignar-modal/cupon-asignar-modal';
import {
  LucideTicket,
  LucidePlus,
  LucideUserCheck,
  LucideUserPlus,
  LucidePencil,
  LucideBan,
  LucideX,
  LucideAlertTriangle,
  LucideRefreshCw,
  LucideSearch,
  LucideFilter,
  LucideChevronLeft,
  LucideChevronRight
} from '@lucide/angular';
import { FormsModule } from '@angular/forms';

import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-cupones-list',
  imports: [
    CommonModule,
    FormsModule,
    CuponFormComponent,
    CuponAsignarModalComponent,
    LucideTicket,
    LucidePlus,
    LucideUserCheck,
    LucideUserPlus,
    LucidePencil,
    LucideBan,
    LucideX,
    LucideAlertTriangle,
    LucideRefreshCw,
    LucideSearch,
    LucideFilter,
    LucideChevronLeft,
    LucideChevronRight
  ],
  templateUrl: './cupones-list.html',
  styleUrl: './cupones-list.scss',
})
export class CuponesListComponent implements OnInit {
  private readonly platformId = inject(PLATFORM_ID);
  private readonly cuponService = inject(CuponService);
  private readonly notificationService = inject(NotificationService);
  private readonly cdr = inject(ChangeDetectorRef);

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

  readonly EstadoCuponEnum = EstadoCupon;
  readonly TipoDescuentoEnum = TipoDescuento;

  ngOnInit(): void {
    if (isPlatformBrowser(this.platformId)) {
      this.loadCupones();
    }
  }

  loadCupones(): void {
    this.isLoading = true;
    this.hasError = false;
    this.cdr.detectChanges();

    this.cuponService.listarCupones().subscribe({
      next: (data) => {
        this.cupones = data || [];
        this.applyFilter();
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error al cargar cupones:', err);
        this.isLoading = false;
        this.hasError = true;
        this.cdr.detectChanges();
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
  }

  onFormSubmitted(): void {
    this.closeFormModal();
    this.loadCupones();
  }

  openAssignModal(cupon?: CuponResponse): void {
    this.selectedCupon = cupon || null;
    this.showAssignModal = true;
  }

  closeAssignModal(): void {
    this.showAssignModal = false;
    this.selectedCupon = null;
  }

  onAssigned(): void {
    this.closeAssignModal();
    this.loadCupones();
  }

  openDeactivateModal(cupon: CuponResponse): void {
    this.selectedCupon = cupon;
    this.showDeactivateModal = true;
  }

  closeDeactivateModal(): void {
    this.showDeactivateModal = false;
    this.selectedCupon = null;
  }

  confirmDeactivate(): void {
    if (!this.selectedCupon) return;

    const cuponId = this.selectedCupon.id;
    this.isDeactivating = true;

    this.cuponService.desactivarCupon(cuponId).subscribe({
      next: () => {
        this.isDeactivating = false;
        this.notificationService.success('Cupón desactivado correctamente.');
        this.closeDeactivateModal();
        this.loadCupones();
      },
      error: (err) => {
        this.isDeactivating = false;
        const msg = err?.error?.message || 'Error al desactivar el cupón';
        this.notificationService.error(msg);
      }
    });
  }

  formatValor(cupon: CuponResponse): string {
    if (cupon.tipoDescuento === TipoDescuento.PORCENTAJE) {
      return `${cupon.valor}%`;
    }
    return `$${cupon.valor}`;
  }
}
