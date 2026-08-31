import { Component, EventEmitter, inject, Input, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { BaseFormComponent } from '../../../../shared/base/base-form.component';
import { CuponService } from '../../../../core/services/cupon.service';
import { EmployeesService } from '../../../../core/services/employees.service';
import { NotificationService } from '../../../../core/services/notification.service';
import { CuponResponse } from '../../../../core/models/cupon-response';
import { Employee } from '../../../../core/models/user-profile-response';
import { AsignarCuponRequest } from '../../../../core/models/asignar-cupon-request';
import { UserRole } from '../../../../core/models/enums/user-role.enum';
import { LucideX, LucideUserCheck, LucideMail, LucideUser } from '@lucide/angular';

export type TipoAsignacion = 'ID' | 'EMAIL';

@Component({
  selector: 'app-cupon-asignar-modal',
  imports: [ReactiveFormsModule, LucideX, LucideUserCheck, LucideMail, LucideUser],
  templateUrl: './cupon-asignar-modal.html',
  styleUrl: './cupon-asignar-modal.scss',
})
export class CuponAsignarModalComponent extends BaseFormComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly cuponService = inject(CuponService);
  private readonly employeesService = inject(EmployeesService);
  private readonly notificationService = inject(NotificationService);

  @Input() initialCuponId: number | null = null;
  @Input() cuponesList: CuponResponse[] = [];
  @Output() assigned = new EventEmitter<void>();
  @Output() cancel = new EventEmitter<void>();

  users: Employee[] = [];
  isLoadingUsers = false;
  isSubmitting = false;

  readonly asignarForm: FormGroup = this.fb.group({
    tipoAsignacion: ['ID', [Validators.required]],
    usuarioId: [null, [Validators.required]],
    email: ['', [Validators.email]],
    cuponId: [null, [Validators.required]]
  });

  protected override get form(): FormGroup {
    return this.asignarForm;
  }

  get tipoAsignacion(): TipoAsignacion {
    return this.asignarForm.get('tipoAsignacion')?.value as TipoAsignacion;
  }

  ngOnInit(): void {
    if (this.initialCuponId) {
      this.asignarForm.patchValue({ cuponId: this.initialCuponId });
    }
    this.loadUsers();
    if (!this.cuponesList || this.cuponesList.length === 0) {
      this.loadCupones();
    }
  }

  setTipoAsignacion(tipo: TipoAsignacion): void {
    this.asignarForm.patchValue({ tipoAsignacion: tipo });

    const usuarioIdControl = this.asignarForm.get('usuarioId');
    const emailControl = this.asignarForm.get('email');

    if (tipo === 'ID') {
      usuarioIdControl?.setValidators([Validators.required]);
      emailControl?.clearValidators();
      emailControl?.setValue('');
    } else {
      emailControl?.setValidators([Validators.required, Validators.email]);
      usuarioIdControl?.clearValidators();
      usuarioIdControl?.setValue(null);
    }

    usuarioIdControl?.updateValueAndValidity();
    emailControl?.updateValueAndValidity();
  }

  private loadUsers(): void {
    this.isLoadingUsers = true;
    this.employeesService.getEmployees(undefined, UserRole.CLIENTE).subscribe({
      next: (data) => {
        if (data && data.length > 0) {
          this.users = data;
          this.isLoadingUsers = false;
        } else {
          // Si no retornó usuarios con rol CLIENTE, cargar la lista completa
          this.loadAllUsers();
        }
      },
      error: () => {
        this.loadAllUsers();
      }
    });
  }

  private loadAllUsers(): void {
    this.employeesService.getEmployees().subscribe({
      next: (data) => {
        this.users = data || [];
        this.isLoadingUsers = false;
      },
      error: () => {
        this.isLoadingUsers = false;
      }
    });
  }

  private loadCupones(): void {
    this.cuponService.listarCupones().subscribe({
      next: (data) => {
        this.cuponesList = data || [];
      }
    });
  }

  onCancel(): void {
    this.cancel.emit();
  }

  onSubmit(): void {
    if (this.asignarForm.invalid) {
      this.markFormAsTouched();
      return;
    }

    this.isSubmitting = true;
    const formVal = this.asignarForm.value;

    const req: AsignarCuponRequest = {
      cuponId: Number(formVal.cuponId)
    };

    if (formVal.tipoAsignacion === 'EMAIL') {
      req.email = formVal.email ? formVal.email.trim() : '';
    } else {
      req.usuarioId = Number(formVal.usuarioId);
    }

    this.cuponService.asignarCupon(req).subscribe({
      next: () => {
        this.isSubmitting = false;
        this.notificationService.success('Cupón asignado correctamente.');
        this.assigned.emit();
      },
      error: (err) => {
        this.isSubmitting = false;
        const msg = err?.error?.message || err?.error?.mensaje || 'Error al asignar el cupón';
        this.notificationService.error(msg);
      }
    });
  }
}

