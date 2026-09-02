import { Component, EventEmitter, inject, Input, OnChanges, OnInit, Output} from '@angular/core';
import { AuthService } from '../../../../core/services/auth.service';
import { AdminSetup } from '../../../../core/models/admin-setup.model';
import { UserRole } from '../../../../core/models/enums/user-role.enum';
import { BaseFormComponent } from '../../../../shared/base/base-form.component';
import { FormBuilder, ReactiveFormsModule, Validators, FormGroup } from '@angular/forms';
import { PhoneMaskDirective } from '../../../../shared/directives/phone-mask.directive';
import { DniMaskDirective } from '../../../../shared/directives/dni-mask.directive';
import { pastDateValidator } from '../../../../shared/validators/past-date.validator';
import { passwordMatchValidator } from '../../../../shared/validators/password-match.validator';
import { LucideCircle, LucideCircleCheck, LucideEye, LucideEyeOff } from '@lucide/angular';
import { Employee } from '../../../../core/models/user-profile-response';
import { UpdateEmployeeRequest } from '../../../../core/models/update-user-request';
import { EmployeesService } from '../../../../core/services/employees.service';
import { Estado } from '../../../../core/models/enums/estado.enum';
import { USER_ROLES } from '../../data/user-roles';
import { NotificationService } from '../../../../core/services/notification.service';


@Component({
  selector: 'app-employee-form',
  imports: [ReactiveFormsModule, PhoneMaskDirective, DniMaskDirective, LucideEye, LucideEyeOff, LucideCircle, LucideCircleCheck],
  templateUrl: './employee-form.html',
  styleUrl: './employee-form.scss',
})
export class EmployeeFormComponent extends BaseFormComponent implements OnInit,OnChanges {

  @Input() editingEmployee: Employee | null = null;
  @Output() employeeCreated = new EventEmitter<void>();
  @Output() employeeUpdated = new EventEmitter<void>();
  @Output() cancel = new EventEmitter<void>();

  protected override get form(): FormGroup {
    return this.employeeForm;
  }

  /* INYECCIÓN DE DEPENDENCIAS */
  private readonly fb = inject(FormBuilder);
  private readonly employeeService = inject(EmployeesService);
  private readonly authService = inject(AuthService);
  private readonly notificationService = inject(NotificationService);
  protected readonly userRoles = USER_ROLES;
  protected readonly Estado = Estado;

  /* FORMULARIO */
  readonly employeeForm = this.fb.group({

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
    rol: [UserRole.EMPLEADO, Validators.required],
    estado: [Estado.ACTIVO, Validators.required],
    confirmPassword: ['', Validators.required]

  },
  {
    validators: passwordMatchValidator('password','confirmPassword')
  });

  //Escucha los requisitos de contraseña
  ngOnInit(): void {
    this.employeeForm
      .get('password')
      ?.valueChanges
      .subscribe(password => {
        this.updatePasswordRequirements(password ?? '');
      });
  }

  get isEditMode(): boolean {
    return this.editingEmployee !== null;
  }

  ngOnChanges(): void {
    if (!this.editingEmployee) {
      return;
    }

    this.configureEditMode();

    this.employeeForm.patchValue({
      nombre: this.editingEmployee.nombre,
      apellido: this.editingEmployee.apellido,
      email: this.editingEmployee.email,
      telefono: this.editingEmployee.telefono,
      estado: this.editingEmployee.estado,
    });
  }

  private configureEditMode(): void {
    this.employeeForm.get('password')?.disable();
    this.employeeForm.get('confirmPassword')?.disable();
    this.employeeForm.get('dni')?.disable();
    this.employeeForm.get('fechaNacimiento')?.disable();
    this.employeeForm.get('direccion')?.disable();
    this.employeeForm.get('rol')?.disable();
  }

  onCancel(): void {
    this.cancel.emit();
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.markFormAsTouched();
      return;
    }
    if (this.isEditMode) {
      this.updateEmployee();
      return;
    }
    this.createEmployee();
  }

  private createEmployee(): void {
    const employee = this.employeeForm.value as AdminSetup;
  
    this.authService.crearPersonal(employee).subscribe({
      next: () => {
        this.notificationService.success(`Personal creado correctamente.`);
        this.employeeCreated.emit();
      },
      error: (error) => {
        console.log(error);
      }
    });
  }

  private updateEmployee(): void {
    if (!this.editingEmployee) {
      return;
    }
  
    const employee: UpdateEmployeeRequest = {
      nombre: this.employeeForm.value.nombre!,
      apellido: this.employeeForm.value.apellido!,
      telefono: this.employeeForm.value.telefono!,
      email: this.employeeForm.value.email!,
      estado: this.employeeForm.value.estado!
    };
          
    this.employeeService.updateEmployee(this.editingEmployee.id, employee).subscribe({
      next: () => {
        this.notificationService.success(`Personal actualizado exitosamente.`);
        this.employeeUpdated.emit();
      },
      error: (error) => {
        console.error('Error al editar el personal', error);
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
