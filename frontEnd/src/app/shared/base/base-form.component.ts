import { FormGroup } from '@angular/forms';

/*
* Clase base para formularios reactivos.
*
* Centraliza la lógica reutilizable de validación y manejo de errores
* para evitar duplicar código en los distintos componentes.
*/

export abstract class BaseFormComponent {

  /*
  * Formulario principal del componente.
  * Cada componente hijo debe devolver su propio FormGroup.
  */
  protected abstract get form(): FormGroup;

  /*
  * Indica si un campo es inválido y ya fue interactuado
  * (touched o dirty).
  */
  isInvalid(controlName: string): boolean {

    const control = this.form.get(controlName);

    return !!control &&
      control.invalid &&
      (control.touched || control.dirty);

  }

  /*
  * Verifica si un campo posee un error específico.
  *
  * Ejemplo:
  * hasError('email', 'required')
  * hasError('cuit', 'maxlength')
  */
  hasError(controlName: string, errorName: string): boolean {

    const control = this.form.get(controlName);

    return !!control &&
      control.hasError(errorName) &&
      (control.touched || control.dirty);

  }

  /*
  * Verifica si el formulario posee un error a nivel de FormGroup.
  *
  * Se utiliza para validaciones que dependen de varios campos,
  * como comparar horarios o fechas.
  */
  hasFormError(errorName: string): boolean {

    return this.form.hasError(errorName);

  }

  /*
  * Marca todos los controles del formulario como touched,
  * forzando la visualización de los mensajes de error.
  *
  * Se utiliza generalmente al enviar un formulario inválido.
  */
  markFormAsTouched(): void {

    this.form.markAllAsTouched();

  }

}