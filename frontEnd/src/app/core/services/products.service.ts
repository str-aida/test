import { HttpClient, HttpParams } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { environment } from "../../../environments/environment";
import { Observable } from "rxjs";
import { ProductResponse } from "../models/product-response";
import { CreateProductRequest } from "../models/create-product-request";
import { UpdateProductRequest } from "../models/update-product-request";
import { Estado } from "../models/enums/estado.enum";

@Injectable({
  providedIn: 'root'
})
export class ProductsService {

    private readonly http = inject(HttpClient);
    private readonly apiUrl = `${environment.baseUrl}/productos`;

    listarProductos(
        categoriaId?: number,
        estado?: Estado,
        texto?: string
    ): Observable<ProductResponse[]>{

        let params = new HttpParams();

        if (categoriaId !== undefined) {
        params = params.set('categoriaId', categoriaId);
        }
        
        if (estado) {
        params = params.set('estado', estado);
        }

        if (texto?.trim()) {
        params = params.set('texto', texto.trim());
        }
        
        return this.http.get<ProductResponse[]>(
            this.apiUrl,
            { params }
        );
    }

    crearProducto(request: CreateProductRequest, imagen: File | null): Observable<ProductResponse> {

        const formData = new FormData();

        formData.append(
            'producto',
            new Blob (
                [JSON.stringify(request)],
                { type: 'application/json' }
            )
        );

        if (imagen) {
            formData.append('imagen', imagen);
        }

        return this.http.post<ProductResponse>(
            this.apiUrl,
            formData
        );

    }

    editarProducto(id: number, request: UpdateProductRequest): Observable<ProductResponse> {
        return this.http.put<ProductResponse>(
            `${this.apiUrl}/${id}`,
            request
        );
    }

}