import { ChangeDetectorRef, Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule, DecimalPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import {
  LucideSearch,
  LucideShoppingCart,
  LucidePlus,
  LucideMinus,
  LucideImage,
  LucideX,
  LucideStore
} from '@lucide/angular';
import { ProductsService } from '../../../../core/services/products.service';
import { CategoriaService } from '../../../../core/services/categoria.service';
import { CartService } from '../../../../core/services/cart.service';
import { NotificationService } from '../../../../core/services/notification.service';
import { ProductResponse } from '../../../../core/models/product-response';
import { CategoriaResponse } from '../../../../core/models/categoria-response';
import { Estado } from '../../../../core/models/enums/estado.enum';
import { environment } from '../../../../../environments/environment';

@Component({
  selector: 'app-catalogo',
  imports: [
    CommonModule,
    FormsModule,
    RouterLink,
    DecimalPipe,
    LucideSearch,
    LucideShoppingCart,
    LucidePlus,
    LucideMinus,
    LucideImage,
    LucideX,
    LucideStore
  ],
  templateUrl: './catalogo.component.html',
  styleUrl: './catalogo.component.scss'
})
export class CatalogoComponent implements OnInit {

  private readonly productService = inject(ProductsService);
  private readonly categoriaService = inject(CategoriaService);
  protected readonly cartService = inject(CartService);
  private readonly notificationService = inject(NotificationService);
  private readonly cdr = inject(ChangeDetectorRef);

  // Estados de datos
  products: ProductResponse[] = [];
  categorias = signal<CategoriaResponse[]>([]);
  isLoading = signal<boolean>(true);

  // Filtros
  textoBusqueda = '';
  categoriaSeleccionadaId: number | undefined = undefined;

  ngOnInit(): void {
    this.cargarCategorias();
    this.cargarProductos();
  }

  cargarCategorias(): void {
    this.categoriaService.listarCategorias().subscribe({
      next: (categorias) => {
        // Filtrar únicamente categorías activas para el cliente
        const activas = categorias.filter(c => c.estado === Estado.ACTIVO);
        this.categorias.set(activas);
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error al cargar categorías:', err);
      }
    });
  }

  cargarProductos(): void {
    this.isLoading.set(true);
    // Para el catálogo cliente, solo mostramos productos en estado ACTIVO
    this.productService
      .listarProductos(this.categoriaSeleccionadaId, Estado.ACTIVO, this.textoBusqueda)
      .subscribe({
        next: (products) => {
          this.products = products;
          this.isLoading.set(false);
          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error('Error al cargar productos:', err);
          this.isLoading.set(false);
          this.cdr.detectChanges();
        }
      });
  }

  seleccionarCategoria(categoriaId: number | undefined): void {
    this.categoriaSeleccionadaId = categoriaId;
    this.cargarProductos();
  }

  buscar(): void {
    this.cargarProductos();
  }

  limpiarFiltros(): void {
    this.textoBusqueda = '';
    this.categoriaSeleccionadaId = undefined;
    this.cargarProductos();
  }

  hasActiveFilters(): boolean {
    return this.categoriaSeleccionadaId !== undefined || !!this.textoBusqueda.trim();
  }

  agregarAlCarrito(producto: ProductResponse, event?: Event): void {
    if (event) {
      event.stopPropagation();
    }
    const agregado = this.cartService.agregar(producto);
    if (agregado) {
      this.notificationService.success(`"${producto.nombre}" agregado al carrito`);
    } else {
      this.notificationService.error(`No hay más unidades disponibles de "${producto.nombre}" (Stock: ${producto.stock})`);
    }
  }

  reducirDelCarrito(productoId: number, event?: Event): void {
    if (event) {
      event.stopPropagation();
    }
    this.cartService.reducir(productoId);
  }

  getCantidadEnCarrito(productoId: number): number {
    return this.cartService.getCantidad(productoId);
  }

  getImageUrl(imagenUrl: string | null): string | null {
    if (!imagenUrl) {
      return null;
    }
    if (imagenUrl.startsWith('http://') || imagenUrl.startsWith('https://')) {
      return imagenUrl;
    }
    return `${environment.baseUrl}${imagenUrl}`;
  }
}
