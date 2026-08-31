import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export function pastDateValidator(): ValidatorFn {

    return (control: AbstractControl): ValidationErrors | null => {

        if (!control.value) {

            return null;

        }

        const [year, month, day] = control.value
            .split('-')
            .map(Number);

        const selectedDate = new Date(year, month - 1, day);

        const today = new Date();

        // Ignoramos la hora para comparar solo la fecha
        today.setHours(0, 0, 0, 0);

        return selectedDate < today
            ? null
            : { pastDate: true };

    };

}