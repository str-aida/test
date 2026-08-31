import { Component, ViewChild} from '@angular/core';
import { EmployeeFormComponent } from '../../components/employee-form/employee-form';
import { EmployeesTableComponent } from '../../components/employees-table/employees-table';

@Component({
  selector: 'app-employees-list',
  imports: [EmployeeFormComponent, EmployeesTableComponent],
  templateUrl: './employees-list.html',
  styleUrl: './employees-list.scss',
})
export class EmployeesListComponent {

  @ViewChild(EmployeesTableComponent)
  employeesTable?: EmployeesTableComponent;

  onEmployeeCreated(): void {

    if (this.mostrarListado) {
      this.employeesTable?.loadEmployees();
    }

  }

  mostrarListado = false;
  listadoCargado = false;

  toggleListado(): void {

    this.mostrarListado = !this.mostrarListado;

    if (this.mostrarListado) {

      this.listadoCargado = true;

      setTimeout(() => {
        this.employeesTable?.loadEmployees();
      });

    }

  }

}
