import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { environment } from "../../../environments/environment";
import { Observable } from "rxjs";
import { Employee } from "../models/user-profile-response";
import { UpdateProfileRequest } from "../models/update-profile-request";
import { UpdatePasswordRequest } from "../models/update-password-request";

@Injectable({
  providedIn: 'root'
})
export class ProfileService {

    private readonly http = inject(HttpClient);

    private readonly apiUrl = `${environment.baseUrl}/perfil`;

    /**
   * Obtiene la información del usuario autenticado.
   * GET /perfil
   */
    getProfile(): Observable<Employee> {
        return this.http.get<Employee>(this.apiUrl);
    }

    /**
   * Actualiza la información del usuario autenticado.
   * PUT /perfil
   */
    updateProfile(request: UpdateProfileRequest): Observable<Employee> {
        return this.http.put<Employee>(
            this.apiUrl,
            request
        );
    }

    /**
   * Cambia la contraseña del usuario autenticado.
   * PUT /perfil/password
   */
    changePassword(request: UpdatePasswordRequest): Observable<string> {
        return this.http.put(
            `${this.apiUrl}/password`,
            request,
            {
                responseType: 'text'
            }
        );
    }

    /**
   * Envía un email para restablecer la contraseña.
   * POST /perfil/password
   */
    requestPasswordReset(): Observable<void> {
        return this.http.post<void>(
            `${this.apiUrl}/password`,
            {}
        );
    }

}