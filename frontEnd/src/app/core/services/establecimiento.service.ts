import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { EstablecimientoResponse } from '../models/establecimiento-response';
import { EstablecimientoClienteResponse } from '../models/establecimiento-cliente-response';
import { UpdateEstablecimientoRequest } from '../models/update-establecimiento-request';

@Injectable({
    providedIn: 'root'
})
export class EstablecimientoService {
    private readonly http = inject(HttpClient);

    obtenerEstablecimiento(): Observable<EstablecimientoResponse> {
        return this.http.get<EstablecimientoResponse>(
            `${environment.baseUrl}/establecimiento`
        );
    }

    obtenerInfoClienteActual(): Observable<EstablecimientoClienteResponse> {
        return this.http.get<EstablecimientoClienteResponse>(
            `${environment.baseUrl}/establecimiento/info`
        );
    }

    actualizarEstablecimiento(data: UpdateEstablecimientoRequest): Observable<EstablecimientoResponse> {
        return this.http.put<EstablecimientoResponse>(
            `${environment.baseUrl}/establecimiento`,
            data
        );
    }
}