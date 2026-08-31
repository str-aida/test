import { Component } from '@angular/core';
import { ProductsTableComponent } from '../../components/products-table/products-table';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-products-list',
  imports: [ProductsTableComponent, RouterLink],
  templateUrl: './products-list.html',
  styleUrl: './products-list.scss',
})
export class ProductsListComponent {}
