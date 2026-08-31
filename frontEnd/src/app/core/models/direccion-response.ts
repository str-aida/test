export interface DireccionResponse {
    id: number;
    nombre: string | null;
    calle: string;
    numero: string;
    localidad: string;
    piso: string | null;
    departamento: string | null;
    codigoPostal: string | null;
    referencia: string | null;
    esPrincipal: boolean;
}
