import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
  LucideSearch,
  LucideSlidersHorizontal,
  LucideDownload,
  LucideAlertTriangle,
  LucideInbox,
  LucideShieldCheck
} from '@lucide/angular';
import { AuditService } from '../../../../core/services/audit.service';
import { NotificationService } from '../../../../core/services/notification.service';
import { LogSistemaResponse } from '../../../../core/models/log-sistema-response';
import { LogSistemaFilter } from '../../../../core/models/log-sistema-filter';
import { TipoOperacion } from '../../../../core/models/enums/tipo-operacion.enum';
import { UserRole } from '../../../../core/models/enums/user-role.enum';

@Component({
  selector: 'app-auditoria-list',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    DatePipe,
    LucideSearch,
    LucideSlidersHorizontal,
    LucideDownload,
    LucideAlertTriangle,
    LucideInbox,
    LucideShieldCheck
  ],
  templateUrl: './auditoria-list.html',
  styleUrl: './auditoria-list.scss'
})
export class AuditoriaListComponent implements OnInit {

  private readonly auditService = inject(AuditService);
  private readonly notificationService = inject(NotificationService);

  protected readonly TipoOperacion = TipoOperacion;
  protected readonly UserRole = UserRole;
  protected readonly rolesDisponibles = Object.values(UserRole);

  // Estado
  logs = signal<LogSistemaResponse[]>([]);
  isLoading = signal<boolean>(false);
  hasError = signal<boolean>(false);
  mostrarFiltros = false;

  // Filtros
  filtroAccion = '';
  filtroRol: UserRole | '' = '';
  filtroUsuario = '';

  // Paginación
  readonly PAGE_SIZE = 10;
  currentPage = signal<number>(1);

  ngOnInit(): void {
    this.cargarLogs();
  }

  cargarLogs(): void {
    this.isLoading.set(true);
    this.hasError.set(false);

    const filter: LogSistemaFilter = {};
    if (this.filtroAccion.trim()) filter.accion = this.filtroAccion.trim();
    if (this.filtroRol) filter.rol = this.filtroRol as UserRole;
    if (this.filtroUsuario.trim()) filter.usuario = this.filtroUsuario.trim();

    this.auditService.listarLogs(filter).subscribe({
      next: (data) => {
        this.logs.set(data);
        this.currentPage.set(1);
        this.isLoading.set(false);
      },
      error: () => {
        this.hasError.set(true);
        this.isLoading.set(false);
      }
    });
  }

  toggleFiltros(): void {
    this.mostrarFiltros = !this.mostrarFiltros;
  }

  buscar(): void {
    this.cargarLogs();
  }

  limpiarFiltros(): void {
    this.filtroAccion = '';
    this.filtroRol = '';
    this.filtroUsuario = '';
    this.cargarLogs();
  }

  // Paginación
  get totalPages(): number {
    return Math.ceil(this.logs().length / this.PAGE_SIZE) || 1;
  }

  get logsPaginados(): LogSistemaResponse[] {
    const inicio = (this.currentPage() - 1) * this.PAGE_SIZE;
    return this.logs().slice(inicio, inicio + this.PAGE_SIZE);
  }

  get pageNumbers(): number[] {
    const total = this.totalPages;
    const current = this.currentPage();
    const pages: number[] = [];
    const maxVisible = 5;

    let start = Math.max(1, current - Math.floor(maxVisible / 2));
    const end = Math.min(total, start + maxVisible - 1);
    if (end - start < maxVisible - 1) {
      start = Math.max(1, end - maxVisible + 1);
    }
    for (let i = start; i <= end; i++) {
      pages.push(i);
    }
    return pages;
  }

  goToPage(page: number): void {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage.set(page);
    }
  }

  prevPage(): void {
    this.goToPage(this.currentPage() - 1);
  }

  nextPage(): void {
    this.goToPage(this.currentPage() + 1);
  }

  // Formatters & Label Helpers para Stitch
  getTipoBadgeClass(tipo: TipoOperacion): string {
    switch (tipo) {
      case TipoOperacion.INSERT: return 'tipo-badge--create';
      case TipoOperacion.UPDATE: return 'tipo-badge--update';
      case TipoOperacion.DELETE: return 'tipo-badge--delete';
      default: return 'tipo-badge--neutral';
    }
  }

  getTipoOperacionBadgeLabel(tipo: TipoOperacion): string {
    switch (tipo) {
      case TipoOperacion.INSERT: return 'CREATE';
      case TipoOperacion.UPDATE: return 'UPDATE';
      case TipoOperacion.DELETE: return 'DELETE';
      default: return tipo || 'INFO';
    }
  }

  getAccionLabel(log: LogSistemaResponse): string {
    if (log.accion) return log.accion;
    switch (log.tipoOperacion) {
      case TipoOperacion.INSERT: return 'Creación';
      case TipoOperacion.UPDATE: return 'Modificación';
      case TipoOperacion.DELETE: return 'Eliminación';
      default: return 'Acción';
    }
  }

  getDescripcion(log: LogSistemaResponse): string {
    if (log.descripcion) return log.descripcion;
    const modulo = log.tablaAfectada || 'registro';
    switch (log.tipoOperacion) {
      case TipoOperacion.INSERT: return `Nuevo ${modulo.toLowerCase()} agregado...`;
      case TipoOperacion.UPDATE: return `${modulo} modificado correctamente...`;
      case TipoOperacion.DELETE: return `${modulo} eliminado del sistema...`;
      default: return `Registro de ${modulo.toLowerCase()}`;
    }
  }

  descargarPDF(): void {
    this.notificationService.error(
      'La descarga de PDF no está disponible actualmente. El backend no proporciona este endpoint.'
    );
  }

}
