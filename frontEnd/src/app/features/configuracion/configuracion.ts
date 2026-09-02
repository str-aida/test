import { Component, inject, OnInit } from '@angular/core';
import { EstablecimientoResponse } from '../../core/models/establecimiento-response';
import { EstablecimientoService } from '../../core/services/establecimiento.service';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { BaseFormComponent } from '../../shared/base/base-form.component';
import { DiaSemana } from '../../core/models/enums/dia-semana.enum';
import { TipoServicio } from '../../core/models/enums/tipo-servicio.enum';
import { DatePipe } from '@angular/common';
import { scheduleValidator } from '../../shared/validators/schedule.validator';
import { LucideBike, LucideShoppingBag, LucideStore } from '@lucide/angular';
import { UpdateEstablecimientoRequest } from '../../core/models/update-establecimiento-request';
import { UpdateDireccionRequest } from '../../core/models/update-direccion-request';
import { PhoneMaskDirective } from '../../shared/directives/phone-mask.directive';

@Component({
  selector: 'app-configuracion',
  imports: [ReactiveFormsModule, DatePipe, LucideBike, LucideShoppingBag, LucideStore, PhoneMaskDirective],
  templateUrl: './configuracion.html',
  styleUrl: './configuracion.scss',
})
export class ConfiguracionComponent extends BaseFormComponent implements OnInit {

  protected override get form(): FormGroup {
    return this.establishmentForm;
  }
  private readonly fb = inject(FormBuilder);
  private readonly establecimientoService = inject(EstablecimientoService);
  protected establecimiento: EstablecimientoResponse | null = null;
  readonly diasSemana = Object.values(DiaSemana);
  readonly diasSemanaLabel: Record<DiaSemana, string> = {
    [DiaSemana.LUNES]: 'Lunes',
    [DiaSemana.MARTES]: 'Martes',
    [DiaSemana.MIERCOLES]: 'Miércoles',
    [DiaSemana.JUEVES]: 'Jueves',
    [DiaSemana.VIERNES]: 'Viernes',
    [DiaSemana.SABADO]: 'Sábado',
    [DiaSemana.DOMINGO]: 'Domingo'
  };
  protected isEditing = false;

  readonly establishmentForm = this.fb.group({

    nombre: ['', [Validators.required, Validators.maxLength(100)]],
    razonSocial: ['', [Validators.required, Validators.maxLength(150)]],
    direccion: this.fb.group({
      nombre: ['', Validators.maxLength(50)],
      calle: ['', [Validators.required, Validators.maxLength(100)]],
      numero: ['', [Validators.required, Validators.maxLength(20)]],
      localidad: ['', [Validators.required, Validators.maxLength(100)]],
      piso: ['', Validators.maxLength(20)],
      departamento: ['', Validators.maxLength(20)],
      codigoPostal: ['', Validators.maxLength(10)],
      referencia: ['', Validators.maxLength(200)],
      esPrincipal: [true]
    }),
    telefono: ['', [Validators.required, Validators.maxLength(20)]],
    email: ['', [Validators.required, Validators.email, Validators.pattern(/^[^\s@]+@[^\s@]+\.[^\s@]+$/), Validators.maxLength(100)]],
    horarioApertura: ['', Validators.required],
    horarioCierre: ['', Validators.required],
    diasHabiles: [[] as DiaSemana[], Validators.required],
    descripcion: ['', Validators.maxLength(500)],
    tipoServicio: [null as TipoServicio | null, Validators.required]

  },
  {
    validators: scheduleValidator
  });

  /* CARGAR DATOS EN EL FORMULARIO */
  private fillForm(establishment: EstablecimientoResponse): void {

    this.establecimiento = establishment;
    this.establishmentForm.patchValue({
      nombre: establishment.nombre,
      razonSocial: establishment.razonSocial,
      email: establishment.email,
      telefono: establishment.telefono,
      direccion: {
        nombre: establishment.direccion.nombre ?? '',
        calle: establishment.direccion.calle,
        numero: establishment.direccion.numero,
        localidad: establishment.direccion.localidad,
        piso: establishment.direccion.piso ?? '',
        departamento: establishment.direccion.departamento ?? '',
        codigoPostal: establishment.direccion.codigoPostal ?? '',
        referencia: establishment.direccion.referencia ?? '',
        esPrincipal: true
      },
      horarioApertura: establishment.horarioApertura,
      horarioCierre: establishment.horarioCierre,
      diasHabiles: establishment.diasHabiles,
      descripcion: establishment.descripcion,
      tipoServicio: establishment.tipoServicio
    });
    this.establishmentForm.disable();
    this.isEditing = false;

  }

  /* CONSTRUIR REQUEST PARA ACTUALIZAR EL PERFIL */
  private buildRequest(): UpdateEstablecimientoRequest {
    return {
      nombre: this.establishmentForm.controls.nombre.value!,
      razonSocial: this.establishmentForm.controls.razonSocial.value!,
      email: this.establishmentForm.controls.email.value!,
      telefono: this.establishmentForm.controls.telefono.value!,
      direccion: {
        ...(this.establishmentForm.controls.direccion.getRawValue() as UpdateDireccionRequest),
        esPrincipal: true
      },
      horarioApertura: this.establishmentForm.controls.horarioApertura.value!,
      horarioCierre: this.establishmentForm.controls.horarioCierre.value!,
      diasHabiles: this.establishmentForm.controls.diasHabiles.value!,
      descripcion: this.establishmentForm.controls.descripcion.value ?? '',
      tipoServicio: this.establishmentForm.controls.tipoServicio.value!
    };
  }

  /* ACTUALIZAR ESTABLECIMIENTO */
  onSubmit(): void {
    if (this.form.invalid) {
      this.markFormAsTouched();
      return;
    }
    this.establecimientoService.actualizarEstablecimiento(this.buildRequest()).subscribe({
      next: (establishment) => {
        this.fillForm(establishment);
      },
      error: (error) => {
        console.error('Error al actualizar el establecimiento.', error);
      }
    });
  }

  /* INICIALIZAR COMPONENTE */
  ngOnInit(): void {
    this.loadEstablishment();
  }

  /* OBTENER PERFIL DEL USUARIO */
  private loadEstablishment(): void {
    this.establecimientoService.obtenerEstablecimiento().subscribe({
      next: (establishment: EstablecimientoResponse) => {
        this.fillForm(establishment);
      },
      error: (error) => {
        console.error('Error al obtener el establecimiento', error);
      }
    });
  }

  /* MÉTODO PARA ENTRAR EN MODO DE EDICIÓN */
  editEstablishment(): void {
    this.isEditing = true;
    this.establishmentForm.enable();
  }

  /* MÉTODO PARA CANCELAR LA EDICIÓN */
  cancelEdit(): void {
    if (!this.establecimiento) {
      return;
    }
    this.fillForm(this.establecimiento);
  }

  onDiaChange(event: Event, dia: DiaSemana): void {
    const checkbox = event.target as HTMLInputElement;
    const control = this.establishmentForm.get('diasHabiles');
    if (!control) return;
    const dias = control.value as DiaSemana[];

    if (checkbox.checked) {
      control.setValue([...dias, dia]);
    } else {
      control.setValue(
        dias.filter(d => d !== dia)
      );
    }

    control.markAsDirty();
    control.markAsTouched();
    control.updateValueAndValidity();
  }
}