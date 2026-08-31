import { Component, inject, OnInit, signal } from '@angular/core';
import { BaseFormComponent } from '../../../../shared/base/base-form.component';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ProductsService } from '../../../../core/services/products.service';
import { CreateProductRequest } from '../../../../core/models/create-product-request';
import { Router } from '@angular/router';
import { CategoriaResponse } from '../../../../core/models/categoria-response';
import { CategoriaService } from '../../../../core/services/categoria.service';
import { LucideImage } from '@lucide/angular';

@Component({
  selector: 'app-product-form',
  imports: [ReactiveFormsModule, LucideImage],
  templateUrl: './product-form.html',
  styleUrl: './product-form.scss',
})
export class ProductFormComponent extends BaseFormComponent implements OnInit {

  protected override get form(): FormGroup {
    return this.productForm;
  }

  private readonly router = inject(Router);
  private readonly fb = inject(FormBuilder);
  private readonly productService = inject(ProductsService);
  private readonly categoriaService = inject(CategoriaService);
  protected categorias = signal<CategoriaResponse[]>([]);

  protected selectedImage: File | null = null;
  protected imagePreviewUrl = signal<string | null>(null);

  /* FORMULARIO */
  readonly productForm = this.fb.group({

    nombre: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(150)]],
    descripcion: ['', Validators.maxLength(500)],
    precio: [null, [Validators.required, Validators.min(0.01)]],
    categoriaId: [null, Validators.required],
    stock: [0, Validators.min(0)],
    codigo: ['', [Validators.required, Validators.maxLength(50)]]

  });

  ngOnInit(): void {
    this.cargarCategorias();
  }

  private cargarCategorias(): void {
    this.categoriaService.listarCategorias().subscribe ({
      next: (categorias) => {
        this.categorias.set(categorias);
      },
      error: (error) => {
        console.error("Error al cargar las categorías: ", error);
      }
    })
  }

  private buildRequest(): CreateProductRequest {
    return {
      nombre: this.productForm.controls.nombre.value!,
      descripcion: this.productForm.controls.descripcion.value!,
      precio: this.productForm.controls.precio.value!,
      categoriaId: this.productForm.controls.categoriaId.value!,
      stock: this.productForm.controls.stock.value!,
      codigo: this.productForm.controls.codigo.value!
    }
  }

  onSubmit(): void {

    if (this.form.invalid) {
      this.markFormAsTouched();
      return;
    }

    const request = this.buildRequest();

    this.productService.crearProducto(request, this.selectedImage).subscribe({
      next: () => {
        this.router.navigate(['/admin/productos']);
      },
      error: (error) => {
        console.error('Error al crear el producto: ', error);
      }
    })

  }

  protected onImageSelected(event: Event): void {

    const input = event.target as HTMLInputElement;

    if (!input.files || input.files.length === 0) {
      return;
    }

    const file = input.files[0];

    /* VALIDAR FORMATO */
    const allowedTypes = ['image/jpeg','image/png'];

    if (!allowedTypes.includes(file.type)) {
      return;
    }

    /* VALIDAR TAMAÑO */
    const maxSize = 2 * 1024 * 1024;

    console.log('Tamaño MB:', file.size / (1024 * 1024));
    
    if (file.size > maxSize) {
      return;
    }

    /* CREAR URL TEMPORAL */
    const objectUrl = URL.createObjectURL(file);

    /* VALIDAR DIMENSIONES */
    const image = new Image();

    image.onload = () => {

      console.log('Dimensiones:', image.width, 'x', image.height);

      const minWidth = 300;
      const minHeight = 300;
      const maxWidth = 3000;
      const maxHeight = 3000;

      if (
        image.width < minWidth ||
        image.height < minHeight ||
        image.width > maxWidth ||
        image.height > maxHeight
      ) {
        URL.revokeObjectURL(objectUrl);
        return;
      }

      /* IMAGEN VÁLIDA */
      this.selectedImage = file;

      if (this.imagePreviewUrl()) {
        URL.revokeObjectURL(this.imagePreviewUrl()!);
      }

      this.imagePreviewUrl.set(objectUrl);

    };

    image.src = objectUrl;

  }

}
