import { Component, inject, OnInit } from '@angular/core';
import { EstablecimientoService } from '../../../core/services/establecimiento.service';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-brand',
  standalone: true,
  imports: [],
  templateUrl: './brand.html',
  styleUrl: './brand.scss',
})
export class BrandComponent implements OnInit {

  private readonly establecimientoService = inject(EstablecimientoService);

  readonly brandInfo = this.establecimientoService.brandInfo;

  ngOnInit(): void {
    if (!this.brandInfo()) {
      this.establecimientoService.loadBrandInfo().subscribe({
        error: (err) => console.error('Error al cargar la identidad del establecimiento:', err)
      });
    }
  }

  get logoUrl(): string {
    const info = this.brandInfo();
    if (info && info.logoUrl) {
      if (info.logoUrl.startsWith('http://') || info.logoUrl.startsWith('https://')) {
        return info.logoUrl;
      }
      return `${environment.baseUrl}${info.logoUrl}`;
    }
    return 'images/logo/gestia-isotype-light.svg';
  }

  get nombre(): string {
    return this.brandInfo()?.nombre || '';
  }

}
