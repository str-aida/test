import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProductsTable } from './products-table';

describe('ProductsTable', () => {
  let component: ProductsTable;
  let fixture: ComponentFixture<ProductsTable>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProductsTable],
    }).compileComponents();

    fixture = TestBed.createComponent(ProductsTable);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
