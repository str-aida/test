import { Component, inject, ViewChild } from '@angular/core';
import { CategoriesTableComponent } from '../../components/categories-table/categories-table';
import { LucideAlertTriangle, LucidePlus, LucideShapes, LucideX } from '@lucide/angular';
import { CategoryFormComponent } from '../../components/category-form/category-form';
import { CategoriaResponse } from '../../../../core/models/categoria-response';
import { CategoriaService } from '../../../../core/services/categoria.service';
import { UpdateCategoriaRequest } from '../../../../core/models/update-categoria-request';
import { Estado } from '../../../../core/models/enums/estado.enum';

@Component({
  selector: 'app-categories-list',
  imports: [CategoriesTableComponent, CategoryFormComponent, LucideShapes, LucidePlus, LucideX, LucideAlertTriangle],
  templateUrl: './categories-list.html',
  styleUrl: './categories-list.scss',
})
export class CategoriesListComponent {

  private readonly categoriaService = inject(CategoriaService);

  @ViewChild(CategoriesTableComponent)
  categoriesTable?: CategoriesTableComponent;

  selectedCategory : CategoriaResponse | null = null;

  showCreateModal = false;
  showDeleteModal = false;

  openCreateModal(): void {
    this.showCreateModal = true;
  }

  closeCreateModal(): void {
    this.showCreateModal = false;
  }

  onCategoryCreate(): void {
    this.closeCreateModal();
    this.categoriesTable?.loadCategories();
  }

  openEditModal(category: unknown): void {
    console.log('Abrir modal de editar categoría', category);
  }

  openDeleteModal(category: CategoriaResponse): void {
    this.selectedCategory = category;
    this.showDeleteModal = true;
  }

  closeDeleteModal(): void {
    this.showDeleteModal = false;
    this.selectedCategory = null;
  }

  confirmDelete(): void {
    if (!this.selectedCategory) {
      return;
    }

    const category = this.selectedCategory;

    //Cerramos el modal inmediatamente
    this.showDeleteModal = false;
    this.selectedCategory = null;

    const request: UpdateCategoriaRequest = {
      nombre: category.nombre,
      descripcion: category.descripcion,
      estado: Estado.INACTIVO,
    };
    
    this.categoriaService
    .editarCategoria(category.id, request)
    .subscribe({
      next: () => {

        this.categoriesTable?.loadCategories();

      },
      error: (error) => {

        console.error('Error al desactivar la categoría', error);

      }
    });
  }

}
