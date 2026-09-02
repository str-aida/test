import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CategoriesListComponent } from './categories-list';

describe('CategoriesListComponent', () => {
  let component: CategoriesListComponent;
  let fixture: ComponentFixture<CategoriesListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CategoriesListComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(CategoriesListComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

