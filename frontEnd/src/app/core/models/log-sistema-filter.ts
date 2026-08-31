import { UserRole } from './enums/user-role.enum';

export interface LogSistemaFilter {
  accion?: string;
  rol?: UserRole;
  usuario?: string;
}
