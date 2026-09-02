import { Component, EventEmitter, inject, Input, Output, OnChanges, SimpleChanges } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CategoriaService } from '../../../../core/services/categoria.service';
import { BaseFormComponent } from '../../../../shared/base/base-form.component';
import { CreateCategoriaRequest } from '../../../../core/models/create-categoria-request';
import { CategoriaResponse } from '../../../../core/models/categoria-response';
import { Estado } from '../../../../core/models/enums/estado.enum';
import { UpdateCategoriaRequest } from '../../../../core/models/update-categoria-request';
import { NotificationService } from '../../../../core/services/notification.service';
import { LucideLoader } from '@lucide/angular';

@Component({
  selector: 'app-category-form',
  imports: [ReactiveFormsModule, LucideLoader],
  templateUrl: './category-form.html',
  styleUrl: './category-form.scss',
})
export class CategoryFormComponent extends BaseFormComponent implements OnChanges {

  private readonly fb = inject(FormBuilder);
  private readonly categoriaService = inject(CategoriaService);
  private readonly notificationService = inject(NotificationService);

  protected readonly Estado = Estado;

  @Input() editingCategory: CategoriaResponse | null = null;
  @Output() categoryCreated = new EventEmitter<void>();
  @Output() categoryUpdated = new EventEmitter<void>();
  @Output() cancel = new EventEmitter<void>();

  isSaving = false;

  protected override get form(): FormGroup {
    return this.categoryForm;
  }

  readonly categoryForm = this.fb.group({
    nombre: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(100)]],
    descripcion: ['', [Validators.maxLength(200)]],
    estado: [Estado.ACTIVO, Validators.required]
  });

  get isEditMode(): boolean {
    return this.editingCategory !== null;
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['editingCategory']) {
      if (this.editingCategory) {
        this.categoryForm.patchValue({
          nombre: this.editingCategory.nombre,
          descripcion: this.editingCategory.descripcion || '',
          estado: this.editingCategory.estado,
        });
      } else {
        this.categoryForm.reset({
          nombre: '',
          descripcion: '',
          estado: Estado.ACTIVO
        });
      }
    }
  }

  onCancel(): void {
    this.cancel.emit();
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.markFormAsTouched();
      return;
    }

    if (this.isSaving) {
      return;
    }

    if (this.isEditMode) {
      this.updateCategory();
      return;
    }

    this.createCategory();
  }

  private createCategory(): void {
    this.isSaving = true;
    const category = this.categoryForm.value as CreateCategoriaRequest;

    this.categoriaService
      .crearCategoria(category)
      .subscribe({
        next: () => {
          this.isSaving = false;
          this.notificationService.success('Categoría creada exitosamente.');
          this.categoryCreated.emit();
        },
        error: (error) => {
          this.isSaving = false;
          console.error('Error al crear la categoría.', error);
          const msg = error?.error?.message || 'Error al crear la categoría.';
          this.notificationService.error(msg);
        }
      });
  }

  private updateCategory(): void {
    if (!this.editingCategory) {
      return;
    }

    this.isSaving = true;
    const category: UpdateCategoriaRequest = {
      nombre: this.categoryForm.value.nombre!,
      descripcion: this.categoryForm.value.descripcion ?? '',
      estado: this.categoryForm.value.estado!
    };

    this.categoriaService
      .editarCategoria(this.editingCategory.id, category)
      .subscribe({
        next: () => {
          this.isSaving = false;
          this.notificationService.success('Categoría actualizada exitosamente.');
          this.categoryUpdated.emit();
        },
        error: (error) => {
          this.isSaving = false;
          console.error('Error al editar la categoría', error);
          const msg = error?.error?.message || 'Error al editar la categoría.';
          this.notificationService.error(msg);
        }
      });
  }

}
