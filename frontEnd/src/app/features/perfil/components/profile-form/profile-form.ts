import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { PhoneMaskDirective } from '../../../../shared/directives/phone-mask.directive';
import { DniMaskDirective } from '../../../../shared/directives/dni-mask.directive';
import { BaseFormComponent } from '../../../../shared/base/base-form.component';
import { ProfileService } from '../../../../core/services/profile.service';
import { Employee } from '../../../../core/models/user-profile-response';
import { UpdateProfileRequest } from '../../../../core/models/update-profile-request';
import { Direccion } from '../../../../core/models/direccion.model';
import { pastDateValidator } from '../../../../shared/validators/past-date.validator';

@Component({
  selector: 'app-profile-form',
  imports: [ReactiveFormsModule, PhoneMaskDirective, DniMaskDirective],
  templateUrl: './profile-form.html',
  styleUrl: './profile-form.scss',
})
export class ProfileFormComponent extends BaseFormComponent implements OnInit {

  protected override get form(): FormGroup {
    return this.profileForm;
  }

  private readonly fb = inject(FormBuilder);
  private readonly profileService = inject(ProfileService);

  protected profile?: Employee;
  protected isEditing = false;

  /* FORMULARIO */
  readonly profileForm = this.fb.group({

    nombre: ['', [Validators.required, Validators.maxLength(100)]],
    apellido: ['', [Validators.required, Validators.maxLength(100)]],
    email: ['', [Validators.required, Validators.email, Validators.pattern(/^[^\s@]+@[^\s@]+\.[^\s@]+$/), Validators.maxLength(150)]],
    telefono: ['', [Validators.required, Validators.maxLength(20)]],
    dni: ['', [Validators.required, Validators.maxLength(20)]],
    fechaNacimiento: ['', [Validators.required, pastDateValidator()]],
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
    rol: ['', Validators.required]

  });
  
  /* CARGAR DATOS EN EL FORMULARIO */
  private fillForm(profile: Employee): void {

    this.profile = profile;

    this.profileForm.patchValue({

      nombre: profile.nombre,
      apellido: profile.apellido,
      email: profile.email,
      telefono: profile.telefono,
      dni: profile.dni,
      fechaNacimiento: profile.fechaNacimiento,
      direccion: {
        nombre: profile.direccion.nombre,
        calle: profile.direccion.calle,
        numero: profile.direccion.numero,
        localidad: profile.direccion.localidad,
        piso: profile.direccion.piso,
        departamento: profile.direccion.departamento,
        codigoPostal: profile.direccion.codigoPostal,
        referencia: profile.direccion.referencia
      },
      rol: profile.rol

    });

    this.profileForm.disable();
    
    this.isEditing = false;

  }

  /* CONSTRUIR REQUEST PARA ACTUALIZAR EL PERFIL */
  private buildRequest(): UpdateProfileRequest {

    return {

      nombre: this.profileForm.controls.nombre.value!,
      apellido: this.profileForm.controls.apellido.value!,
      telefono: this.profileForm.controls.telefono.value!,
      fechaNacimiento: this.profileForm.controls.fechaNacimiento.value!,
      direccion: {
        ...(this.profileForm.controls.direccion.getRawValue() as Direccion),
        esPrincipal: this.profile!.direccion.esPrincipal
      }

    };

  }

  /* ACTUALIZAR PERFIL */
  onSubmit(): void {

    if (this.form.invalid) {

      this.markFormAsTouched();

      return;

    }

    this.profileService.updateProfile(this.buildRequest()).subscribe({

      next: (profile) => {

        this.fillForm(profile);

      },

      error: (error) => {

        console.error('Error al actualizar el perfil.', error);

      }

    });

  }
  
  /* INICIALIZAR COMPONENTE */
  ngOnInit(): void {
    this.loadProfile();
  }

  /* OBTENER PERFIL DEL USUARIO */
  private loadProfile(): void {

    this.profileService.getProfile().subscribe({

      next: (profile: Employee) => {

        this.fillForm(profile);

      },

      error: (error) => {

        console.error('Error al obtener el perfil', error);

      }

    });

  }

  /* MÉTODO PARA ENTRAR EN MODO DE EDICIÓN */
  editProfile(): void {

    this.isEditing = true;

    this.profileForm.enable();

    // Estos nunca se editan
    this.profileForm.controls.email.disable();
    this.profileForm.controls.dni.disable();
    this.profileForm.controls.rol.disable();

  }

  /* MÉTODO PARA CANCELAR LA EDICIÓN */
  cancelEdit(): void {

    if (!this.profile) {
      return;
    }

    this.fillForm(this.profile);

  }


  readonly maxBirthDate = this.getMaxBirthDate();

  private getMaxBirthDate(): string {

    const today = new Date();

    const year = today.getFullYear();
    const month = String(today.getMonth() + 1).padStart(2, '0');
    const day = String(today.getDate()).padStart(2, '0');

    return `${year}-${month}-${day}`;

  }


}