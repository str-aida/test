import { Direccion } from "./direccion.model";
import { Estado } from "./enums/estado.enum";
import { UserRole } from "./enums/user-role.enum";

export interface Employee {

    id: number;

    nombre: string;
    apellido: string;
    email: string;
    telefono: string;
    dni: string;
    fechaNacimiento: string;
    direccion: Direccion;
    rol: UserRole;
    estado: Estado;

}