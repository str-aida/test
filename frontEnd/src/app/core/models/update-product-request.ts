import { Estado } from "./enums/estado.enum";

export interface UpdateProductRequest {

    nombre: string;
    descripcion?: string;
    precio: number;
    categoriaId: number;
    estado: Estado;
    stock?: number;
    eliminarImagen?: boolean;
    codigo?: string;

}