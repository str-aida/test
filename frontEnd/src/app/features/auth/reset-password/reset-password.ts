import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';
import { BaseFormComponent } from '../../../shared/base/base-form.component';
import { ResetPasswordRequest } from '../../../core/models/reset-password-request';
import { LucideCircle, LucideCircleCheck, LucideEye, LucideEyeOff } from '@lucide/angular';
import { Router} from '@angular/router';

@Component({
  selector: 'app-reset-password',
  imports: [
    ReactiveFormsModule,
    LucideEye,
    LucideEyeOff,
    LucideCircle,
    LucideCircleCheck
  ],
  templateUrl: './reset-password.html',
  styleUrl: './reset-password.scss',
})
export class ResetPasswordComponent extends BaseFormComponent implements OnInit {

  protected override get form(): FormGroup {
    return this.resetPasswordForm;
  }

  /* INYECCIÓN DE DEPENDENCIAS */
  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  readonly resetPasswordForm = this.fb.group({
  
    token: ['', Validators.required],
    nuevaPassword: ['', [Validators.required, Validators.maxLength(255), Validators.pattern(/^(?=.*[A-Z])(?=.*\d).+$/)]]

  });

  //Escucha los requisitos de contraseña
  ngOnInit(): void {

    this.resetPasswordForm
      .get('nuevaPassword')
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
  
    const request = this.resetPasswordForm.value as ResetPasswordRequest;
  
    this.authService
      .restablecerPassword(request)
      .subscribe({
  
        next: () => {
  
          console.log('Entró al next');

          this.router.navigate(['/login']).then(result => {
            console.log('navigate:', result);
            console.log('url:', this.router.url);
          });
  
        },
  
        error: (error) => {
  
          console.error('Error al restablecer la contraseña.');
          console.error(error);
  
        }
  
      });
  
  }

  showPassword = false;

  togglePassword(): void {
    this.showPassword = !this.showPassword;
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
