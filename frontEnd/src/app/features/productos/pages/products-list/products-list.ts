import { Component, inject, ViewChild } from '@angular/core';
import { ProductsTableComponent } from '../../components/products-table/products-table';
import { Router, RouterLink } from '@angular/router';
import { LucidePackage, LucidePlus, LucideAlertTriangle } from '@lucide/angular';
import { ProductResponse } from '../../../../core/models/product-response';
import { ProductsService } from '../../../../core/services/products.service';
import { CategoriaService } from '../../../../core/services/categoria.service';
import { NotificationService } from '../../../../core/services/notification.service';
import { Estado } from '../../../../core/models/enums/estado.enum';
import { UpdateProductRequest } from '../../../../core/models/update-product-request';

@Component({
  selector: 'app-products-list',
  imports: [ProductsTableComponent, RouterLink, LucidePackage, LucidePlus, LucideAlertTriangle],
  templateUrl: './products-list.html',
  styleUrl: './products-list.scss',
})
export class ProductsListComponent {

  private readonly productService = inject(ProductsService);
  private readonly categoriaService = inject(CategoriaService);
  private readonly notificationService = inject(NotificationService);
  private readonly router = inject(Router);

  @ViewChild(ProductsTableComponent) productsTable!: ProductsTableComponent;

  showStatusModal = false;
  selectedProduct: ProductResponse | null = null;
  protected readonly Estado = Estado;

  openStatusModal(product: ProductResponse): void {
    this.selectedProduct = product;
    this.showStatusModal = true;
  }

  closeStatusModal(): void {
    this.showStatusModal = false;
    this.selectedProduct = null;
  }

  confirmToggleStatus(): void {
    if (!this.selectedProduct) return;

    const targetProduct = this.selectedProduct;
    const nuevoEstado = targetProduct.estado === Estado.ACTIVO ? Estado.INACTIVO : Estado.ACTIVO;

    this.productService.obtenerProductoPorId(targetProduct.id).subscribe({
      next: (fullProduct) => {
        this.categoriaService.listarCategorias().subscribe({
          next: (categorias) => {
            const cat = categorias.find(c => c.nombre.toLowerCase() === fullProduct.categoriaNombre.toLowerCase());
            const catId = cat ? cat.id : 0;

            const updateRequest: UpdateProductRequest = {
              nombre: fullProduct.nombre,
              descripcion: fullProduct.descripcion,
              precio: fullProduct.precio,
              categoriaId: catId,
              estado: nuevoEstado,
              stock: fullProduct.stock,
              codigo: fullProduct.codigo
            };

            this.productService.editarProducto(targetProduct.id, updateRequest).subscribe({
              next: () => {
                const estadoTxt = nuevoEstado === Estado.ACTIVO ? 'activado' : 'desactivado';
                this.notificationService.success(`Producto "${fullProduct.nombre}" ${estadoTxt} correctamente.`);
                this.closeStatusModal();
                if (this.productsTable) {
                  this.productsTable.loadProducts();
                }
              },
              error: (err) => {
                console.error('Error al cambiar estado del producto:', err);
                this.notificationService.error('No se pudo cambiar el estado del producto.');
                this.closeStatusModal();
              }
            });
          },
          error: () => {
            this.notificationService.error('Error al obtener la categoría del producto.');
            this.closeStatusModal();
          }
        });
      },
      error: () => {
        this.notificationService.error('Error al consultar el producto.');
        this.closeStatusModal();
      }
    });
  }

  onEditProduct(product: ProductResponse): void {
    this.router.navigate(['/admin/productos/editar', product.id]);
  }

}
