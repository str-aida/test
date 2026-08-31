import { Direccion } from './direccion.model';
import { UserRole } from './enums/user-role.enum';

export interface AdminSetup {

    nombre: string;
    apellido: string;
    email: string;
    password: string;
    telefono: string;
    dni: string;
    fechaNacimiento: string;
    direccion: Direccion;
    rol?: UserRole;

}