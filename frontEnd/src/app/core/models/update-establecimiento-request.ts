import { DiaSemana } from "./enums/dia-semana.enum";
import { TipoServicio } from "./enums/tipo-servicio.enum";
import { UpdateDireccionRequest } from "./update-direccion-request";

export interface UpdateEstablecimientoRequest {
    nombre: string;
    razonSocial: string;
    email: string;
    telefono: string;
    direccion: UpdateDireccionRequest;
    horarioApertura: string;
    horarioCierre: string;
    diasHabiles: DiaSemana[];
    descripcion: string;
    tipoServicio: TipoServicio;
}