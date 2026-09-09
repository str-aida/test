import { Component, inject, OnInit, signal } from '@angular/core';
import { EstablecimientoResponse } from '../../core/models/establecimiento-response';
import { EstablecimientoService } from '../../core/services/establecimiento.service';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { BaseFormComponent } from '../../shared/base/base-form.component';
import { DiaSemana } from '../../core/models/enums/dia-semana.enum';
import { TipoServicio } from '../../core/models/enums/tipo-servicio.enum';
import { DatePipe } from '@angular/common';
import { scheduleValidator } from '../../shared/validators/schedule.validator';
import { LucideBike, LucideShoppingBag, LucideStore, LucideUpload, LucideImage, LucideX, LucideCheck } from '@lucide/angular';
import { UpdateEstablecimientoRequest } from '../../core/models/update-establecimiento-request';
import { UpdateDireccionRequest } from '../../core/models/update-direccion-request';
import { PhoneMaskDirective } from '../../shared/directives/phone-mask.directive';
import { NotificationService } from '../../core/services/notification.service';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-configuracion',
  imports: [
    ReactiveFormsModule,
    DatePipe,
    LucideBike,
    LucideShoppingBag,
    LucideStore,
    LucideUpload,
    LucideImage,
    LucideX,
    LucideCheck,
    PhoneMaskDirective
  ],
  templateUrl: './configuracion.html',
  styleUrl: './configuracion.scss',
})
export class ConfiguracionComponent extends BaseFormComponent implements OnInit {

  protected override get form(): FormGroup {
    return this.establishmentForm;
  }
  private readonly fb = inject(FormBuilder);
  private readonly establecimientoService = inject(EstablecimientoService);
  private readonly notificationService = inject(NotificationService);

  protected establecimiento: EstablecimientoResponse | null = null;
  protected selectedLogoFile = signal<File | null>(null);
  protected logoPreviewUrl = signal<string | null>(null);
  protected isUploadingLogo = signal<boolean>(false);

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

  /* CONSTRUCCIÓN DE URL DEL LOGO */
  getImageUrl(logoUrl: string | null | undefined): string | null {
    if (!logoUrl) {
      return null;
    }
    if (logoUrl.startsWith('http://') || logoUrl.startsWith('https://')) {
      return logoUrl;
    }
    return `${environment.baseUrl}${logoUrl}`;
  }

  getLogoSrc(): string {
    if (this.logoPreviewUrl()) {
      return this.logoPreviewUrl()!;
    }
    const url = this.getImageUrl(this.establecimiento?.logoUrl);
    if (url) {
      return url;
    }
    return 'images/logo/gestia-isotype-light.svg';
  }

  hasCustomLogo(): boolean {
    return !!this.logoPreviewUrl() || !!this.establecimiento?.logoUrl;
  }

  /* SELECCIÓN DE IMAGEN PARA EL LOGO */
  onLogoSelected(event: Event): void {
    const input = event.target as HTMLInputElement;

    if (!input.files || input.files.length === 0) {
      return;
    }

    const file = input.files[0];

    /* VALIDAR FORMATO */
    const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png'];
    if (!allowedTypes.includes(file.type)) {
      this.notificationService.error('Formato de imagen no válido. Permitidos: JPG, JPEG, PNG.');
      input.value = '';
      return;
    }

    /* VALIDAR TAMAÑO (máx 2MB) */
    const maxSize = 2 * 1024 * 1024;
    if (file.size > maxSize) {
      this.notificationService.error('El tamaño del logo no debe superar los 2 MB.');
      input.value = '';
      return;
    }

    /* VALIDAR DIMENSIONES */
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
        this.notificationService.error(
          `Las dimensiones (${image.width}x${image.height}px) deben estar entre 300x300 y 3000x3000px.`
        );
        URL.revokeObjectURL(objectUrl);
        input.value = '';
        return;
      }

      if (this.logoPreviewUrl()) {
        URL.revokeObjectURL(this.logoPreviewUrl()!);
      }

      this.selectedLogoFile.set(file);
      this.logoPreviewUrl.set(objectUrl);
      input.value = '';
    };

    image.onerror = () => {
      this.notificationService.error('No se pudo procesar la imagen seleccionada.');
      URL.revokeObjectURL(objectUrl);
      input.value = '';
    };

    image.src = objectUrl;
  }

  cancelLogoSelection(): void {
    if (this.logoPreviewUrl()) {
      URL.revokeObjectURL(this.logoPreviewUrl()!);
    }
    this.selectedLogoFile.set(null);
    this.logoPreviewUrl.set(null);
  }

  guardarLogo(): void {
    const file = this.selectedLogoFile();
    if (!file) {
      return;
    }

    this.isUploadingLogo.set(true);
    this.establecimientoService.actualizarLogo(file).subscribe({
      next: (updatedEstablecimiento) => {
        this.establecimiento = updatedEstablecimiento;
        this.cancelLogoSelection();
        this.isUploadingLogo.set(false);
        this.notificationService.success('Logo del establecimiento actualizado correctamente.');
      },
      error: (error) => {
        console.error('Error al actualizar el logo del establecimiento', error);
        this.isUploadingLogo.set(false);
        this.notificationService.error('Error al actualizar el logo del establecimiento.');
      }
    });
  }

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
        this.notificationService.success('Información del establecimiento actualizada correctamente.');
      },
      error: (error) => {
        console.error('Error al actualizar el establecimiento.', error);
        this.notificationService.error('Error al actualizar el establecimiento.');
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