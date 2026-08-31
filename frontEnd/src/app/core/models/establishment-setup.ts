import { TipoServicio } from './enums/tipo-servicio.enum';
import { DireccionEstablishment } from './direccion-establishment.model';
import { DiaSemana } from './enums/dia-semana.enum';

export interface EstablishmentSetup {

    nombre: string;
    razonSocial: string;
    cuit: string;
    email: string;
    telefono: string;
    direccion: DireccionEstablishment;
    horarioApertura: string;
    horarioCierre: string;
    diasHabiles: DiaSemana[];
    descripcion?: string;
    tipoServicio: TipoServicio;

}