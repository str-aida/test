import { Component, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { BaseFormComponent } from '../../../shared/base/base-form.component';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ForgotPasswordRequest } from '../../../core/models/forgot-password-request';
import { AuthService } from '../../../core/services/auth.service';


@Component({
  selector: 'app-forgot-password',
  imports: [RouterLink, ReactiveFormsModule],
  templateUrl: './forgot-password.html',
  styleUrl: './forgot-password.scss',
})
export class ForgotPasswordComponent extends BaseFormComponent {

  protected override get form(): FormGroup {
    return this.forgotPasswordForm;
  }

  /* INYECCIÓN DE DEPENDENCIAS */
  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  readonly forgotPasswordForm = this.fb.group({
  
    email: ['', [Validators.required, Validators.email, Validators.pattern(/^[^\s@]+@[^\s@]+\.[^\s@]+$/)]]

  });

  onSubmit(): void {

    if (this.form.invalid) {

      this.markFormAsTouched();

      return;

    }

    const request = this.forgotPasswordForm.value as ForgotPasswordRequest;

    this.authService
      .solicitarRecuperacionPassword(request)
      .subscribe({

        next: () => {

          console.log('Entró al next');

          this.router.navigate(['/reset-password']).then(result => {
            console.log('navigate:', result);
            console.log('url:', this.router.url);
          });

        },

        error: (error) => {

          console.error('Error al solicitar la recuperación.');
          console.error(error);

        }

      });

  }

}
