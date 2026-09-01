import { Component, inject, ViewChild} from '@angular/core';
import { EmployeesTableComponent } from '../../components/employees-table/employees-table';
import { LucideAlertTriangle, LucidePlus, LucideShapes, LucideX } from '@lucide/angular';
import { Employee } from '../../../../core/models/user-profile-response';
import { EmployeeFormComponent } from '../../components/employee-form/employee-form';
import { EmployeesService } from '../../../../core/services/employees.service';
import { UpdateEmployeeRequest } from '../../../../core/models/update-user-request';
import { Estado } from '../../../../core/models/enums/estado.enum';

@Component({
  selector: 'app-employees-list',
  imports: [EmployeesTableComponent, EmployeeFormComponent, LucideShapes, LucidePlus, LucideX, LucideAlertTriangle],
  templateUrl: './employees-list.html',
  styleUrl: './employees-list.scss',
})
export class EmployeesListComponent {

  private readonly employeeService = inject(EmployeesService);

  @ViewChild(EmployeesTableComponent)
  employeesTable?: EmployeesTableComponent;

  selectedEmployee : Employee | null = null;
  showCreateModal = false;
  showDeleteModal = false;
  showEditModal = false;

  openCreateModal(): void {
    this.showCreateModal = true;
  }

  closeCreateModal(): void {
    this.showCreateModal = false;
  }

  onEmployeeCreate(): void {
    this.closeCreateModal();
    this.employeesTable?.loadEmployees();
  }

  openEditModal(employee: Employee): void {
    this.selectedEmployee = employee;
    this.showEditModal = true;
  }

  closeEditModal(): void {
    this.showEditModal = false;
    this.selectedEmployee = null;
  }

  onEmployeeUpdated(): void {
    this.closeEditModal();
    this.employeesTable?.loadEmployees();
  }
  
  openDeleteModal(employee: Employee): void {
    this.selectedEmployee = employee;
    this.showDeleteModal = true;
  }

  closeDeleteModal(): void {
    this.showDeleteModal = false;
    this.selectedEmployee = null;
  }
  
  confirmDelete(): void {
    if (!this.selectedEmployee) {
      return;
    }
  
    const employee = this.selectedEmployee;
  
    //Cerramos el modal inmediatamente
    this.closeDeleteModal();

    const request: UpdateEmployeeRequest = {
      nombre: employee.nombre,
      apellido: employee.apellido,
      telefono: employee.telefono,
      email: employee.email,
      estado: Estado.INACTIVO
    };
      
    this.employeeService.updateEmployee(employee.id, request).subscribe({
      next: () => {
        this.employeesTable?.loadEmployees();
      },
      error: (error) => {
        console.error('Error al desactivar el empleado', error);
      }
    });
  }

}
