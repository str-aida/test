import { Component, EventEmitter, inject, OnInit, Output, signal } from '@angular/core';
import { EmployeesService } from '../../../../core/services/employees.service';
import { Employee } from '../../../../core/models/user-profile-response';
import { FormsModule } from '@angular/forms';
import { UserRole } from '../../../../core/models/enums/user-role.enum';
import { Estado } from '../../../../core/models/enums/estado.enum';
import { LucidePencil, LucideShield, LucideTrash2, LucideUserCheck } from '@lucide/angular';

@Component({
  selector: 'app-employees-table',
  imports: [FormsModule, LucideShield, LucideUserCheck, LucidePencil, LucideTrash2],
  templateUrl: './employees-table.html',
  styleUrl: './employees-table.scss',
})
export class EmployeesTableComponent implements OnInit {

  private readonly employeesService = inject(EmployeesService);
  protected readonly Estado = Estado;
  protected readonly UserRole = UserRole;
  @Output() editEmployee = new EventEmitter<Employee>();
  @Output() deleteEmployee = new EventEmitter<Employee>();

  employees = signal<Employee[]>([]);
  texto = '';
  rol: UserRole | '' = '';

  ngOnInit(): void {
    this.loadEmployees();
  }

  loadEmployees(): void {
    this.employeesService.getEmployees(this.texto, this.rol || undefined).subscribe({
      next: employees => {
        this.employees.set(employees);
      },
      error: err => {
        console.error(err);
      }
    });
  }

  edit(employee: Employee): void {
    this.editEmployee.emit(employee);
  }

  delete(employee: Employee): void {
    this.deleteEmployee.emit(employee);  
  }

  search(): void {
    this.loadEmployees();
  }

  hasActiveFilters(): boolean {
    return !!this.texto || !!this.rol
  }

}
