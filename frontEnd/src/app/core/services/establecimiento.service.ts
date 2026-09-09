import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { EstablecimientoResponse } from '../models/establecimiento-response';
import { EstablecimientoClienteResponse } from '../models/establecimiento-cliente-response';
import { UpdateEstablecimientoRequest } from '../models/update-establecimiento-request';
import { TokenService } from './token.service';
import { UserRole } from '../models/enums/user-role.enum';

export interface EstablishmentBrandInfo {
    nombre: string;
    logoUrl?: string | null;
}

@Injectable({
    providedIn: 'root'
})
export class EstablecimientoService {
    private readonly http = inject(HttpClient);
    private readonly tokenService = inject(TokenService);

    private readonly _brandInfo = signal<EstablishmentBrandInfo | null>(null);
    readonly brandInfo = this._brandInfo.asReadonly();

    obtenerEstablecimiento(): Observable<EstablecimientoResponse> {
        return this.http.get<EstablecimientoResponse>(
            `${environment.baseUrl}/establecimiento`
        ).pipe(
            tap(res => {
                this._brandInfo.set({
                    nombre: res.nombre,
                    logoUrl: res.logoUrl
                });
            })
        );
    }

    obtenerInfoClienteActual(): Observable<EstablecimientoClienteResponse> {
        return this.http.get<EstablecimientoClienteResponse>(
            `${environment.baseUrl}/establecimiento/info`
        ).pipe(
            tap(res => {
                this._brandInfo.set({
                    nombre: res.nombre,
                    logoUrl: null
                });
            })
        );
    }

    loadBrandInfo(): Observable<any> {
        const role = this.tokenService.getRole();
        if (role === UserRole.ADMIN) {
            return this.obtenerEstablecimiento();
        } else {
            return this.obtenerInfoClienteActual();
        }
    }

    actualizarEstablecimiento(data: UpdateEstablecimientoRequest): Observable<EstablecimientoResponse> {
        return this.http.put<EstablecimientoResponse>(
            `${environment.baseUrl}/establecimiento`,
            data
        ).pipe(
            tap(res => {
                this._brandInfo.set({
                    nombre: res.nombre,
                    logoUrl: res.logoUrl
                });
            })
        );
    }

    actualizarLogo(logo: File): Observable<EstablecimientoResponse> {
        const formData = new FormData();
        formData.append('logo', logo);
        return this.http.put<EstablecimientoResponse>(
            `${environment.baseUrl}/establecimiento/logo`,
            formData
        ).pipe(
            tap(res => {
                this._brandInfo.set({
                    nombre: res.nombre,
                    logoUrl: res.logoUrl
                });
            })
        );
    }
}