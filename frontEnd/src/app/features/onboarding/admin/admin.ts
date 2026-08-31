import { Component, inject, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilder, ReactiveFormsModule, Validators, FormGroup } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';
import { AdminSetup } from '../../../core/models/admin-setup.model';
import { PhoneMaskDirective } from '../../../shared/directives/phone-mask.directive';
import { DniMaskDirective } from '../../../shared/directives/dni-mask.directive';
import { LucideEye, LucideEyeOff, LucideCircle, LucideCircleCheck, LucideArrowRight } from '@lucide/angular';
import { pastDateValidator } from '../../../shared/validators/past-date.validator';
import { passwordMatchValidator } from '../../../shared/validators/password-match.validator';
import { BaseFormComponent } from '../../../shared/base/base-form.component';
import { TokenService } from '../../../core/services/token.service';

@Component({
  selector: 'app-admin',
  imports: [
    ReactiveFormsModule,
    PhoneMaskDirective,
    DniMaskDirective,
    LucideEye,
    LucideEyeOff,
    LucideCircle,
    LucideCircleCheck,
    LucideArrowRight
    ],
  templateUrl: './admin.html',
  styleUrl: './admin.scss',
})
export class AdminComponent extends BaseFormComponent implements OnInit {

  protected override get form(): FormGroup {
    return this.adminForm;
  }

  /* INYECCIÓN DE DEPENDENCIAS */
  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly tokenService = inject(TokenService);
  private readonly router = inject(Router);

  /* FORMULARIO */
  readonly adminForm = this.fb.group({

    nombre: ['', [Validators.required, Validators.maxLength(100)]],
    apellido: ['', [Validators.required, Validators.maxLength(100)]],
    email: ['', [Validators.required, Validators.email, Validators.pattern(/^[^\s@]+@[^\s@]+\.[^\s@]+$/), Validators.maxLength(150)]],
    password: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(255), Validators.pattern(/^(?=.*[A-Z])(?=.*\d).+$/)]],
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
    confirmPassword: ['', Validators.required]

  },
  {
    validators: passwordMatchValidator('password','confirmPassword')
  });

  readonly header = {

    eyebrow: 'Configuración',
    title: 'Datos del administrador',
    subtitle: 'Completá la información para crear la cuenta principal del establecimiento.'

  };

  //Escucha los requisitos de contraseña
  ngOnInit(): void {

    this.adminForm
      .get('password')
      ?.valueChanges
      .subscribe(password => {

        this.updatePasswordRequirements(password ?? '');

      });

  }

  onSubmit(): void {
  
    if (this.form.invalid) {

      this.markFormAsTouched();

      return;

    }
  
    const admin = this.adminForm.value as AdminSetup;
  
    this.authService
      .registerAdmin(admin)
      .subscribe({
  
        next: (response) => {
  
          this.tokenService.saveToken(response.token);
          
          console.log('Admin creado correctamente.');
          console.log(response);
          
          // Acá navegamos al login.
          this.router.navigate(['/login']);
  
        },
  
        error: (error) => {
  
          console.error('Error al crear el admin.');
          console.error(error);
  
        }
  
      });
  
  }

  showPassword = false;
  showConfirmPassword = false;

  togglePassword(): void {
    this.showPassword = !this.showPassword;
  }

  toggleConfirmPassword(): void {
    this.showConfirmPassword = !this.showConfirmPassword;
  }

  //Impedir seleccionar fechas futuras
  readonly maxBirthDate = this.getMaxBirthDate();

  private getMaxBirthDate(): string {

    const today = new Date();

    const year = today.getFullYear();
    const month = String(today.getMonth() + 1).padStart(2, '0');
    const day = String(today.getDate()).padStart(2, '0');

    return `${year}-${month}-${day}`;

  }


  //ARREGLO DE REQUISITOS
  passwordRequirements = [
    {
      key: 'minLength',
      label: 'Mínimo 8 caracteres',
      validator: (password: string) => password.length >= 8,
      valid: false
    },
    {
      key: 'uppercase',
      label: 'Una letra mayúscula',
      validator: (password: string) => /[A-Z]/.test(password),
      valid: false
    },
    {
      key: 'number',
      label: 'Un número',
      validator: (password: string) => /\d/.test(password),
      valid: false
    }
  ];

  //MÉTODO: ¿cada requisito se cumple? (no sabe que significa cada uno)
  updatePasswordRequirements(password: string): void {

    this.passwordRequirements = this.passwordRequirements.map(requirement => ({

      ...requirement,

      valid: requirement.validator(password)

    }));

  }


}
