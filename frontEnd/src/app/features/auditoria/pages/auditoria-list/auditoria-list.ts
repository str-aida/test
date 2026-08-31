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
  isExporting = signal<boolean>(false);
  hasError = signal<boolean>(false);
  mostrarFiltros = false;

  // Filtros
  filtroAccion = '';
  filtroRol: UserRole | '' = '';
  filtroUsuario = '';

  // Paginación desde Backend (0-indexed en API)
  currentPage = signal<number>(0);
  pageSize = signal<number>(20);
  totalElementos = signal<number>(0);
  totalPaginas = signal<number>(0);
  primera = signal<boolean>(true);
  ultima = signal<boolean>(true);

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

    this.auditService.listarLogs(this.currentPage(), this.pageSize(), filter).subscribe({
      next: (response) => {
        this.logs.set(response.content || []);
        this.currentPage.set(response.pagina);
        this.pageSize.set(response.size);
        this.totalElementos.set(response.totalElementos);
        this.totalPaginas.set(response.totalPaginas);
        this.primera.set(response.primera);
        this.ultima.set(response.ultima);
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
    this.currentPage.set(0);
    this.cargarLogs();
  }

  limpiarFiltros(): void {
    this.filtroAccion = '';
    this.filtroRol = '';
    this.filtroUsuario = '';
    this.currentPage.set(0);
    this.cargarLogs();
  }

  // Paginación (1-based para la UI)
  get displayCurrentPage(): number {
    return this.currentPage() + 1;
  }

  get pageNumbers(): number[] {
    const total = this.totalPaginas();
    const current = this.displayCurrentPage;
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

  goToPage(pageOneBased: number): void {
    if (pageOneBased >= 1 && pageOneBased <= this.totalPaginas()) {
      this.currentPage.set(pageOneBased - 1);
      this.cargarLogs();
    }
  }

  prevPage(): void {
    if (!this.primera()) {
      this.goToPage(this.displayCurrentPage - 1);
    }
  }

  nextPage(): void {
    if (!this.ultima()) {
      this.goToPage(this.displayCurrentPage + 1);
    }
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
    if (this.isExporting()) return;

    this.isExporting.set(true);

    const filter: LogSistemaFilter = {};
    if (this.filtroAccion.trim()) filter.accion = this.filtroAccion.trim();
    if (this.filtroRol) filter.rol = this.filtroRol as UserRole;
    if (this.filtroUsuario.trim()) filter.usuario = this.filtroUsuario.trim();

    this.auditService.exportarPdf(filter).subscribe({
      next: (blob: Blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'auditoria.pdf';
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        window.URL.revokeObjectURL(url);
        this.isExporting.set(false);
        this.notificationService.success('Archivo auditoria.pdf descargado correctamente.');
      },
      error: (err) => {
        this.isExporting.set(false);
        const mensaje = err?.error?.message || 'Error al exportar los logs de auditoría a PDF.';
        this.notificationService.error(mensaje);
      }
    });
  }

}

