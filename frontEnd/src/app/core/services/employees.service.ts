import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { Employee } from '../models/user-profile-response';
import { UserRole } from '../models/enums/user-role.enum';
import { UpdateEmployeeRequest } from '../models/update-user-request';

@Injectable({
  providedIn: 'root'
})
export class EmployeesService {

  private readonly http = inject(HttpClient);

  private readonly apiUrl = `${environment.baseUrl}/perfil/usuarios`;

  getEmployees(
    texto?: string,
    rol?: UserRole
  ): Observable<Employee[]> {

    let params = new HttpParams();

    if (texto?.trim()) {
      params = params.set('texto', texto.trim());
    }

    if (rol) {
      params = params.set('rol', rol);
    }

    return this.http.get<Employee[]>(this.apiUrl, { params });

  }

  updateEmployee(
    id: number,
    request: UpdateEmployeeRequest
  ): Observable<Employee> {

    return this.http.put<Employee>(
      `${this.apiUrl}/${id}`,
      request
    );

  }

}