import { Estado } from "./enums/estado.enum";

export interface CategoriaResponse{

    id: number;
    nombre: string;
    descripcion: string;
    estado: Estado;

}