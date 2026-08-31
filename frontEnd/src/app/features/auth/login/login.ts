import { Component, inject } from '@angular/core';
import { BaseFormComponent } from '../../../shared/base/base-form.component';
import { Router, RouterLink } from '@angular/router';
import { LucideEye, LucideEyeOff, LucideArrowRight } from '@lucide/angular';
import { LoginRequest } from '../../../core/models/login-request';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';
import { TokenService } from '../../../core/services/token.service';
import { UserRole } from '../../../core/models/enums/user-role.enum';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, LucideEye, LucideEyeOff, LucideArrowRight, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.scss',
})
export class LoginComponent extends BaseFormComponent {

  protected override get form(): FormGroup {
    return this.loginForm;
  }

  /* INYECCIÓN DE DEPENDENCIAS */
  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly tokenService = inject(TokenService);
  private readonly router = inject(Router);

  /* FORMULARIO */
  readonly loginForm = this.fb.group({
  
    email: ['', [Validators.required, Validators.email, Validators.pattern(/^[^\s@]+@[^\s@]+\.[^\s@]+$/)]],
    password: ['', Validators.required]
  
  });

  onSubmit(): void {

    if (this.form.invalid) {

      this.markFormAsTouched();

      return;

    }

    const credentials = this.loginForm.value as LoginRequest;

    this.authService
      .login(credentials)
      .subscribe({

        next: (response) => {

          this.tokenService.saveToken(response.token);

          switch (this.tokenService.getRole()) {

            case UserRole.ADMIN:
              this.router.navigate(['/admin']);
              break;

            case UserRole.EMPLEADO:
              this.router.navigate(['/empleado']);
              break;

            case UserRole.CLIENTE:
              this.router.navigate(['/cliente']);
              break;

            default:
              this.router.navigate(['/login']);

          }

          console.log('Login exitoso.');

        },

        error: (error) => {

          console.error('Error al iniciar sesión.');
          console.error(error);

        }

      });

  }

  showPassword = false;

  togglePassword(): void {
    this.showPassword = !this.showPassword;
  }

}
