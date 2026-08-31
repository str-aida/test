import { Component, inject } from '@angular/core';
import { CommonModule, DecimalPipe } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import {
  LucideShoppingBag,
  LucideTrash2,
  LucidePlus,
  LucideMinus,
  LucideArrowLeft,
  LucideArrowRight,
  LucideStore,
  LucideImage,
  LucideShieldCheck,
  LucideTruck
} from '@lucide/angular';
import { CartItem, CartService } from '../../../../core/services/cart.service';
import { NotificationService } from '../../../../core/services/notification.service';
import { environment } from '../../../../../environments/environment';

@Component({
  selector: 'app-carrito',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    DecimalPipe,
    LucideShoppingBag,
    LucideTrash2,
    LucidePlus,
    LucideMinus,
    LucideArrowLeft,
    LucideArrowRight,
    LucideStore,
    LucideImage,
    LucideShieldCheck,
    LucideTruck
  ],
  templateUrl: './carrito.component.html',
  styleUrl: './carrito.component.scss'
})
export class CarritoComponent {

  protected readonly cartService = inject(CartService);
  private readonly notificationService = inject(NotificationService);
  private readonly router = inject(Router);

  incrementar(item: CartItem): void {
    if (item.producto.stock > 0 && item.cantidad >= item.producto.stock) {
      this.notificationService.error(`Stock máximo disponible alcanzado (${item.producto.stock})`);
      return;
    }
    this.cartService.agregar(item.producto);
  }

  reducir(productoId: number): void {
    this.cartService.reducir(productoId);
  }

  eliminar(item: CartItem): void {
    this.cartService.eliminar(item.producto.id);
    this.notificationService.success(`"${item.producto.nombre}" eliminado del carrito`);
  }

  vaciarCarrito(): void {
    if (confirm('¿Estás seguro de que deseás vaciar todo el carrito?')) {
      this.cartService.vaciar();
      this.notificationService.success('Se vació el carrito');
    }
  }

  continuarAlCheckout(): void {
    if (this.cartService.isEmpty()) {
      this.notificationService.error('Tu carrito está vacío');
      return;
    }
    this.router.navigate(['/cliente/checkout']);
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
