import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';

import { EstablishmentSetup } from '../models/establishment-setup';
import { EstablishmentSetupResponse } from '../models/establishment-setup-response';

@Injectable({
    providedIn: 'root'
})
export class EstablishmentService {

    private readonly http = inject(HttpClient);

    crearEstablecimiento(data: EstablishmentSetup): Observable<EstablishmentSetupResponse> {

        return this.http.post<EstablishmentSetupResponse>(
            `${environment.baseUrl}/setup/establecimiento`,
            data
        );

    }

}