import { Component, inject, OnInit, signal, ChangeDetectorRef } from '@angular/core';
import { CommonModule, DecimalPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import {
  LucideSearch,
  LucideSlidersHorizontal,
  LucideArrowRight,
  LucideHeart,
  LucideStar,
  LucidePlus,
  LucideShoppingCart,
  LucideCake,
  LucideUtensils,
  LucideGift,
  LucidePartyPopper,
  LucideCoffee,
  LucideTag,
  LucideShapes,
  LucideImage,
  LucideRefreshCw,
  LucideInbox
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
  selector: 'app-inicio-cliente',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterLink,
    DecimalPipe,
    LucideSearch,
    LucideSlidersHorizontal,
    LucideArrowRight,
    LucideHeart,
    LucidePlus,
    LucideShoppingCart,
    LucideCake,
    LucideUtensils,
    LucideGift,
    LucidePartyPopper,
    LucideCoffee,
    LucideTag,
    LucideShapes,
    LucideImage,
    LucideRefreshCw,
    LucideInbox
  ],
  templateUrl: './inicio.component.html',
  styleUrl: './inicio.component.scss'
})
export class InicioClienteComponent implements OnInit {

  private readonly productService = inject(ProductsService);
  private readonly categoriaService = inject(CategoriaService);
  protected readonly cartService = inject(CartService);
  private readonly notificationService = inject(NotificationService);
  private readonly router = inject(Router);
  private readonly cdr = inject(ChangeDetectorRef);

  // Estados
  categorias = signal<CategoriaResponse[]>([]);
  productos = signal<ProductResponse[]>([]);
  destacados = signal<ProductResponse[]>([]);
  isLoading = signal<boolean>(true);
  hasError = signal<boolean>(false);

  textoBusqueda = '';

  ngOnInit(): void {
    this.cargarDatos();
  }

  cargarDatos(): void {
    this.isLoading.set(true);
    this.hasError.set(false);

    // Cargar categorías
    this.categoriaService.listarCategorias().subscribe({
      next: (cats) => {
        const activas = cats.filter(c => c.estado === Estado.ACTIVO);
        this.categorias.set(activas);
      },
      error: (err) => {
        console.error('Error al cargar categorías:', err);
      }
    });

    // Cargar productos activos
    this.productService.listarProductos(undefined, Estado.ACTIVO).subscribe({
      next: (prods) => {
        this.productos.set(prods || []);
        // Tomar primeros 6 como productos destacados
        this.destacados.set((prods || []).slice(0, 6));
        this.isLoading.set(false);
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error al cargar productos:', err);
        this.isLoading.set(false);
        this.hasError.set(true);
        this.cdr.detectChanges();
      }
    });
  }

  onSearch(): void {
    if (this.textoBusqueda.trim()) {
      this.router.navigate(['/cliente/productos'], {
        queryParams: { search: this.textoBusqueda.trim() }
      });
    } else {
      this.router.navigate(['/cliente/productos']);
    }
  }

  onCategoryClick(categoriaId: number): void {
    this.router.navigate(['/cliente/productos'], {
      queryParams: { categoriaId }
    });
  }

  onVerProductos(): void {
    this.router.navigate(['/cliente/productos']);
  }

  agregarAlCarrito(producto: ProductResponse, event?: Event): void {
    if (event) {
      event.stopPropagation();
    }
    const agregado = this.cartService.agregar(producto);
    if (agregado) {
      this.notificationService.success(`"${producto.nombre}" agregado al carrito`);
    } else {
      this.notificationService.error(`No hay más unidades disponibles (Stock: ${producto.stock})`);
    }
  }

  getCategoryProductCount(categoriaId: number): number {
    return this.productos().filter(p => p.categoriaId === categoriaId).length;
  }

  getCategoryIcon(nombre: string): any {
    const name = (nombre || '').toLowerCase();
    if (name.includes('torta') || name.includes('postre') || name.includes('pastel') || name.includes('dulce')) return LucideCake;
    if (name.includes('catering') || name.includes('comida') || name.includes('salado')) return LucideUtensils;
    if (name.includes('box') || name.includes('regalo') || name.includes('caja')) return LucideGift;
    if (name.includes('evento') || name.includes('fiesta') || name.includes('cumple')) return LucidePartyPopper;
    if (name.includes('café') || name.includes('cafeteria') || name.includes('bebida')) return LucideCoffee;
    if (name.includes('promo') || name.includes('oferta') || name.includes('descuento')) return LucideTag;
    return LucideShapes;
  }

  getImageUrl(imagenUrl: string | null): string | null {
    if (!imagenUrl) return null;
    if (imagenUrl.startsWith('http://') || imagenUrl.startsWith('https://')) {
      return imagenUrl;
    }
    return `${environment.baseUrl}${imagenUrl}`;
  }

}
