import { Component, EventEmitter, inject, OnInit, Output, signal, computed } from '@angular/core';
import { CategoriaService } from '../../../../core/services/categoria.service';
import { CategoriaResponse } from '../../../../core/models/categoria-response';
import { FormsModule } from '@angular/forms';
import {
  LucidePencil,
  LucideTrash2,
  LucideShapes,
  LucideSearch,
  LucideFilter,
  LucideRefreshCw,
  LucideAlertTriangle,
  LucideX
} from '@lucide/angular';
import { Estado } from '../../../../core/models/enums/estado.enum';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-categories-table',
  imports: [
    CommonModule,
    FormsModule,
    LucidePencil,
    LucideTrash2,
    LucideShapes,
    LucideSearch,
    LucideFilter,
    LucideRefreshCw,
    LucideAlertTriangle,
    LucideX
  ],
  templateUrl: './categories-table.html',
  styleUrl: './categories-table.scss',
})
export class CategoriesTableComponent implements OnInit {

  private readonly categoriaService = inject(CategoriaService);
  protected readonly Estado = Estado;

  categories = signal<CategoriaResponse[]>([]);
  isLoading = signal<boolean>(false);
  hasError = signal<boolean>(false);

  searchTerm = signal<string>('');
  statusFilter = signal<string>('TODOS');

  @Output() editCategory = new EventEmitter<CategoriaResponse>();
  @Output() deleteCategory = new EventEmitter<CategoriaResponse>();

  filteredCategories = computed(() => {
    let list = this.categories();
    const search = this.searchTerm().trim().toLowerCase();
    const filter = this.statusFilter();

    if (search) {
      list = list.filter(c =>
        c.nombre.toLowerCase().includes(search) ||
        (c.descripcion && c.descripcion.toLowerCase().includes(search))
      );
    }

    if (filter !== 'TODOS') {
      list = list.filter(c => c.estado === filter);
    }

    return list;
  });

  ngOnInit(): void {
    this.loadCategories();
  }

  loadCategories(): void {
    this.isLoading.set(true);
    this.hasError.set(false);

    this.categoriaService
      .listarCategorias()
      .subscribe({
        next: categories => {
          this.categories.set(categories || []);
          this.isLoading.set(false);
        },
        error: err => {
          console.error('Error al cargar categorias', err);
          this.isLoading.set(false);
          this.hasError.set(true);
        }
      });
  }

  edit(category: CategoriaResponse): void {
    this.editCategory.emit(category);
  }

  delete(category: CategoriaResponse): void {
    this.deleteCategory.emit(category);
  }

  clearFilters(): void {
    this.searchTerm.set('');
    this.statusFilter.set('TODOS');
  }

  hasActiveFilters(): boolean {
    return !!this.searchTerm().trim() || this.statusFilter() !== 'TODOS';
  }

}
