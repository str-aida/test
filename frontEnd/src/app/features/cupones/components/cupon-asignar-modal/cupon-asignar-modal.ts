import { Component, ElementRef, EventEmitter, HostListener, inject, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { BaseFormComponent } from '../../../../shared/base/base-form.component';
import { CuponService } from '../../../../core/services/cupon.service';
import { EmployeesService } from '../../../../core/services/employees.service';
import { NotificationService } from '../../../../core/services/notification.service';
import { CuponResponse } from '../../../../core/models/cupon-response';
import { AsignarCuponRequest } from '../../../../core/models/asignar-cupon-request';
import { UsuarioPerfilResponse } from '../../../../core/models/usuario-perfil-response';
import { UserRole } from '../../../../core/models/enums/user-role.enum';
import { LucideX, LucideUserCheck, LucideMail, LucideUser, LucideSearch, LucideLoader2, LucideCheck } from '@lucide/angular';
import { catchError, debounceTime, distinctUntilChanged, of, Subscription, switchMap, tap } from 'rxjs';

export type TipoAsignacion = 'ID' | 'EMAIL';

@Component({
  selector: 'app-cupon-asignar-modal',
  imports: [
    ReactiveFormsModule,
    LucideX,
    LucideUserCheck,
    LucideMail,
    LucideUser,
    LucideSearch,
    LucideLoader2,
    LucideCheck
  ],
  templateUrl: './cupon-asignar-modal.html',
  styleUrl: './cupon-asignar-modal.scss',
})
export class CuponAsignarModalComponent extends BaseFormComponent implements OnInit, OnDestroy {
  private readonly fb = inject(FormBuilder);
  private readonly cuponService = inject(CuponService);
  private readonly employeesService = inject(EmployeesService);
  private readonly notificationService = inject(NotificationService);
  private readonly elementRef = inject(ElementRef);

  @Input() initialCuponId: number | null = null;
  @Input() cuponesList: CuponResponse[] = [];
  @Output() assigned = new EventEmitter<void>();
  @Output() cancel = new EventEmitter<void>();

  readonly busquedaControl = new FormControl<string>('');
  searchResults: UsuarioPerfilResponse[] = [];
  selectedUser: UsuarioPerfilResponse | null = null;

  isSearching = false;
  searchError = false;
  showDropdown = false;
  isSubmitting = false;

  private searchSubscription?: Subscription;

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
    if (!this.cuponesList || this.cuponesList.length === 0) {
      this.loadCupones();
    }
    this.setupAutocomplete();
  }

  ngOnDestroy(): void {
    this.searchSubscription?.unsubscribe();
  }

  private setupAutocomplete(): void {
    this.searchSubscription = this.busquedaControl.valueChanges.pipe(
      tap((term) => {
        const query = term ? term.trim() : '';
        if (this.selectedUser && query !== `${this.selectedUser.nombre} ${this.selectedUser.apellido} – ${this.selectedUser.email}`) {
          this.selectedUser = null;
          this.asignarForm.patchValue({ usuarioId: null });
        }

        if (query.length < 2) {
          this.searchResults = [];
          this.isSearching = false;
          this.searchError = false;
          this.showDropdown = false;
        } else {
          this.isSearching = true;
          this.showDropdown = true;
        }
      }),
      debounceTime(300),
      distinctUntilChanged(),
      switchMap((term) => {
        const query = term ? term.trim() : '';
        if (query.length < 2) {
          return of([]);
        }
        this.isSearching = true;
        this.searchError = false;
        return this.employeesService.buscarUsuarios(query, UserRole.CLIENTE).pipe(
          catchError((err) => {
            console.error('Error al buscar usuarios:', err);
            this.searchError = true;
            return of([]);
          })
        );
      })
    ).subscribe({
      next: (results) => {
        this.searchResults = results || [];
        this.isSearching = false;
      }
    });
  }

  onInputFocus(): void {
    const val = this.busquedaControl.value?.trim() || '';
    if (val.length >= 2) {
      this.showDropdown = true;
    }
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    if (!this.elementRef.nativeElement.contains(event.target)) {
      this.showDropdown = false;
    }
  }

  seleccionarUsuario(user: UsuarioPerfilResponse): void {
    this.selectedUser = user;
    this.asignarForm.patchValue({ usuarioId: user.id });
    this.busquedaControl.setValue(`${user.nombre} ${user.apellido} – ${user.email}`, { emitEvent: false });
    this.showDropdown = false;
    this.asignarForm.get('usuarioId')?.markAsTouched();
    this.asignarForm.get('usuarioId')?.updateValueAndValidity();
  }

  limpiarSeleccion(): void {
    this.selectedUser = null;
    this.asignarForm.patchValue({ usuarioId: null });
    this.busquedaControl.setValue('');
    this.searchResults = [];
    this.showDropdown = false;
    this.asignarForm.get('usuarioId')?.updateValueAndValidity();
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
      this.limpiarSeleccion();
    }

    usuarioIdControl?.updateValueAndValidity();
    emailControl?.updateValueAndValidity();
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
