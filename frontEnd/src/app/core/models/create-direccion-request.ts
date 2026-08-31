export interface CreateDireccionRequest {
    nombre?: string;
    calle: string;
    numero: string;
    localidad: string;
    piso?: string;
    departamento?: string;
    codigoPostal?: string;
    referencia?: string;
    esPrincipal?: boolean;
}
