import { ChangeDetectorRef, Component, inject, OnInit, PLATFORM_ID } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { CuponService } from '../../../../core/services/cupon.service';
import { CuponUsuarioResponse } from '../../../../core/models/cupon-usuario-response';
import { TipoDescuento } from '../../../../core/models/enums/tipo-descuento.enum';
import {
  LucideTicket,
  LucideCheckCircle,
  LucideAlertTriangle,
  LucideRefreshCw,
  LucideTag,
  LucideInfo
} from '@lucide/angular';

@Component({
  selector: 'app-mis-cupones',
  imports: [
    CommonModule,
    LucideTicket,
    LucideCheckCircle,
    LucideAlertTriangle,
    LucideRefreshCw,
    LucideTag,
    LucideInfo
  ],
  templateUrl: './mis-cupones.html',
  styleUrl: './mis-cupones.scss',
})
export class MisCuponesComponent implements OnInit {
  private readonly platformId = inject(PLATFORM_ID);
  private readonly cuponService = inject(CuponService);
  private readonly cdr = inject(ChangeDetectorRef);

  cuponesUsuario: CuponUsuarioResponse[] = [];
  isLoading = false;
  hasError = false;

  readonly TipoDescuentoEnum = TipoDescuento;

  ngOnInit(): void {
    if (isPlatformBrowser(this.platformId)) {
      this.loadMisCupones();
    }
  }

  loadMisCupones(): void {
    this.isLoading = true;
    this.hasError = false;
    this.cdr.detectChanges();

    this.cuponService.misCupones().subscribe({
      next: (data) => {
        this.cuponesUsuario = data || [];
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error al cargar mis cupones:', err);
        this.isLoading = false;
        this.hasError = true;
        this.cdr.detectChanges();
      }
    });
  }

  formatValor(tipo: TipoDescuento, valor: number): string {
    if (tipo === TipoDescuento.PORCENTAJE) {
      return `${valor}% OFF`;
    }
    return `$${valor} OFF`;
  }
}
