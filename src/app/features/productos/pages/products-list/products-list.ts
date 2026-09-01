import { Component, ViewChild } from '@angular/core';
import { ProductsTableComponent } from '../../components/products-table/products-table';
import { LucidePlus, LucideShapes, LucideX } from '@lucide/angular';
import { ProductFormComponent } from '../../components/product-form/product-form';
import { ProductResponse } from '../../../../core/models/product-response';

@Component({
  selector: 'app-products-list',
  imports: [ProductsTableComponent, ProductFormComponent, LucideShapes, LucidePlus, LucideX],
  templateUrl: './products-list.html',
  styleUrl: './products-list.scss',
})
export class ProductsListComponent {

  @ViewChild(ProductsTableComponent)
  productsTable?: ProductsTableComponent;

  selectedProduct : ProductResponse | null = null;
  showCreateModal = false;
  showEditModal = false;

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

}
