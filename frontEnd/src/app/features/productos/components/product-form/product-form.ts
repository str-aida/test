import { Component, inject, OnInit, signal } from '@angular/core';
import { BaseFormComponent } from '../../../../shared/base/base-form.component';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ProductsService } from '../../../../core/services/products.service';
import { CreateProductRequest } from '../../../../core/models/create-product-request';
import { UpdateProductRequest } from '../../../../core/models/update-product-request';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { CategoriaResponse } from '../../../../core/models/categoria-response';
import { CategoriaService } from '../../../../core/services/categoria.service';
import { NotificationService } from '../../../../core/services/notification.service';
import { Estado } from '../../../../core/models/enums/estado.enum';
import { environment } from '../../../../../environments/environment';
import { LucideImage, LucideArrowLeft, LucideTrash2 } from '@lucide/angular';

@Component({
  selector: 'app-product-form',
  imports: [ReactiveFormsModule, RouterLink, LucideImage, LucideArrowLeft, LucideTrash2],
  templateUrl: './product-form.html',
  styleUrl: './product-form.scss',
})
export class ProductFormComponent extends BaseFormComponent implements OnInit {

  protected override get form(): FormGroup {
    return this.productForm;
  }

  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly fb = inject(FormBuilder);
  private readonly productService = inject(ProductsService);
  private readonly categoriaService = inject(CategoriaService);
  private readonly notificationService = inject(NotificationService);

  protected categorias = signal<CategoriaResponse[]>([]);
  protected isEditMode = signal<boolean>(false);
  protected productId: number | null = null;

  protected selectedImage: File | null = null;
  protected imagePreviewUrl = signal<string | null>(null);
  protected eliminarImagen = signal<boolean>(false);
  protected readonly Estado = Estado;

  /* FORMULARIO */
  readonly productForm = this.fb.group({
    nombre: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(150)]],
    descripcion: ['', Validators.maxLength(500)],
    precio: [null as number | null, [Validators.required, Validators.min(0.01)]],
    categoriaId: [null as number | null, Validators.required],
    stock: [0, [Validators.required, Validators.min(0)]],
    codigo: ['', [Validators.required, Validators.maxLength(50)]],
    estado: [Estado.ACTIVO, Validators.required]
  });

  ngOnInit(): void {
    this.cargarCategorias();
    
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.isEditMode.set(true);
      this.productId = Number(idParam);
      this.cargarProducto(this.productId);
    }
  }

  private cargarCategorias(): void {
    this.categoriaService.listarCategorias().subscribe({
      next: (categorias) => {
        this.categorias.set(categorias);
      },
      error: (error) => {
        console.error("Error al cargar las categorías: ", error);
        this.notificationService.error("Error al cargar las categorías.");
      }
    });
  }

  private cargarProducto(id: number): void {
    this.productService.obtenerProductoPorId(id).subscribe({
      next: (producto) => {
        this.productForm.patchValue({
          nombre: producto.nombre,
          descripcion: producto.descripcion || '',
          precio: producto.precio,
          categoriaId: this.findCategoriaId(producto.categoriaNombre),
          stock: producto.stock,
          codigo: producto.codigo,
          estado: producto.estado
        });

        if (producto.imagenUrl) {
          const fullUrl = producto.imagenUrl.startsWith('http://') || producto.imagenUrl.startsWith('https://')
            ? producto.imagenUrl
            : `${environment.baseUrl}${producto.imagenUrl}`;
          this.imagePreviewUrl.set(fullUrl);
        }
      },
      error: (error) => {
        console.error("Error al cargar el producto: ", error);
        this.notificationService.error("No se pudo cargar el producto.");
        this.router.navigate(['/admin/productos']);
      }
    });
  }

  private findCategoriaId(categoriaNombre: string): number | null {
    const found = this.categorias().find(c => c.nombre.toLowerCase() === categoriaNombre.toLowerCase());
    return found ? found.id : null;
  }

  private buildCreateRequest(): CreateProductRequest {
    return {
      nombre: this.productForm.controls.nombre.value!,
      descripcion: this.productForm.controls.descripcion.value!,
      precio: this.productForm.controls.precio.value!,
      categoriaId: this.productForm.controls.categoriaId.value!,
      stock: this.productForm.controls.stock.value!,
      codigo: this.productForm.controls.codigo.value!
    };
  }

  private buildUpdateRequest(): UpdateProductRequest {
    return {
      nombre: this.productForm.controls.nombre.value!,
      descripcion: this.productForm.controls.descripcion.value!,
      precio: this.productForm.controls.precio.value!,
      categoriaId: this.productForm.controls.categoriaId.value!,
      estado: this.productForm.controls.estado.value!,
      stock: this.productForm.controls.stock.value!,
      eliminarImagen: this.eliminarImagen(),
      codigo: this.productForm.controls.codigo.value!
    };
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.markFormAsTouched();
      return;
    }

    if (this.isEditMode() && this.productId) {
      const updateRequest = this.buildUpdateRequest();
      this.productService.editarProducto(this.productId, updateRequest, this.selectedImage).subscribe({
        next: () => {
          this.notificationService.success('Producto actualizado correctamente.');
          this.router.navigate(['/admin/productos']);
        },
        error: (error) => {
          console.error('Error al actualizar el producto: ', error);
          this.notificationService.error('Error al actualizar el producto.');
        }
      });
    } else {
      const createRequest = this.buildCreateRequest();
      this.productService.crearProducto(createRequest, this.selectedImage).subscribe({
        next: () => {
          this.notificationService.success('Producto registrado correctamente.');
          this.router.navigate(['/admin/productos']);
        },
        error: (error) => {
          console.error('Error al crear el producto: ', error);
          this.notificationService.error('Error al crear el producto.');
        }
      });
    }
  }

  onCancel(): void {
    this.router.navigate(['/admin/productos']);
  }

  protected removeImage(): void {
    if (this.imagePreviewUrl() && this.imagePreviewUrl()!.startsWith('blob:')) {
      URL.revokeObjectURL(this.imagePreviewUrl()!);
    }
    this.selectedImage = null;
    this.imagePreviewUrl.set(null);
    this.eliminarImagen.set(true);
  }

  protected onImageSelected(event: Event): void {
    const input = event.target as HTMLInputElement;

    if (!input.files || input.files.length === 0) {
      return;
    }

    const file = input.files[0];
    const allowedTypes = ['image/jpeg', 'image/png'];

    if (!allowedTypes.includes(file.type)) {
      this.notificationService.error('Formato no permitido. Solo JPG y PNG.');
      return;
    }

    const maxSize = 2 * 1024 * 1024;
    if (file.size > maxSize) {
      this.notificationService.error('La imagen no puede superar los 2MB.');
      return;
    }

    const objectUrl = URL.createObjectURL(file);
    const image = new Image();

    image.onload = () => {
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
        this.notificationService.error(`Las dimensiones deben estar entre ${minWidth}x${minHeight}px y ${maxWidth}x${maxHeight}px.`);
        return;
      }

      this.selectedImage = file;
      this.eliminarImagen.set(false);

      if (this.imagePreviewUrl() && this.imagePreviewUrl()!.startsWith('blob:')) {
        URL.revokeObjectURL(this.imagePreviewUrl()!);
      }

      this.imagePreviewUrl.set(objectUrl);
    };

    image.src = objectUrl;
  }

}
