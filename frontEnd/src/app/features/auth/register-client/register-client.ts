import { Component, inject, OnInit } from '@angular/core';
import { AuthService } from '../../../core/services/auth.service';
import { AdminSetup } from '../../../core/models/admin-setup.model';
import { UserRole } from '../../../core/models/enums/user-role.enum';

import { PhoneMaskDirective } from '../../../shared/directives/phone-mask.directive';
import { LucideCircle, LucideCircleCheck, LucideEye, LucideEyeOff } from '@lucide/angular';
import { DniMaskDirective } from '../../../shared/directives/dni-mask.directive';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { BaseFormComponent } from '../../../shared/base/base-form.component';
import { USER_ROLES } from '../../empleados/data/user-roles';
import { pastDateValidator } from '../../../shared/validators/past-date.validator';
import { passwordMatchValidator } from '../../../shared/validators/password-match.validator';
import { TokenService } from '../../../core/services/token.service';
import { Router } from '@angular/router';
import { NotificationService } from '../../../core/services/notification.service';

@Component({
  selector: 'app-register-client',
  imports: [
    ReactiveFormsModule,
    PhoneMaskDirective,
    DniMaskDirective,
    LucideEye,
    LucideEyeOff,
    LucideCircle,
    LucideCircleCheck
  ],
  templateUrl: './register-client.html',
  styleUrl: './register-client.scss',
})
export class RegisterClientComponent extends BaseFormComponent implements OnInit {

  private readonly router = inject(Router);
  private readonly notificationService = inject(NotificationService);

  protected override get form(): FormGroup {
    return this.clientForm;
  }

  /* INYECCIÓN DE DEPENDENCIAS */
  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly tokenService = inject(TokenService);
  protected readonly userRoles = USER_ROLES;

  /* FORMULARIO */
  readonly clientForm = this.fb.group({

    nombre: ['', [Validators.required, Validators.maxLength(100)]],
    apellido: ['', [Validators.required, Validators.maxLength(100)]],
    email: ['', [Validators.required, Validators.email, Validators.pattern(/^[^\s@]+@[^\s@]+\.[^\s@]+$/), Validators.maxLength(150)]],
    password: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(255), Validators.pattern(/^(?=.*[A-Z])(?=.*\d).+$/)]],
    telefono: ['', [Validators.required, Validators.maxLength(20)]],
    dni: ['', [Validators.required, Validators.minLength(7), Validators.maxLength(9)]],
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

  //Escucha los requisitos de contraseña
  ngOnInit(): void {

    this.clientForm
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
  
    const client = this.clientForm.value as AdminSetup;
  
    this.authService
      .registerCliente(client)
      .subscribe({
  
        next: (response) => {

          this.tokenService.saveToken(response.token);

          console.log(response);
          this.router.navigate(['/cliente']);
  
        },
  
        error: (error) => {

          console.log(error);
  
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
