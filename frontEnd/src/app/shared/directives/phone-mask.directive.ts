import { Directive, HostListener, inject } from '@angular/core';
import { NgControl } from '@angular/forms';

@Directive({
  selector: '[appPhoneMask]'
})
export class PhoneMaskDirective {

  private readonly ngControl = inject(NgControl);

  @HostListener('input', ['$event'])
  onInput(event: Event): void {

    const input = event.target as HTMLInputElement;

    let value = input.value.replace(/\D/g, '');

    value = value.substring(0, 10);

    if (value.length > 2) {
      value = value.replace(/^(\d{2})(\d)/, '$1-$2');
    }

    if (value.length > 7) {
      value = value.replace(/^(\d{2})-(\d{4})(\d+)/, '$1-$2-$3');
    }

    input.value = value;

    this.ngControl.control?.setValue(value, {
      emitEvent: false
    });

  }

}