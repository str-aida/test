import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { LoginRequest } from '../models/login-request';
import { AdminSetup } from '../models/admin-setup.model';
import { ForgotPasswordRequest } from '../models/forgot-password-request';
import { ResetPasswordRequest } from '../models/reset-password-request';
import { AuthResponse } from '../models/auth-reponse.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly http = inject(HttpClient);

  login(datos: LoginRequest): Observable<AuthResponse> {

    return this.http.post<AuthResponse>(
      `${environment.baseUrl}/auth/login`,
      datos
    );

  }

  logout() {
    return this.http.post(
      `${environment.baseUrl}/auth/logout`,
      {},
      {
        responseType: 'text'
      }
    );
  }

  registerAdmin(data: AdminSetup): Observable<AuthResponse> {
  
    return this.http.post<AuthResponse>(
      `${environment.baseUrl}/auth/registro-admin`,
      data
    );
  
  }

  registerCliente(data: AdminSetup): Observable<AuthResponse> {

    return this.http.post<AuthResponse>(
      `${environment.baseUrl}/auth/registro-cliente`,
      data
    );

  }

  crearPersonal(data: AdminSetup): Observable<string> {

    return this.http.post(
      `${environment.baseUrl}/auth/crear-personal`,
      data,
      {
        responseType: 'text'
      }
    );

  }

  solicitarRecuperacionPassword(request: ForgotPasswordRequest) {

    return this.http.post(
      `${environment.baseUrl}/auth/solicitar-recuperacion`,
      request,
      {
        responseType: 'text'
      }
    );

  }

  restablecerPassword(request: ResetPasswordRequest) {

    return this.http.post(
      `${environment.baseUrl}/auth/restablecer-password`,
      request,
      {
        responseType: 'text'
      }
    );

  }

}