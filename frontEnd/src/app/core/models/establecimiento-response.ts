import { TipoServicio } from './enums/tipo-servicio.enum';
import { DiaSemana } from './enums/dia-semana.enum';
import { Estado } from './enums/estado.enum';
import { Direccion } from './direccion.model';

export interface EstablecimientoResponse {
    id: number,
    nombre: string;
    razonSocial: string;
    cuit: string;
    email: string;
    telefono: string;
    direccion: Direccion;
    horarioApertura: string;
    horarioCierre: string;
    diasHabiles: DiaSemana[];
    descripcion: string;
    tipoServicio: TipoServicio;
    estado: Estado;
    fechaCreacion: string;
}