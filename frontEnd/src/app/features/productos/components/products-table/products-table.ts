import { ChangeDetectorRef, Component, inject, OnInit, signal } from '@angular/core';
import { LucideImage } from '@lucide/angular';
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
  imports: [FormsModule, LucideImage, DecimalPipe],
  templateUrl: './products-table.html',
  styleUrl: './products-table.scss',
})
export class ProductsTableComponent implements OnInit {

  private readonly productService = inject(ProductsService);
  private readonly categoriaService = inject(CategoriaService);
  private readonly cdr = inject(ChangeDetectorRef);

  products : ProductResponse[] = [];
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
          this.products = products;

          this.cdr.detectChanges();
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

          this.cdr.detectChanges();
        },
        error: error => {
          console.error('Error al cargar los productos', error);
        }
      });
  }

  search(): void {
    this.loadProducts();
  }

  hasActiveFilters(): boolean {
    return this.categoriaId !== undefined ||
          !!this.estado ||
          !!this.texto
  }

  protected getImageUrl (imagenUrl: string | null): string | null {
    if (!imagenUrl) {
      return null;
    }

    return `${environment.baseUrl}${imagenUrl}`;
  }

}
