import { ChangeDetectorRef, Component, EventEmitter, inject, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { BaseFormComponent } from '../../../../shared/base/base-form.component';
import { CuponService } from '../../../../core/services/cupon.service';
import { NotificationService } from '../../../../core/services/notification.service';
import { ReglaCuponResponse } from '../../../../core/models/regla-cupon-response';
import { UpdateReglaCuponRequest } from '../../../../core/models/update-regla-cupon-request';
import { TipoDescuento } from '../../../../core/models/enums/tipo-descuento.enum';
import { TipoAsignacionCupon } from '../../../../core/models/enums/tipo-asignacion-cupon.enum';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-regla-cupon-form',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './regla-cupon-form.html',
  styleUrl: './regla-cupon-form.scss',
})
export class ReglaCuponFormComponent extends BaseFormComponent implements OnInit, OnChanges {
  private readonly fb = inject(FormBuilder);
  private readonly cuponService = inject(CuponService);
  private readonly notificationService = inject(NotificationService);
  private readonly cdr = inject(ChangeDetectorRef);

  @Input() reglaToEdit: ReglaCuponResponse | null = null;
  @Output() formSubmitted = new EventEmitter<void>();
  @Output() cancel = new EventEmitter<void>();

  isSubmitting = false;

  readonly tipoDescuentoOptions = [
    { label: 'Porcentaje (%)', value: TipoDescuento.PORCENTAJE },
    { label: 'Monto Fijo ($)', value: TipoDescuento.MONTO }
  ];

  readonly reglaForm: FormGroup = this.fb.group({
    tipoDescuento: [TipoDescuento.PORCENTAJE, [Validators.required]],
    valor: [null, [Validators.required, Validators.min(0.01)]],
    diasValidez: [30, [Validators.required, Validators.min(1)]],
    cantidadComprasRequeridas: [null],
    activo: [true, [Validators.required]],
    descripcion: ['']
  });

  protected override get form(): FormGroup {
    return this.reglaForm;
  }

  get esReglaCantidadCompras(): boolean {
    return this.reglaToEdit?.tipoAsignacion === TipoAsignacionCupon.CANTIDAD_COMPRAS;
  }

  get tituloEstrategia(): string {
    if (!this.reglaToEdit) return 'Regla de Asignación Automática';
    switch (this.reglaToEdit.tipoAsignacion) {
      case TipoAsignacionCupon.CUMPLEANOS:
        return 'Regla: Cupón de Cumpleaños';
      case TipoAsignacionCupon.CANTIDAD_COMPRAS:
        return 'Regla: Fidelidad por Cantidad de Compras';
      case TipoAsignacionCupon.BIENVENIDA:
        return 'Regla: Cupón de Bienvenida';
      case TipoAsignacionCupon.REFERIDO:
        return 'Regla: Cupón por Referido';
      default:
        return `Regla: ${this.reglaToEdit.tipoAsignacion}`;
    }
  }

  ngOnInit(): void {
    this.populateForm();
    this.cdr.detectChanges();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['reglaToEdit']) {
      this.populateForm();
      this.cdr.detectChanges();
    }
  }

  private populateForm(): void {
    if (this.reglaToEdit) {
      this.reglaForm.patchValue({
        tipoDescuento: this.reglaToEdit.tipoDescuento,
        valor: this.reglaToEdit.valor,
        diasValidez: this.reglaToEdit.diasValidez,
        cantidadComprasRequeridas: this.reglaToEdit.cantidadComprasRequeridas,
        activo: this.reglaToEdit.activo,
        descripcion: this.reglaToEdit.descripcion || ''
      });

      if (this.esReglaCantidadCompras) {
        this.reglaForm.get('cantidadComprasRequeridas')?.setValidators([Validators.required, Validators.min(1)]);
      } else {
        this.reglaForm.get('cantidadComprasRequeridas')?.clearValidators();
      }
      this.reglaForm.get('cantidadComprasRequeridas')?.updateValueAndValidity();
    }
  }

  onCancel(): void {
    this.cancel.emit();
  }

  onSubmit(): void {
    if (this.reglaForm.invalid || !this.reglaToEdit) {
      this.markFormAsTouched();
      return;
    }

    this.isSubmitting = true;
    const formVal = this.reglaForm.getRawValue();

    const comprasReq = this.esReglaCantidadCompras
      ? (formVal.cantidadComprasRequeridas ? Number(formVal.cantidadComprasRequeridas) : null)
      : null;

    const updateReq: UpdateReglaCuponRequest = {
      activo: Boolean(formVal.activo),
      tipoDescuento: formVal.tipoDescuento,
      valor: Number(formVal.valor),
      diasValidez: Number(formVal.diasValidez),
      cantidadComprasRequeridas: comprasReq,
      descripcion: formVal.descripcion?.trim() || null
    };

    this.cuponService.actualizarRegla(this.reglaToEdit.tipoAsignacion, updateReq).subscribe({
      next: () => {
        this.isSubmitting = false;
        this.notificationService.success('Regla de estrategia actualizada correctamente');
        this.formSubmitted.emit();
      },
      error: (err) => {
        this.isSubmitting = false;
        const msg = err?.error?.message || err?.error?.mensaje || 'Error al actualizar la regla';
        this.notificationService.error(msg);
      }
    });
  }
}
