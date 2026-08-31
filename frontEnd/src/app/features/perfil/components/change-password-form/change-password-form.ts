import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { passwordMatchValidator } from '../../../../shared/validators/password-match.validator';
import { UpdatePasswordRequest } from '../../../../core/models/update-password-request';
import { ProfileService } from '../../../../core/services/profile.service';
import { BaseFormComponent } from '../../../../shared/base/base-form.component';
import { LucideCircle, LucideCircleCheck, LucideEye, LucideEyeOff } from '@lucide/angular';

@Component({
  selector: 'app-change-password-form',
  imports: [ReactiveFormsModule, LucideEye,
    LucideEyeOff,
    LucideCircle,
    LucideCircleCheck],
  templateUrl: './change-password-form.html',
  styleUrl: './change-password-form.scss',
})
export class ChangePasswordFormComponent extends BaseFormComponent implements OnInit {

  protected override get form(): FormGroup {
    return this.passwordForm;
  }

  private readonly fb = inject(FormBuilder);
  private readonly profileService = inject(ProfileService);

  readonly passwordForm = this.fb.group({
    
    passwordActual: ['', Validators.required],
    passwordNueva: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(20), Validators.pattern(/^(?=.*[A-Z])(?=.*\d).+$/)]],
    confirmPassword: ['', Validators.required]
    
  },
  {
    validators: passwordMatchValidator('passwordNueva','confirmPassword')
  });

  onSubmit(): void {
  
    const request: UpdatePasswordRequest = {
  
      passwordActual: this.passwordForm.value.passwordActual!,
      passwordNueva: this.passwordForm.value.passwordNueva!
  
    };
  
    this.profileService
      .changePassword(request)
      .subscribe({
  
        next: (response) => {
  
          console.log(response);
          this.passwordForm.reset();
  
          this.passwordRequirements.forEach(requirement => {
              requirement.valid = false;
          });
  
        },
  
        error: (error) => {
  
          console.error('Error al cambiar la contraseña:', error);
  
        }
  
      });
  
  }

  ngOnInit(): void {

    this.passwordForm
    .get('passwordNueva')
    ?.valueChanges
    .subscribe(password => {
      this.updatePasswordRequirements(password ?? '');
    });
    
  }

  showCurrentPassword = false;
  showNewPassword = false;
  showConfirmPassword = false;

  toggleCurrentPassword(): void {
    this.showCurrentPassword = !this.showCurrentPassword;
  }

  toggleNewPassword(): void {
    this.showNewPassword = !this.showNewPassword;
  }

  toggleConfirmPassword(): void {
    this.showConfirmPassword = !this.showConfirmPassword;
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
