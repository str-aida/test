import { Component, EventEmitter, inject, OnInit, Output, signal } from '@angular/core';
import { LucideImage, LucidePencil, LucideTrash2, LucideSearch, LucideFilter } from '@lucide/angular';
import { ProductsService } from '../../../../core/services/products.service';
import { ProductResponse } from '../../../../core/models/product-response';
import { environment } from '../../../../../environments/environment';
import { DecimalPipe } from '@angular/common';
import { CategoriaResponse } from '../../../../core/models/categoria-response';
import { CategoriaService } from '../../../../core/services/categoria.service';
import { Estado } from '../../../../core/models/enums/estado.enum';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-products-table',
  imports: [FormsModule, LucideImage, DecimalPipe, LucidePencil, LucideTrash2, LucideSearch, LucideFilter],
  templateUrl: './products-table.html',
  styleUrl: './products-table.scss',
})
export class ProductsTableComponent implements OnInit {

  private readonly productService = inject(ProductsService);
  private readonly categoriaService = inject(CategoriaService);
  protected readonly Estado = Estado;

  @Output() editProduct = new EventEmitter<ProductResponse>();
  @Output() deleteProduct = new EventEmitter<ProductResponse>();

  products = signal<ProductResponse[]>([]);
  texto = '';
  estado: Estado | '' = '';
  categoriaId: number | undefined = undefined;
  categorias = signal<CategoriaResponse[]>([]);

  ngOnInit(): void {
    this.loadProducts();
    this.loadCategories();
  }

  loadProducts(): void {
    this.productService
      .listarProductos(this.categoriaId, this.estado || undefined, this.texto)
      .subscribe({
        next: products => {
          this.products.set(products);
        },
        error: error => {
          console.error('Error al cargar los productos', error);
        }
      });
  }

  loadCategories(): void {
    this.categoriaService
      .listarCategorias()
      .subscribe({
        next: categorias => {
          this.categorias.set(categorias);
        },
        error: error => {
          console.error('Error al cargar los productos', error);
        }
      });
  }

  edit(product: ProductResponse): void {
    this.editProduct.emit(product);
  }

  delete(product: ProductResponse): void {
    this.deleteProduct.emit(product);
  }

  search(): void {
    this.loadProducts();
  }

  hasActiveFilters(): boolean {
    return this.categoriaId !== undefined ||
          !!this.estado ||
          !!this.texto;
  }

  protected getImageUrl (imagenUrl: string | null): string | null {
    if (!imagenUrl) {
      return null;
    }

    return `${environment.baseUrl}${imagenUrl}`;
  }

}
