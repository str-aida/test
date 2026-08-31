import { Component, EventEmitter, inject, OnInit, Output, signal } from '@angular/core';
import { CategoriaService } from '../../../../core/services/categoria.service';
import { CategoriaResponse } from '../../../../core/models/categoria-response';
import { FormsModule } from '@angular/forms';
import { LucidePencil, LucideShapes, LucideTrash2 } from '@lucide/angular';
import { Estado } from '../../../../core/models/enums/estado.enum';

@Component({
  selector: 'app-categories-table',
  imports: [FormsModule, LucidePencil, LucideTrash2, LucideShapes],
  templateUrl: './categories-table.html',
  styleUrl: './categories-table.scss',
})
export class CategoriesTableComponent implements OnInit {

  private readonly categoriaService = inject(CategoriaService);
  protected readonly Estado = Estado;
  categories = signal<CategoriaResponse[]>([]);

  @Output() editCategory = new EventEmitter<CategoriaResponse>();
  @Output() deleteCategory = new EventEmitter<CategoriaResponse>();

  ngOnInit(): void {
    this.loadCategories();
  }
  
  loadCategories(): void {
    this.categoriaService
    .listarCategorias()
    .subscribe({
      next: categories => {
        this.categories.set(categories);
      },
      error: err => {
        console.error('Error al cargar categorias', err);
      }
    });
  }
  
  edit(category: CategoriaResponse): void {

    this.editCategory.emit(category);
  
  }
  
  delete(category: CategoriaResponse): void {
  
    this.deleteCategory.emit(category);
  
  }

}
