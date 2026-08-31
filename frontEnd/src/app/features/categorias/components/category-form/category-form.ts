import { Component, EventEmitter, inject, Output } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CategoriaService } from '../../../../core/services/categoria.service';
import { BaseFormComponent } from '../../../../shared/base/base-form.component';
import { CreateCategoriaRequest } from '../../../../core/models/create-categoria-request';

@Component({
  selector: 'app-category-form',
  imports: [ReactiveFormsModule],
  templateUrl: './category-form.html',
  styleUrl: './category-form.scss',
})
export class CategoryFormComponent extends BaseFormComponent {

  private readonly fb = inject(FormBuilder);
  private readonly categoriaService = inject(CategoriaService);

  @Output() categoryCreated = new EventEmitter<void>();
  @Output() cancel = new EventEmitter<void>();

  protected override get form(): FormGroup {
    return this.categoryForm;
  }

  readonly categoryForm = this.fb.group({
    nombre: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(100)]],
    descripcion: ['', Validators.maxLength(200)]
  });

  onCancel(): void {
    this.cancel.emit();
  }

  onSubmit(): void {
    
    if (this.form.invalid) {
      this.markFormAsTouched();
      return;
    }
    
    const category = this.categoryForm.value as CreateCategoriaRequest;
    
    this.categoriaService
      .crearCategoria(category)
      .subscribe({
        next: () => {
          this.categoryCreated.emit();
        },
        error: (error) => {
          console.error('Error al crear la categoría.', error);
        }
      });
    
  }

}
