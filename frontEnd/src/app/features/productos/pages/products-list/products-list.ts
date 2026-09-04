import { Component, inject, ViewChild } from '@angular/core';
import { ProductsTableComponent } from '../../components/products-table/products-table';
import { LucideAlertTriangle, LucidePlus, LucideRefreshCw, LucideShapes, LucideX } from '@lucide/angular';
import { ProductFormComponent } from '../../components/product-form/product-form';
import { ProductResponse } from '../../../../core/models/product-response';
import { ProductsService } from '../../../../core/services/products.service';
import { NotificationService } from '../../../../core/services/notification.service';
import { UpdateProductRequest } from '../../../../core/models/update-product-request';
import { Estado } from '../../../../core/models/enums/estado.enum';

@Component({
  selector: 'app-products-list',
  imports: [
    ProductsTableComponent,
    ProductFormComponent,
    LucideShapes,
    LucidePlus,
    LucideX,
    LucideAlertTriangle,
    LucideRefreshCw
  ],
  templateUrl: './products-list.html',
  styleUrl: './products-list.scss',
})
export class ProductsListComponent {

  private readonly productService = inject(ProductsService);
  private readonly notificationService = inject(NotificationService);

  @ViewChild(ProductsTableComponent)
  productsTable?: ProductsTableComponent;

  selectedProduct: ProductResponse | null = null;

  showCreateModal = false;
  showEditModal = false;
  showDeleteModal = false;
  isDeactivating = false;

  openCreateModal(): void {
    this.showCreateModal = true;
  }

  closeCreateModal(): void {
    this.showCreateModal = false;
  }

  onProductCreate(): void {
    this.closeCreateModal();
    this.productsTable?.loadProducts();
  }

  openEditModal(product: ProductResponse): void {
    this.selectedProduct = product;
    this.showEditModal = true;
  }

  closeEditModal(): void {
    this.showEditModal = false;
    this.selectedProduct = null;
  }

  onProductUpdated(): void {
    this.closeEditModal();
    this.productsTable?.loadProducts();
  }

  openDeleteModal(product: ProductResponse): void {
    this.selectedProduct = product;
    this.showDeleteModal = true;
  }

  closeDeleteModal(): void {
    this.showDeleteModal = false;
    this.selectedProduct = null;
    this.isDeactivating = false;
  }

  confirmDelete(): void {
    if (!this.selectedProduct) {
      return;
    }

    const product = this.selectedProduct;
    this.closeDeleteModal();

    const request: UpdateProductRequest = {
      nombre: product.nombre,
      descripcion: product.descripcion,
      precio: product.precio,
      categoriaId: product.categoriaId,
      estado: Estado.INACTIVO,
      stock: product.stock,
      codigo: product.codigo,
      eliminarImagen: false
    };

    this.productService.editarProducto(product.id, request, null).subscribe({
      next: () => {
        this.notificationService.success(`Producto "${product.nombre}" desactivado correctamente.`);
        this.productsTable?.loadProducts();
      },
      error: (error) => {
        console.error('Error al desactivar el producto', error);
        const msg = error?.error?.message || 'Error al desactivar el producto.';
        this.notificationService.error(msg);
      }
    });
  }

}
