import { Directive, ElementRef, forwardRef, HostListener, inject } from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';

@Directive({
  selector: '[appDniMask]',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => DniMaskDirective),
      multi: true
    }
  ]
})
export class DniMaskDirective implements ControlValueAccessor {

  private readonly elementRef = inject(ElementRef<HTMLInputElement>);

  private onChange: (value: string) => void = () => {};
  private onTouched: () => void = () => {};

  @HostListener('input', ['$event'])
  onInput(event: Event): void {

    const input = event.target as HTMLInputElement;

    // Valor real: solamente dígitos
    let rawValue = input.value.replace(/\D/g, '');

    // Máximo 9 dígitos
    rawValue = rawValue.substring(0, 9);

    // Valor visual
    const formattedValue = rawValue.replace(
      /\B(?=(\d{3})+(?!\d))/g,
      '.'
    );

    // Mostrar puntos
    input.value = formattedValue;

    // Informar a Angular solamente los dígitos
    this.onChange(rawValue);
  }

  @HostListener('blur')
  onBlur(): void {
    this.onTouched();
  }

  writeValue(value: string | null): void {

    const rawValue = (value ?? '')
      .replace(/\D/g, '')
      .substring(0, 9);

    const formattedValue = rawValue.replace(
      /\B(?=(\d{3})+(?!\d))/g,
      '.'
    );

    this.elementRef.nativeElement.value = formattedValue;
  }

  registerOnChange(fn: (value: string) => void): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: () => void): void {
    this.onTouched = fn;
  }

  setDisabledState(isDisabled: boolean): void {
    this.elementRef.nativeElement.disabled = isDisabled;
  }
}