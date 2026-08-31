export interface Direccion {

    id?: number;
    nombre?: string;
    calle: string;
    numero: string;
    localidad: string;
    piso?: string;
    departamento?: string;
    codigoPostal?: string;
    referencia?: string;
    esPrincipal: boolean;

}