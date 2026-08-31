import { Estado } from "./enums/estado.enum";

export interface UpdateEmployeeRequest {

    nombre: string;
    apellido: string;
    telefono: string;
    email: string;
    estado: Estado;

}