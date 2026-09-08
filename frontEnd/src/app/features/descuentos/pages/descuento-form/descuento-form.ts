import { ChangeDetectorRef, Component, EventEmitter, inject, Input, OnChanges, OnInit, Output, PLATFORM_ID, SimpleChanges } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { FormArray, FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { forkJoin } from 'rxjs';
import { DescuentoService } from '../../../../core/services/descuento.service';
import { ProductsService } from '../../../../core/services/products.service';
import { NotificationService } from '../../../../core/services/notification.service';
import { ProductResponse } from '../../../../core/models/product-response';
import { DescuentoResponse } from '../../../../core/models/descuento-response';
import { CreateDescuentoRequest } from '../../../../core/models/create-descuento-request';
import { UpdateDescuentoRequest } from '../../../../core/models/update-descuento-request';
import { TipoCampanaDescuento } from '../../../../core/models/enums/tipo-campana-descuento.enum';
import { EstadoDescuento } from '../../../../core/models/enums/estado-descuento.enum';
import { BaseFormComponent } from '../../../../shared/base/base-form.component';
import {
  LucideArrowLeft,
  LucidePercent,
  LucideTag,
  LucideMegaphone,
  LucideSearch,
  LucidePlus,
  LucideTrash2,
  LucideCheck,
  LucideTrendingUp,
  LucideSparkles
} from '@lucide/angular';

interface ProductoItemSeleccionado {
  productoId: number;
  nombre: string;
  categoriaNombre?: string;
  imagenUrl?: string;
  precio: number;
  porcentaje: number;
}

@Component({
  selector: 'app-descuento-form',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    LucideArrowLeft,
    LucidePercent,
    LucideTag,
    LucideMegaphone,
    LucideSearch,
    LucidePlus,
    LucideTrash2,
    LucideCheck,
    LucideTrendingUp,
    LucideSparkles
  ],
  templateUrl: './descuento-form.html',
  styleUrl: './descuento-form.scss'
})
export class DescuentoFormComponent extends BaseFormComponent implements OnInit, OnChanges {
  private readonly fb = inject(FormBuilder);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly descuentoService = inject(DescuentoService);
  private readonly productsService = inject(ProductsService);
  private readonly notificationService = inject(NotificationService);
  private readonly cdr = inject(ChangeDetectorRef);
  private readonly platformId = inject(PLATFORM_ID);

  @Input() isModal = false;
  @Input() editingDescuento: DescuentoResponse | null = null;
  @Output() descuentoCreated = new EventEmitter<void>();
  @Output() descuentoUpdated = new EventEmitter<void>();
  @Output() cancel = new EventEmitter<void>();

  isEditing = false;
  descuentoId: number | null = null;
  currentDescuento: DescuentoResponse | null = null;

  isLoading = false;
  isSubmitting = false;

  // Catálogo completo de productos disponibles
  todosLosProductos: ProductResponse[] = [];
  productosFiltradosParaAgregar: ProductResponse[] = [];
  searchProductoTerm = '';
  showDropdownProductos = false;

  readonly TipoCampanaEnum = TipoCampanaDescuento;
  readonly EstadoDescuentoEnum = EstadoDescuento;

  readonly tipoCampanaOptions = [
    { value: TipoCampanaDescuento.DIA_FESTIVO, label: 'Día Festivo' },
    { value: TipoCampanaDescuento.TEMPORADA, label: 'Temporada' },
    { value: TipoCampanaDescuento.LIQUIDACION, label: 'Liquidación' },
    { value: TipoCampanaDescuento.GENERAL, label: 'General' }
  ];

  readonly estadoOptions = [
    { value: EstadoDescuento.ACTIVO, label: 'Activo' },
    { value: EstadoDescuento.INACTIVO, label: 'Inactivo' }
  ];

  readonly descuentoForm: FormGroup = this.fb.group({
    nombre: ['', [Validators.required, Validators.maxLength(100)]],
    tipo: [TipoCampanaDescuento.DIA_FESTIVO, [Validators.required]],
    fechaInicio: ['', [Validators.required]],
    fechaFin: ['', [Validators.required]],
    estado: [EstadoDescuento.ACTIVO],
    productos: this.fb.array([])
  });

  protected override get form(): FormGroup {
    return this.descuentoForm;
  }

  get productosArray(): FormArray {
    return this.descuentoForm.get('productos') as FormArray;
  }

  ngOnInit(): void {
    if (isPlatformBrowser(this.platformId)) {
      if (this.editingDescuento) {
        this.isEditing = true;
        this.descuentoId = this.editingDescuento.id;
        this.cargarDatosEdicion(this.descuentoId);
      } else if (!this.isModal) {
        this.determinarModo();
      } else {
        this.cargarCatalogoProductos();
      }
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['editingDescuento']) {
      if (this.editingDescuento) {
        this.isEditing = true;
        this.descuentoId = this.editingDescuento.id;
        this.cargarDatosEdicion(this.descuentoId);
      } else if (this.isModal) {
        this.isEditing = false;
        this.descuentoId = null;
        this.currentDescuento = null;
        this.descuentoForm.reset({
          nombre: '',
          tipo: TipoCampanaDescuento.DIA_FESTIVO,
          fechaInicio: '',
          fechaFin: '',
          estado: EstadoDescuento.ACTIVO
        });
        this.productosArray.clear();
        this.cargarCatalogoProductos();
        this.cdr.markForCheck();
      }
    }
  }

  private determinarModo(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.isEditing = true;
      this.descuentoId = Number(idParam);
      this.cargarDatosEdicion(this.descuentoId);
    } else {
      this.cargarCatalogoProductos();
    }
  }

  private cargarCatalogoProductos(): void {
    this.productsService.listarProductos().subscribe({
      next: (prods) => {
        this.todosLosProductos = prods || [];
        this.filtrarProductosCatalogo();
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Error al cargar productos:', err);
      }
    });
  }

  private cargarDatosEdicion(id: number): void {
    this.isLoading = true;
    this.cdr.markForCheck();

    forkJoin({
      productos: this.productsService.listarProductos(),
      descuento: this.descuentoService.obtenerDescuentoPorId(id)
    }).subscribe({
      next: ({ productos, descuento }) => {
        this.todosLosProductos = productos || [];
        this.currentDescuento = descuento;
        this.descuentoForm.patchValue({
          nombre: descuento.nombre,
          tipo: descuento.tipo,
          fechaInicio: descuento.fechaInicio ? String(descuento.fechaInicio).split('T')[0] : '',
          fechaFin: descuento.fechaFin ? String(descuento.fechaFin).split('T')[0] : '',
          estado: descuento.estado
        });

        this.poblarProductosDescuento(descuento);
        this.isLoading = false;
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Error al cargar descuento y productos:', err);
        this.isLoading = false;
        this.notificationService.error('No se pudo cargar el descuento solicitado.');
        this.volver();
      }
    });
  }

  private poblarProductosDescuento(descuento: DescuentoResponse): void {
    this.productosArray.clear();
    descuento.productos?.forEach((p) => {
      const prodInfo = this.todosLosProductos.find((prod) => prod.id === p.productoId);
      this.agregarProductoAlFormulario({
        productoId: p.productoId,
        nombre: p.nombreProducto || prodInfo?.nombre || `Producto #${p.productoId}`,
        categoriaNombre: prodInfo?.categoriaNombre || 'Carta',
        imagenUrl: prodInfo?.imagenUrl,
        precio: Number(prodInfo?.precio) || 0,
        porcentaje: Number(p.porcentaje) || 10
      });
    });
    this.filtrarProductosCatalogo();
    this.cdr.markForCheck();
  }

  filtrarProductosCatalogo(): void {
    const term = this.searchProductoTerm.trim().toLowerCase();
    const idsYaIncluidos = new Set(this.productosArray.value.map((p: any) => p.productoId));

    this.productosFiltradosParaAgregar = this.todosLosProductos
      .filter((p) => !idsYaIncluidos.has(p.id))
      .filter((p) => {
        if (!term) return true;
        return (
          p.nombre.toLowerCase().includes(term) ||
          p.codigo?.toLowerCase().includes(term) ||
          p.categoriaNombre?.toLowerCase().includes(term)
        );
      });
  }

  onSearchProductoInput(): void {
    this.filtrarProductosCatalogo();
    this.showDropdownProductos = true;
  }

  abrirSelectorProductos(): void {
    this.filtrarProductosCatalogo();
    this.showDropdownProductos = true;
  }

  seleccionarProducto(producto: ProductResponse): void {
    this.agregarProductoAlFormulario({
      productoId: producto.id,
      nombre: producto.nombre,
      categoriaNombre: producto.categoriaNombre,
      imagenUrl: producto.imagenUrl,
      precio: Number(producto.precio) || 0,
      porcentaje: 10 // Porcentaje inicial predeterminado
    });
    this.searchProductoTerm = '';
    this.showDropdownProductos = false;
    this.filtrarProductosCatalogo();
  }

  agregarProductoAlFormulario(item: ProductoItemSeleccionado): void {
    const group = this.fb.group({
      productoId: [item.productoId, [Validators.required]],
      nombre: [item.nombre],
      categoriaNombre: [item.categoriaNombre],
      imagenUrl: [item.imagenUrl],
      precio: [Number(item.precio) || 0],
      porcentaje: [
        Number(item.porcentaje) || 10,
        [Validators.required, Validators.min(0.01), Validators.max(100)]
      ]
    });

    this.productosArray.push(group);
    this.cdr.markForCheck();
  }

  /** Clave estable para @for: evita desalineamiento de controles al re-render */
  trackByProductoId(_index: number, ctrl: any): number {
    return ctrl.get('productoId')?.value ?? _index;
  }

  onPorcentajeChange(): void {
    this.cdr.markForCheck();
  }

  eliminarProducto(index: number): void {
    this.productosArray.removeAt(index);
    this.filtrarProductosCatalogo();
    this.cdr.markForCheck();
  }

  getPrecioFinalConDescuento(precio: any, porcentaje: any): number {
    const numPrecio = Number(precio) || 0;
    const numPorcentaje = Number(porcentaje);
    if (numPrecio <= 0) return 0;
    if (isNaN(numPorcentaje) || numPorcentaje <= 0) return numPrecio;
    const desc = (numPrecio * numPorcentaje) / 100;
    return Math.max(0, numPrecio - desc);
  }

  get promedioRebaja(): number {
    if (this.productosArray.length === 0) return 0;
    const total = this.productosArray.controls.reduce(
      (sum, ctrl) => sum + (Number(ctrl.get('porcentaje')?.value) || 0),
      0
    );
    return Math.round(total / this.productosArray.length);
  }

  volver(): void {
    if (this.isModal) {
      this.cancel.emit();
    } else {
      const basePath = this.router.url.includes('/empleado') ? '/empleado/descuentos' : '/admin/descuentos';
      this.router.navigate([basePath]);
    }
  }

  onSubmit(): void {
    if (this.descuentoForm.invalid) {
      this.markFormAsTouched();
      if (this.productosArray.length === 0) {
        this.notificationService.error('Debes incluir al menos un producto en la campaña.');
      }
      return;
    }

    if (this.productosArray.length === 0) {
      this.notificationService.error('Debes incluir al menos un producto en la campaña.');
      return;
    }

    this.isSubmitting = true;
    // getRawValue() garantiza leer todos los controles del FormArray
    // sin excepción (value omite disabled/invalid y puede devolver null)
    const formVal = this.descuentoForm.getRawValue();

    const productosPayload = (formVal.productos as any[]).map((p) => ({
      productoId: Number(p.productoId),
      porcentaje: Number(p.porcentaje)
    }));

    if (this.isEditing && this.descuentoId) {
      const updateReq: UpdateDescuentoRequest = {
        nombre: formVal.nombre.trim(),
        tipo: formVal.tipo,
        fechaInicio: formVal.fechaInicio,
        fechaFin: formVal.fechaFin,
        productos: productosPayload
      };

      this.descuentoService.editarDescuento(this.descuentoId, updateReq).subscribe({
        next: () => {
          this.isSubmitting = false;
          this.notificationService.success('Descuento actualizado correctamente.');
          if (this.isModal) {
            this.descuentoUpdated.emit();
          } else {
            this.volver();
          }
        },
        error: (err) => {
          console.error('Error al editar descuento:', err);
          this.isSubmitting = false;
          const msg = err?.error?.message || err?.error?.mensaje || 'Error al actualizar el descuento.';
          this.notificationService.error(msg);
          this.cdr.markForCheck();
        }
      });
    } else {
      const createReq: CreateDescuentoRequest = {
        nombre: formVal.nombre.trim(),
        tipo: formVal.tipo,
        fechaInicio: formVal.fechaInicio,
        fechaFin: formVal.fechaFin,
        productos: productosPayload
      };

      this.descuentoService.crearDescuento(createReq).subscribe({
        next: () => {
          this.isSubmitting = false;
          this.notificationService.success('Descuento creado correctamente.');
          if (this.isModal) {
            this.descuentoCreated.emit();
          } else {
            this.volver();
          }
        },
        error: (err) => {
          console.error('Error al crear descuento:', err);
          this.isSubmitting = false;
          const msg = err?.error?.message || err?.error?.mensaje || 'Error al crear el descuento.';
          this.notificationService.error(msg);
          this.cdr.markForCheck();
        }
      });
    }
  }
}
