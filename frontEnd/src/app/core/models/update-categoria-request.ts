import { Estado } from "./enums/estado.enum";

export interface UpdateCategoriaRequest {

    nombre: string;
    descripcion: string;
    estado: Estado;

}