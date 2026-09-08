import { ChangeDetectorRef, Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule, DecimalPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import {
  LucideSearch,
  LucideShoppingCart,
  LucidePlus,
  LucideMinus,
  LucideImage,
  LucidePercent,
  LucideRefreshCw,
  LucideInbox,
  LucideHeart,
  LucideX
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
  selector: 'app-descuentos-cliente',
  standalone: true,
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
    LucidePercent,
    LucideRefreshCw,
    LucideInbox,
    LucideHeart,
    LucideX
  ],
  templateUrl: './descuentos.component.html',
  styleUrl: './descuentos.component.scss'
})
export class DescuentosClienteComponent implements OnInit {

  private readonly productService = inject(ProductsService);
  private readonly categoriaService = inject(CategoriaService);
  protected readonly cartService = inject(CartService);
  private readonly notificationService = inject(NotificationService);
  private readonly router = inject(Router);
  private readonly cdr = inject(ChangeDetectorRef);

  // Estados de datos
  productosConDescuento = signal<ProductResponse[]>([]);
  categorias = signal<CategoriaResponse[]>([]);
  isLoading = signal<boolean>(true);
  hasError = signal<boolean>(false);

  // Filtros
  textoBusqueda = '';
  categoriaSeleccionadaId: number | undefined = undefined;

  ngOnInit(): void {
    this.cargarCategorias();
    this.cargarProductosConDescuento();
  }

  cargarCategorias(): void {
    this.categoriaService.listarCategorias().subscribe({
      next: (cats) => {
        const activas = cats.filter(c => c.estado === Estado.ACTIVO);
        this.categorias.set(activas);
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error al cargar categorías:', err);
      }
    });
  }

  cargarProductosConDescuento(): void {
    this.isLoading.set(true);
    this.hasError.set(false);

    // Fuente única: GET /productos con estado ACTIVO
    this.productService
      .listarProductos(this.categoriaSeleccionadaId, Estado.ACTIVO, this.textoBusqueda)
      .subscribe({
        next: (prods) => {
          // Filtrado de presentación exclusivamente basado en datos del backend:
          // producto.descuentoPorcentaje != null && producto.descuentoPorcentaje > 0
          const conDescuento = (prods || []).filter(
            p => p.descuentoPorcentaje !== undefined && p.descuentoPorcentaje !== null && p.descuentoPorcentaje > 0
          );
          this.productosConDescuento.set(conDescuento);
          this.isLoading.set(false);
          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error('Error al cargar productos con descuento:', err);
          this.isLoading.set(false);
          this.hasError.set(true);
          this.cdr.detectChanges();
        }
      });
  }

  seleccionarCategoria(categoriaId: number | undefined): void {
    this.categoriaSeleccionadaId = categoriaId;
    this.cargarProductosConDescuento();
  }

  buscar(): void {
    this.cargarProductosConDescuento();
  }

  limpiarFiltros(): void {
    this.textoBusqueda = '';
    this.categoriaSeleccionadaId = undefined;
    this.cargarProductosConDescuento();
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
    if (!imagenUrl) return null;
    if (imagenUrl.startsWith('http://') || imagenUrl.startsWith('https://')) {
      return imagenUrl;
    }
    return `${environment.baseUrl}${imagenUrl}`;
  }
}
