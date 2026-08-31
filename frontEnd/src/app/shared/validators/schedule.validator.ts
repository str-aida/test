import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export const scheduleValidator: ValidatorFn = (
  control: AbstractControl
): ValidationErrors | null => {

  const opening = control.get('horarioApertura')?.value;
  const closing = control.get('horarioCierre')?.value;

  // Si alguno está vacío, dejamos que Validators.required
  // muestre su propio mensaje.
  if (!opening || !closing) {
    return null;
  }

  return opening < closing
    ? null
    : { invalidSchedule: true };

};