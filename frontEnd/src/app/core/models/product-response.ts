import { Estado } from "./enums/estado.enum";

export interface ProductResponse {

    id: number;
    nombre: string;
    descripcion: string;
    precio: number;
    categoriaNombre: string;
    estado: Estado;
    stock: number;
    imagenUrl: string;
    codigo: string;

}