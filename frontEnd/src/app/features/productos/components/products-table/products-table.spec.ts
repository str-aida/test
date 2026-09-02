import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ProductsTableComponent } from './products-table';

describe('ProductsTableComponent', () => {
  let component: ProductsTableComponent;
  let fixture: ComponentFixture<ProductsTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProductsTableComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(ProductsTableComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

