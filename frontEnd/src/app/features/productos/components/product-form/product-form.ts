import { Component, EventEmitter, inject, Input, OnChanges, OnInit, Output, signal } from '@angular/core';
import { BaseFormComponent } from '../../../../shared/base/base-form.component';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ProductsService } from '../../../../core/services/products.service';
import { CreateProductRequest } from '../../../../core/models/create-product-request';
import { CategoriaResponse } from '../../../../core/models/categoria-response';
import { CategoriaService } from '../../../../core/services/categoria.service';
import { LucideImage, LucidePlus, LucideTrash2, } from '@lucide/angular';
import { ProductResponse } from '../../../../core/models/product-response';
import { UpdateProductRequest } from '../../../../core/models/update-product-request';
import { Estado } from '../../../../core/models/enums/estado.enum';
import { environment } from '../../../../../environments/environment';

@Component({
  selector: 'app-product-form',
  imports: [ReactiveFormsModule, LucideImage, LucideTrash2, LucidePlus],
  templateUrl: './product-form.html',
  styleUrl: './product-form.scss',
})
export class ProductFormComponent extends BaseFormComponent implements OnInit, OnChanges {

  protected override get form(): FormGroup {
    return this.productForm;
  }

  private readonly fb = inject(FormBuilder);
  private readonly productService = inject(ProductsService);
  private readonly categoriaService = inject(CategoriaService);
  protected readonly Estado = Estado;
  protected categorias = signal<CategoriaResponse[]>([]);

  protected selectedImage: File | null = null;
  protected imagePreviewUrl = signal<string | null>(null);
  protected removeImage = signal(false);

  @Input() editingProduct: ProductResponse | null = null;
  @Output() productCreated = new EventEmitter<void>();
  @Output() productUpdated = new EventEmitter<void>();
  @Output() cancel = new EventEmitter<void>();

  /* FORMULARIO */
  readonly productForm = this.fb.group({

    nombre: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(150)]],
    descripcion: ['', Validators.maxLength(500)],
    precio: this.fb.control<number | null>(null, [Validators.required, Validators.min(0.01)]),
    categoriaId: this.fb.control<number | null>(null, Validators.required),
    estado: [Estado.ACTIVO, Validators.required],
    stock: [0, Validators.min(0)],
    codigo: ['', [Validators.required, Validators.maxLength(50)]]

  });

  get isEditMode(): boolean {
    return this.editingProduct !== null;
  }

  ngOnChanges(): void {
    if (!this.editingProduct) {
      return;
    }

    this.productForm.patchValue({
      nombre: this.editingProduct.nombre,
      descripcion: this.editingProduct.descripcion,
      precio: this.editingProduct.precio,
      categoriaId: this.editingProduct.categoriaId,
      estado: this.editingProduct.estado,
      stock: this.editingProduct.stock,
      codigo: this.editingProduct.codigo,
    });

    //Al abrir la edición comenzamos conservando la imagen actual
    this.selectedImage = null;
    this.removeImage.set(false);

    if (this.editingProduct.imagenUrl) {
      this.imagePreviewUrl.set(
        this.getImageUrl(this.editingProduct.imagenUrl)
      );
    } else {
      this.imagePreviewUrl.set(null);
    }
  }

  private getImageUrl(imagenUrl: string): string {
    return `${environment.baseUrl}${imagenUrl}`;
  }

  onCancel(): void {
    this.cancel.emit();
  }

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

  onSubmit(): void {

    if (this.form.invalid) {
      this.markFormAsTouched();
      return;
    }

    if (this.isEditMode) {
      this.updateProduct();
      return;
    }

    this.createProduct();

  }

  private createProduct(): void {

    const request = this.buildRequest();

    this.productService.crearProducto(request, this.selectedImage).subscribe({
      next: () => {
        this.productCreated.emit();
      },
      error: (error) => {
        console.error('Error al crear el producto: ', error);
      }
    });

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

  private updateProduct(): void {

    if (!this.editingProduct) {
      return;
    }

    const request = this.buildUpdateRequest();

    this.productService.editarProducto(this.editingProduct.id, request, this.selectedImage).subscribe({
      next: () => {
        this.productUpdated.emit();
      },
      error: (error) => {
        console.error('Error al editar el producto: ', error);
      }
    });

  }

  private buildUpdateRequest(): UpdateProductRequest {
    return {
      nombre: this.productForm.controls.nombre.value!,
      descripcion: this.productForm.controls.descripcion.value!,
      precio: this.productForm.controls.precio.value!,
      categoriaId: this.productForm.controls.categoriaId.value!,
      estado: this.productForm.controls.estado.value!,
      stock: this.productForm.controls.stock.value!,
      codigo: this.productForm.controls.codigo.value!,
      eliminarImagen: this.removeImage()
    }
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
      this.removeImage.set(false);

      if (this.imagePreviewUrl()) {
        URL.revokeObjectURL(this.imagePreviewUrl()!);
      }

      this.imagePreviewUrl.set(objectUrl);

    };

    image.src = objectUrl;

  }

  protected onRemoveImage(): void {
    this.selectedImage = null;
    this.removeImage.set(true);
    this.imagePreviewUrl.set(null);
  }

}
