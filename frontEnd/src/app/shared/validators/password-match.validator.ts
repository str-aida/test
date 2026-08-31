import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export function passwordMatchValidator(
    passwordControlName: string,
    confirmPasswordControlName: string
): ValidatorFn {

    return (form: AbstractControl): ValidationErrors | null => {

        const password = form.get(passwordControlName)?.value;
        const confirmPassword = form.get(confirmPasswordControlName)?.value;

        if (!password || !confirmPassword ) {

            return null;

        }

        return password === confirmPassword
            ? null
            : { passwordMismatch: true };

    };

}