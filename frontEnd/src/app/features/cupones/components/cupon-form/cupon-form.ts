import { ChangeDetectorRef, Component, EventEmitter, inject, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { BaseFormComponent } from '../../../../shared/base/base-form.component';
import { CuponService } from '../../../../core/services/cupon.service';
import { NotificationService } from '../../../../core/services/notification.service';
import { CuponResponse } from '../../../../core/models/cupon-response';
import { CreateCuponRequest } from '../../../../core/models/create-cupon-request';
import { UpdateCuponRequest } from '../../../../core/models/update-cupon-request';
import { TipoDescuento } from '../../../../core/models/enums/tipo-descuento.enum';
import { TipoAsignacionCupon } from '../../../../core/models/enums/tipo-asignacion-cupon.enum';
import { EstadoCupon } from '../../../../core/models/enums/estado-cupon.enum';

@Component({
  selector: 'app-cupon-form',
  imports: [ReactiveFormsModule],
  templateUrl: './cupon-form.html',
  styleUrl: './cupon-form.scss',
})
export class CuponFormComponent extends BaseFormComponent implements OnInit, OnChanges {
  private readonly fb = inject(FormBuilder);
  private readonly cuponService = inject(CuponService);
  private readonly notificationService = inject(NotificationService);
  private readonly cdr = inject(ChangeDetectorRef);

  @Input() cuponToEdit: CuponResponse | null = null;
  @Output() formSubmitted = new EventEmitter<void>();
  @Output() cancel = new EventEmitter<void>();

  isSubmitting = false;

  readonly tipoDescuentoOptions = [
    { label: 'Porcentaje (%)', value: TipoDescuento.PORCENTAJE },
    { label: 'Monto Fijo ($)', value: TipoDescuento.MONTO }
  ];

  readonly tipoAsignacionOptions = [
    { label: 'Manual', value: TipoAsignacionCupon.MANUAL },
    { label: 'Bienvenida', value: TipoAsignacionCupon.BIENVENIDA },
    { label: 'Cumpleaños', value: TipoAsignacionCupon.CUMPLEANOS },
    { label: 'Cantidad de Compras', value: TipoAsignacionCupon.CANTIDAD_COMPRAS },
    { label: 'Referido', value: TipoAsignacionCupon.REFERIDO }
  ];

  readonly estadoOptions = [
    { label: 'Activo', value: EstadoCupon.ACTIVO },
    { label: 'Inactivo', value: EstadoCupon.INACTIVO }
  ];

  readonly cuponForm: FormGroup = this.fb.group({
    codigo: ['', [Validators.required, Validators.maxLength(50)]],
    tipoDescuento: [TipoDescuento.PORCENTAJE, [Validators.required]],
    valor: [null, [Validators.required, Validators.min(0.01)]],
    fechaInicio: ['', [Validators.required]],
    fechaFin: ['', [Validators.required]],
    usoMaximo: [null, [Validators.min(1)]],
    tipoAsignacion: [TipoAsignacionCupon.MANUAL, [Validators.required]],
    estado: [EstadoCupon.ACTIVO, [Validators.required]]
  });

  protected override get form(): FormGroup {
    return this.cuponForm;
  }

  get isEditing(): boolean {
    return !!this.cuponToEdit;
  }

  ngOnInit(): void {
    this.populateForm();
    this.cdr.detectChanges();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['cuponToEdit']) {
      this.populateForm();
      this.cdr.detectChanges();
    }
  }

  private populateForm(): void {
    if (this.cuponToEdit) {
      this.cuponForm.patchValue({
        codigo: this.cuponToEdit.codigo,
        tipoDescuento: this.cuponToEdit.tipoDescuento,
        valor: this.cuponToEdit.valor,
        fechaInicio: this.formatDateForInput(this.cuponToEdit.fechaInicio),
        fechaFin: this.formatDateForInput(this.cuponToEdit.fechaFin),
        usoMaximo: this.cuponToEdit.usoMaximo,
        estado: this.cuponToEdit.estado
      });
      // De acuerdo a UpdateCuponRequest del backend: codigo y tipoAsignacion NO se pueden editar.
      this.cuponForm.get('codigo')?.disable();
      this.cuponForm.get('tipoAsignacion')?.disable();
    } else {
      this.cuponForm.reset({
        codigo: '',
        tipoDescuento: TipoDescuento.PORCENTAJE,
        valor: null,
        fechaInicio: '',
        fechaFin: '',
        usoMaximo: null,
        tipoAsignacion: TipoAsignacionCupon.MANUAL,
        estado: EstadoCupon.ACTIVO
      });
      this.cuponForm.get('codigo')?.enable();
      this.cuponForm.get('tipoAsignacion')?.enable();
    }
  }

  private formatDateForInput(dateVal: any): string {
    if (!dateVal) return '';
    if (Array.isArray(dateVal)) {
      const [y, m, d] = dateVal;
      return `${y}-${String(m).padStart(2, '0')}-${String(d).padStart(2, '0')}`;
    }
    if (typeof dateVal === 'string') {
      return dateVal.split('T')[0];
    }
    return '';
  }

  private formatDateForBackend(dateVal: any): string {
    if (!dateVal) return '';
    if (typeof dateVal === 'string') {
      return dateVal.trim().split('T')[0];
    }
    return String(dateVal);
  }

  onCancel(): void {
    this.cancel.emit();
  }

  onSubmit(): void {
    if (this.cuponForm.invalid) {
      this.markFormAsTouched();
      return;
    }

    this.isSubmitting = true;
    const formVal = this.cuponForm.getRawValue();

    const usoMaximoVal = (formVal.usoMaximo !== null && formVal.usoMaximo !== '' && !isNaN(Number(formVal.usoMaximo)))
      ? Number(formVal.usoMaximo)
      : null;

    if (this.isEditing && this.cuponToEdit) {
      const updateReq: UpdateCuponRequest = {
        tipoDescuento: formVal.tipoDescuento,
        valor: Number(formVal.valor),
        fechaInicio: this.formatDateForBackend(formVal.fechaInicio),
        fechaFin: this.formatDateForBackend(formVal.fechaFin),
        usoMaximo: usoMaximoVal,
        estado: formVal.estado
      };

      this.cuponService.editarCupon(this.cuponToEdit.id, updateReq).subscribe({
        next: () => {
          this.isSubmitting = false;
          this.notificationService.success('Cupón actualizado correctamente');
          this.formSubmitted.emit();
        },
        error: (err) => {
          this.isSubmitting = false;
          const msg = err?.error?.message || err?.error?.mensaje || 'Error al actualizar el cupón';
          this.notificationService.error(msg);
        }
      });
    } else {
      const createReq: CreateCuponRequest = {
        codigo: formVal.codigo?.trim(),
        tipoDescuento: formVal.tipoDescuento,
        valor: Number(formVal.valor),
        fechaInicio: this.formatDateForBackend(formVal.fechaInicio),
        fechaFin: this.formatDateForBackend(formVal.fechaFin),
        usoMaximo: usoMaximoVal,
        tipoAsignacion: formVal.tipoAsignacion
      };

      this.cuponService.crearCupon(createReq).subscribe({
        next: () => {
          this.isSubmitting = false;
          this.notificationService.success('Cupón creado correctamente');
          this.formSubmitted.emit();
        },
        error: (err) => {
          this.isSubmitting = false;
          const msg = err?.error?.message || err?.error?.mensaje || 'Error al crear el cupón';
          this.notificationService.error(msg);
        }
      });
    }
  }
}
