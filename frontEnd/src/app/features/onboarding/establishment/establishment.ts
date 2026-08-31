import { Component, inject } from '@angular/core';
import { EventEmitter, Output } from '@angular/core';
import { LucideBike, LucideShoppingBag, LucideStore, LucideArrowRight } from '@lucide/angular';
import { FormBuilder, ReactiveFormsModule, Validators, FormGroup } from '@angular/forms';
import { TipoServicio } from '../../../core/models/enums/tipo-servicio.enum';
import { EstablishmentService } from '../../../core/services/establishment.service';
import { EstablishmentSetup } from '../../../core/models/establishment-setup';
import { CuitMaskDirective } from '../../../shared/directives/cuit-mask.directive';
import { PhoneMaskDirective } from '../../../shared/directives/phone-mask.directive';
import { BaseFormComponent } from '../../../shared/base/base-form.component';
import { DiaSemana } from '../../../core/models/enums/dia-semana.enum';
import { scheduleValidator } from '../../../shared/validators/schedule.validator';

@Component({
  selector: 'app-establishment',
  imports: [
    ReactiveFormsModule,
    LucideBike,
    LucideShoppingBag,
    LucideStore,
    LucideArrowRight,
    CuitMaskDirective,
    PhoneMaskDirective],
  templateUrl: './establishment.html',
  styleUrl: './establishment.scss',
})
export class EstablishmentComponent extends BaseFormComponent {

  @Output()
  completed = new EventEmitter<void>();


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

  protected override get form(): FormGroup {
    return this.establishmentForm;
  }

  /* INYECCIÓN DE DEPENDENCIAS */
  private readonly fb = inject(FormBuilder);

  private readonly establishmentService = inject(EstablishmentService);

  /* FORMULARIO */
  readonly establishmentForm = this.fb.group({

    nombre: ['', [Validators.required, Validators.maxLength(100)]],
    razonSocial: ['', [Validators.required, Validators.maxLength(150)]],
    cuit: ['', [Validators.required, Validators.pattern(/^\d{2}-\d{8}-\d$/)]],
    direccion: this.fb.group({
      nombre: ['', Validators.maxLength(50)],
      calle: ['', [Validators.required, Validators.maxLength(100)]],
      numero: ['', [Validators.required, Validators.maxLength(20)]],
      localidad: ['', [Validators.required, Validators.maxLength(100)]],
      piso: ['', Validators.maxLength(20)],
      departamento: ['', Validators.maxLength(20)],
      codigoPostal: ['', Validators.maxLength(10)],
      referencia: ['', Validators.maxLength(200)],
    }),
    telefono: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(20)]],
    email: ['', [Validators.required, Validators.email, Validators.pattern(/^[^\s@]+@[^\s@]+\.[^\s@]+$/), Validators.maxLength(150)]],
    horarioApertura: ['', Validators.required],
    horarioCierre: ['', Validators.required],
    diasHabiles: [[] as DiaSemana[], Validators.required],
    descripcion: ['', Validators.maxLength(500)],
    tipoServicio: [TipoServicio.AMBOS, Validators.required]

  },
  {
    validators: scheduleValidator
  });

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


  readonly header = {

    eyebrow: 'Configuración',
    title: 'Tu establecimiento',
    subtitle: 'Datos básicos para tu tienda online y pedidos.'

  };


  onSubmit(): void {

    if (this.form.invalid) {

      this.markFormAsTouched();

      return;

    }

    const establishment = this.establishmentForm.value as EstablishmentSetup;

    this.establishmentService
      .crearEstablecimiento(establishment)
      .subscribe({

        next: (response) => {

          console.log('Establecimiento creado correctamente.');
          console.log(response);
          
          // Avisa al componente padre que puede continuar
          this.completed.emit();

        },

        error: (error) => {

          console.error('Error al crear el establecimiento.');
          console.error(error);

        }

      });

  }

}
