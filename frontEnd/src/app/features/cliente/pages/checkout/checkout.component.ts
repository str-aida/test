import { ChangeDetectorRef, Component, inject, OnInit, PLATFORM_ID, signal } from '@angular/core';
import { CommonModule, DecimalPipe, isPlatformBrowser } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import {
  LucideArrowLeft,
  LucideArrowRight,
  LucideTruck,
  LucideStore,
  LucideCreditCard,
  LucideBanknote,
  LucideTicket,
  LucideMapPin,
  LucideCheckCircle,
  LucideImage,
  LucidePlus,
  LucideX
} from '@lucide/angular';
import { CartService } from '../../../../core/services/cart.service';
import { PedidoService } from '../../../../core/services/pedido.service';
import { CuponService } from '../../../../core/services/cupon.service';
import { DireccionService } from '../../../../core/services/direccion.service';
import { PagoService } from '../../../../core/services/pago.service';
import { NotificationService } from '../../../../core/services/notification.service';
import { EstablecimientoService } from '../../../../core/services/establecimiento.service';
import { TipoServicio } from '../../../../core/models/enums/tipo-servicio.enum';
import { TipoEntrega } from '../../../../core/models/enums/tipo-entrega.enum';
import { MetodoPago } from '../../../../core/models/enums/metodo-pago.enum';
import { DireccionResponse } from '../../../../core/models/direccion-response';
import { CreateDireccionRequest } from '../../../../core/models/create-direccion-request';
import { CuponUsuarioResponse } from '../../../../core/models/cupon-usuario-response';
import { CreatePedidoRequest } from '../../../../core/models/create-pedido-request';
import { TipoDescuento } from '../../../../core/models/enums/tipo-descuento.enum';
import { EstadoCupon } from '../../../../core/models/enums/estado-cupon.enum';
import { environment } from '../../../../../environments/environment';

@Component({
  selector: 'app-checkout',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterLink,
    DecimalPipe,
    LucideArrowLeft,
    LucideArrowRight,
    LucideTruck,
    LucideStore,
    LucideCreditCard,
    LucideBanknote,
    LucideTicket,
    LucideMapPin,
    LucideCheckCircle,
    LucideImage,
    LucidePlus,
    LucideX
  ],
  templateUrl: './checkout.component.html',
  styleUrl: './checkout.component.scss'
})
export class CheckoutComponent implements OnInit {

  protected readonly cartService = inject(CartService);
  private readonly pedidoService = inject(PedidoService);
  private readonly cuponService = inject(CuponService);
  private readonly direccionService = inject(DireccionService);
  private readonly establecimientoService = inject(EstablecimientoService);
  private readonly pagoService = inject(PagoService);
  private readonly notificationService = inject(NotificationService);
  private readonly router = inject(Router);
  private readonly cdr = inject(ChangeDetectorRef);

  // Enums para template
  protected readonly TipoEntrega = TipoEntrega;
  protected readonly TipoServicio = TipoServicio;
  protected readonly MetodoPago = MetodoPago;
  protected readonly TipoDescuento = TipoDescuento;

  // Opciones de checkout
  tipoEntrega: TipoEntrega = TipoEntrega.DELIVERY;
  tipoServicioEstablecimiento: TipoServicio | null = null;
  direccionIdSelected: number | null = null;
  metodoPago: MetodoPago = MetodoPago.EFECTIVO;

  // Direcciones
  direcciones: DireccionResponse[] = [];
  isLoadingDirecciones = false;

  // Formulario para crear dirección nueva inline
  mostrarFormNuevaDireccion = false;
  isSavingDireccion = false;
  nuevaDireccionForm: CreateDireccionRequest = {
    nombre: '',
    calle: '',
    numero: '',
    localidad: '',
    piso: '',
    departamento: '',
    codigoPostal: '',
    referencia: '',
    esPrincipal: false
  };

  // Cupones
  misCupones: CuponUsuarioResponse[] = [];
  codigoCuponIngresado = '';
  cuponSeleccionado: CuponUsuarioResponse | null = null;
  isLoadingCupones = false;

  // Submitting state
  isSubmitting = signal<boolean>(false);

  private readonly platformId = inject(PLATFORM_ID);

  ngOnInit(): void {
    if (!isPlatformBrowser(this.platformId)) {
      return;
    }

    if (this.cartService.isEmpty()) {
      this.router.navigate(['/cliente/carrito']);
      return;
    }

    this.cargarEstablecimiento();
    this.cargarDirecciones();
    this.cargarCupones();
  }

  cargarEstablecimiento(): void {
    this.establecimientoService.obtenerInfoClienteActual().subscribe({
      next: (est) => {
        this.tipoServicioEstablecimiento = est.tipoServicio;
        if (est.tipoServicio === TipoServicio.DELIVERY) {
          this.tipoEntrega = TipoEntrega.DELIVERY;
        } else if (est.tipoServicio === TipoServicio.RETIRO) {
          this.tipoEntrega = TipoEntrega.RETIRO;
        }
        this.cdr.markForCheck();
      },
      error: () => {
        // Fallback en caso de error de red
        this.tipoServicioEstablecimiento = null;
        this.cdr.markForCheck();
      }
    });
  }

  cargarDirecciones(selectId?: number): void {
    this.isLoadingDirecciones = true;
    this.direccionService.listarDirecciones().pipe(
      finalize(() => {
        this.isLoadingDirecciones = false;
        this.cdr.markForCheck();
      })
    ).subscribe({
      next: (dirs) => {
        this.direcciones = dirs;
        if (dirs.length === 0) {
          // Si no tiene ninguna dirección guardada, abrir formulario de creación directamente
          this.mostrarFormNuevaDireccion = true;
          this.direccionIdSelected = null;
        } else {
          // Si hay direcciones, cerrar formulario si estaba abierto
          this.mostrarFormNuevaDireccion = false;
          if (selectId && dirs.some(d => d.id === selectId)) {
            this.direccionIdSelected = selectId;
          } else {
            const principal = dirs.find(d => d.esPrincipal);
            if (principal) {
              this.direccionIdSelected = principal.id;
            } else {
              this.direccionIdSelected = dirs[0].id;
            }
          }
        }
      },
      error: (err) => {
        console.error('Error al cargar direcciones:', err);
      }
    });
  }

  toggleFormNuevaDireccion(mostrar?: boolean): void {
    this.mostrarFormNuevaDireccion = mostrar !== undefined ? mostrar : !this.mostrarFormNuevaDireccion;
    if (this.mostrarFormNuevaDireccion) {
      this.nuevaDireccionForm = {
        nombre: '',
        calle: '',
        numero: '',
        localidad: '',
        piso: '',
        departamento: '',
        codigoPostal: '',
        referencia: '',
        esPrincipal: this.direcciones.length === 0
      };
    }
    this.cdr.markForCheck();
  }

  guardarNuevaDireccion(): void {
    if (!this.nuevaDireccionForm.calle.trim()) {
      this.notificationService.error('La calle es obligatoria.');
      return;
    }
    if (!this.nuevaDireccionForm.numero.trim()) {
      this.notificationService.error('El número es obligatorio.');
      return;
    }
    if (!this.nuevaDireccionForm.localidad.trim()) {
      this.notificationService.error('La localidad es obligatoria.');
      return;
    }

    this.isSavingDireccion = true;
    this.cdr.markForCheck();

    const request: CreateDireccionRequest = {
      nombre: this.nuevaDireccionForm.nombre?.trim() || undefined,
      calle: this.nuevaDireccionForm.calle.trim(),
      numero: this.nuevaDireccionForm.numero.trim(),
      localidad: this.nuevaDireccionForm.localidad.trim(),
      piso: this.nuevaDireccionForm.piso?.trim() || undefined,
      departamento: this.nuevaDireccionForm.departamento?.trim() || undefined,
      codigoPostal: this.nuevaDireccionForm.codigoPostal?.trim() || undefined,
      referencia: this.nuevaDireccionForm.referencia?.trim() || undefined,
      esPrincipal: this.nuevaDireccionForm.esPrincipal || false
    };

    this.direccionService.crearDireccion(request).pipe(
      finalize(() => {
        this.isSavingDireccion = false;
        this.cdr.markForCheck();
      })
    ).subscribe({
      next: (nuevaDir) => {
        this.notificationService.success('Dirección creada con éxito');
        // Refrescar lista de direcciones desde el Backend y seleccionar la nueva dirección
        this.cargarDirecciones(nuevaDir.id);
      },
      error: (err) => {
        const msg = err.error?.message || err.error?.mensaje || 'Error al guardar la dirección. Verificá los campos.';
        this.notificationService.error(msg);
      }
    });
  }

  cargarCupones(): void {
    this.isLoadingCupones = true;
    this.cuponService.misCupones().subscribe({
      next: (cupones) => {
        // Solo cupones no usados y activos
        this.misCupones = (cupones || []).filter(c => !c.usado && c.cupon && c.cupon.estado === EstadoCupon.ACTIVO);
        this.isLoadingCupones = false;
      },
      error: (err) => {
        console.error('Error al cargar cupones:', err);
        this.isLoadingCupones = false;
      }
    });
  }

  seleccionarCuponDeLista(cuponUsuario: CuponUsuarioResponse): void {
    if (this.cuponSeleccionado?.id === cuponUsuario.id) {
      this.cuponSeleccionado = null;
      this.codigoCuponIngresado = '';
    } else {
      this.cuponSeleccionado = cuponUsuario;
      this.codigoCuponIngresado = cuponUsuario.cupon.codigo;
    }
  }

  limpiarCupon(): void {
    this.cuponSeleccionado = null;
    this.codigoCuponIngresado = '';
  }

  getCodigoCuponFinal(): string {
    if (this.cuponSeleccionado) {
      return this.cuponSeleccionado.cupon.codigo;
    }
    return this.codigoCuponIngresado.trim();
  }

  // Estimación visual del descuento antes de confirmar (para la UI)
  getDescuentoEstimado(): number {
    const subtotal = this.cartService.subtotal();
    const codigo = this.getCodigoCuponFinal();

    if (!codigo) return 0;

    // Si se seleccionó de la lista, calculamos según su tipoDescuento
    if (this.cuponSeleccionado) {
      const c = this.cuponSeleccionado.cupon;
      if (c.tipoDescuento === TipoDescuento.PORCENTAJE) {
        return (subtotal * c.valor) / 100;
      } else if (c.tipoDescuento === TipoDescuento.MONTO) {
        return Math.min(subtotal, c.valor);
      }
    }
    return 0;
  }

  getTotalEstimado(): number {
    const subtotal = this.cartService.subtotal();
    const desc = this.getDescuentoEstimado();
    return Math.max(0, subtotal - desc);
  }

  confirmarPedido(): void {
    if (this.cartService.isEmpty()) {
      this.notificationService.error('El carrito está vacío');
      return;
    }

    if (this.tipoEntrega === TipoEntrega.DELIVERY && !this.direccionIdSelected) {
      this.notificationService.error('Debés seleccionar una dirección para la entrega a domicilio');
      return;
    }

    if (this.isSubmitting()) {
      return; // Prevenir doble envío
    }

    this.isSubmitting.set(true);

    const request: CreatePedidoRequest = {
      tipoEntrega: this.tipoEntrega,
      direccionId: this.tipoEntrega === TipoEntrega.DELIVERY ? this.direccionIdSelected : null,
      metodoPago: this.metodoPago,
      detalles: this.cartService.items().map(item => ({
        productoId: item.producto.id,
        cantidad: item.cantidad
      }))
    };

    const codigoCupon = this.getCodigoCuponFinal();

    // 1. Crear el pedido
    this.pedidoService.crearPedido(request).subscribe({
      next: (pedidoCreado) => {
        const pedidoId = pedidoCreado.id;
        const numeroPedido = pedidoCreado.numeroPedido || String(pedidoId);

        // 2. Si se especificó un código de cupón, intentamos aplicarlo
        if (codigoCupon) {
          this.pedidoService.aplicarCupon(pedidoId, { codigo: codigoCupon, pedidoId }).subscribe({
            next: (resCupon) => {
              if (resCupon.valido) {
                this.notificationService.success(`¡Cupón aplicado con éxito!`);
              } else {
                this.notificationService.error(`Pedido #${numeroPedido} creado, pero el cupón no pudo aplicarse: ${resCupon.mensaje}`);
              }
              // 3. Crear el pago
              this.procesarPago(pedidoId, numeroPedido);
            },
            error: (errCupon) => {
              const msgErr = errCupon.error?.message || errCupon.error?.mensaje || 'no válido o vencido';
              this.notificationService.error(`Pedido #${numeroPedido} creado con éxito, pero el cupón no se aplicó (${msgErr}).`);
              // 3. Crear el pago
              this.procesarPago(pedidoId, numeroPedido);
            }
          });
        } else {
          // 3. Crear el pago directamente
          this.procesarPago(pedidoId, numeroPedido);
        }
      },
      error: (errPedido) => {
        this.isSubmitting.set(false);
        const mensaje = errPedido.error?.message || errPedido.error?.mensaje || 'Error al procesar el pedido. Verificá los datos e intentá nuevamente.';
        this.notificationService.error(mensaje);
      }
    });
  }

  /**
   * Invoca el endpoint obligatorio del backend POST /pagos/{pedidoId}.
   * Si es TARJETA y se obtiene urlPago, redirige al usuario a Mercado Pago.
   * Si es EFECTIVO o no hay urlPago, notifica y redirige al detalle del pedido.
   */
  private procesarPago(pedidoId: number, numeroPedido: string): void {
    this.pagoService.crearPago(pedidoId).subscribe({
      next: (pagoRes) => {
        if (this.metodoPago === MetodoPago.TARJETA && pagoRes.urlPago) {
          this.notificationService.success(`Pedido #${numeroPedido} creado. Redirigiendo a Mercado Pago...`);
          this.cartService.vaciar();
          this.isSubmitting.set(false);
          // Redirección externa a Mercado Pago
          window.location.href = pagoRes.urlPago;
        } else {
          this.notificationService.success(`¡Pedido #${numeroPedido} registrado con éxito!`);
          this.finalizarExitoso(pedidoId);
        }
      },
      error: (errPago) => {
        console.error('Error al registrar pago:', errPago);
        const msgErr = errPago.error?.message || errPago.error?.mensaje || 'No se pudo generar el pago automático';
        this.notificationService.error(`Pedido #${numeroPedido} creado, pero ocurrió un aviso en el pago: ${msgErr}`);
        this.finalizarExitoso(pedidoId);
      }
    });
  }

  private finalizarExitoso(pedidoId: number): void {
    this.cartService.vaciar();
    this.isSubmitting.set(false);
    this.router.navigate(['/cliente/pedidos', pedidoId]);
  }

  getImageUrl(imagenUrl: string | null): string | null {
    if (!imagenUrl) return null;
    if (imagenUrl.startsWith('http://') || imagenUrl.startsWith('https://')) return imagenUrl;
    return `${environment.baseUrl}${imagenUrl}`;
  }
}
