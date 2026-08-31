import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { EmployeesService } from '../../../../core/services/employees.service';
import { Employee } from '../../../../core/models/user-profile-response';
import { FormsModule } from '@angular/forms';
import { UserRole } from '../../../../core/models/enums/user-role.enum';
import { UpdateEmployeeRequest } from '../../../../core/models/update-user-request';
import { Estado } from '../../../../core/models/enums/estado.enum';

@Component({
  selector: 'app-employees-table',
  imports: [FormsModule],
  templateUrl: './employees-table.html',
  styleUrl: './employees-table.scss',
})
export class EmployeesTableComponent implements OnInit {

  private readonly employeesService = inject(EmployeesService);
  protected readonly Estado = Estado;
  private readonly cdr = inject(ChangeDetectorRef);

  employees: Employee[] = [];
  texto = '';
  rol: UserRole | '' = '';

  ngOnInit(): void {
    this.loadEmployees();
  }

  loadEmployees(): void {
    this.employeesService
    .getEmployees(this.texto, this.rol || undefined)
    .subscribe({
      next: employees => {
        this.employees = employees;

        // Fuerza la actualización de la vista (luego de recibir la respuesta del backend)
        // PENDIENTE: investigar la causa de la configuracion global
        this.cdr.detectChanges();
      },
      error: err => {
        console.error(err);
      }
    });
  }

  search(): void {
    this.loadEmployees();
  }

  
  editingEmployeeId: number | null = null;
  editingEmployee?: UpdateEmployeeRequest;

  edit(employee: Employee): void {

    this.editingEmployeeId = employee.id;

    this.editingEmployee = {
      nombre: employee.nombre,
      apellido: employee.apellido,
      telefono: employee.telefono,
      email: employee.email,
      estado: employee.estado
    };

  }

  cancel(): void {

    this.editingEmployeeId = null;
    this.editingEmployee = undefined;

  }

  save(employee: Employee): void {

    if (!this.editingEmployee) {
      return;
    }

    this.employeesService
      .updateEmployee(employee.id, this.editingEmployee)
      .subscribe({

        next: () => {

          this.cancel();
          this.loadEmployees();
          
        },

        error: error => {

          console.error('Error al actualizar el empleado.');
          console.error(error);

        }

      });

  }

}
