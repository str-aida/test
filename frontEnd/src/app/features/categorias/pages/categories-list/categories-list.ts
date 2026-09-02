import { Component, inject, ViewChild } from '@angular/core';
import { CategoriesTableComponent } from '../../components/categories-table/categories-table';
import { LucideAlertTriangle, LucidePlus, LucideShapes, LucideX, LucideRefreshCw } from '@lucide/angular';
import { CategoryFormComponent } from '../../components/category-form/category-form';
import { CategoriaResponse } from '../../../../core/models/categoria-response';
import { CategoriaService } from '../../../../core/services/categoria.service';
import { UpdateCategoriaRequest } from '../../../../core/models/update-categoria-request';
import { Estado } from '../../../../core/models/enums/estado.enum';
import { NotificationService } from '../../../../core/services/notification.service';

@Component({
  selector: 'app-categories-list',
  imports: [
    CategoriesTableComponent,
    CategoryFormComponent,
    LucideShapes,
    LucidePlus,
    LucideX,
    LucideAlertTriangle,
    LucideRefreshCw
  ],
  templateUrl: './categories-list.html',
  styleUrl: './categories-list.scss',
})
export class CategoriesListComponent {

  private readonly categoriaService = inject(CategoriaService);
  private readonly notificationService = inject(NotificationService);

  @ViewChild(CategoriesTableComponent)
  categoriesTable?: CategoriesTableComponent;

  selectedCategory: CategoriaResponse | null = null;

  showCreateModal = false;
  showDeleteModal = false;
  showEditModal = false;
  isDeactivating = false;

  openCreateModal(): void {
    this.selectedCategory = null;
    this.showCreateModal = true;
  }

  closeCreateModal(): void {
    this.showCreateModal = false;
  }

  onCategoryCreate(): void {
    this.closeCreateModal();
    this.categoriesTable?.loadCategories();
  }

  openEditModal(category: CategoriaResponse): void {
    this.selectedCategory = category;
    this.showEditModal = true;
  }

  closeEditModal(): void {
    this.showEditModal = false;
    this.selectedCategory = null;
  }

  onCategoryUpdated(): void {
    this.closeEditModal();
    this.categoriesTable?.loadCategories();
  }

  openDeleteModal(category: CategoriaResponse): void {
    this.selectedCategory = category;
    this.showDeleteModal = true;
  }

  closeDeleteModal(): void {
    this.showDeleteModal = false;
    this.selectedCategory = null;
    this.isDeactivating = false;
  }

  confirmDelete(): void {
    if (!this.selectedCategory) {
      return;
    }

    const category = this.selectedCategory;
    this.isDeactivating = true;

    const request: UpdateCategoriaRequest = {
      nombre: category.nombre,
      descripcion: category.descripcion || '',
      estado: Estado.INACTIVO,
    };
    
    this.categoriaService
      .editarCategoria(category.id, request)
      .subscribe({
        next: () => {
          this.isDeactivating = false;
          this.notificationService.success(`Categoría "${category.nombre}" desactivada correctamente.`);
          this.closeDeleteModal();
          this.categoriesTable?.loadCategories();
        },
        error: (error) => {
          this.isDeactivating = false;
          console.error('Error al desactivar la categoría', error);
          const msg = error?.error?.message || 'Error al desactivar la categoría.';
          this.notificationService.error(msg);
        }
      });
  }

}
