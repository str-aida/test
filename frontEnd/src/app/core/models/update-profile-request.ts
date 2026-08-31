import { Direccion } from './direccion.model';

export interface UpdateProfileRequest {

    nombre: string;
    apellido: string;
    telefono: string;
    fechaNacimiento: string;
    direccion: Direccion;

}