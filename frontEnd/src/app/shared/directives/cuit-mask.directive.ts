import { Directive, HostListener, inject } from '@angular/core';
import { NgControl } from '@angular/forms';

@Directive({
  selector: '[appCuitMask]'
})
export class CuitMaskDirective {

  private readonly ngControl = inject(NgControl);

  @HostListener('input', ['$event'])
  onInput(event: Event): void {

    const input = event.target as HTMLInputElement;

    let value = input.value.replace(/\D/g, '');

    value = value.substring(0, 11);

    if (value.length > 2) {
      value = value.replace(/^(\d{2})(\d)/, '$1-$2');
    }

    if (value.length > 10) {
      value = value.replace(/^(\d{2})-(\d{8})(\d)/, '$1-$2-$3');
    }

    input.value = value;

    this.ngControl.control?.setValue(value, {
      emitEvent: false
    });

  }

}