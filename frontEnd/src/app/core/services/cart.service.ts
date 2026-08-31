import { computed, Injectable, signal } from '@angular/core';
import { ProductResponse } from '../models/product-response';

export interface CartItem {
    producto: ProductResponse;
    cantidad: number;
}

/**
 * Servicio reactivo de carrito de compras.
 * Gestiona el estado del carrito en memoria usando Angular Signals.
 * No persiste entre sesiones (intencional para esta versión).
 *
 * Regla de stock: nunca se permite agregar más unidades que producto.stock.
 * El método agregar() retorna true si se agregó, false si el stock ya estaba agotado.
 */
@Injectable({
    providedIn: 'root'
})
export class CartService {

    private readonly _items = signal<CartItem[]>([]);

    /** Lista de ítems del carrito (readonly) */
    readonly items = this._items.asReadonly();

    /** Cantidad total de ítems (suma de cantidades) */
    readonly totalItems = computed(() =>
        this._items().reduce((acc, item) => acc + item.cantidad, 0)
    );

    /** Subtotal antes de descuentos */
    readonly subtotal = computed(() =>
        this._items().reduce((acc, item) => acc + (item.producto.precio * item.cantidad), 0)
    );

    /** Si el carrito está vacío */
    readonly isEmpty = computed(() => this._items().length === 0);

    /**
     * Agrega un producto al carrito, respetando el límite de stock.
     * Si el producto no tiene stock (stock === 0) o la cantidad ya alcanzó el stock, no agrega.
     * @returns true si se agregó correctamente, false si no se pudo por límite de stock.
     */
    agregar(producto: ProductResponse): boolean {
        const stockDisponible = producto.stock ?? 0;

        // Sin stock disponible: no agregar
        if (stockDisponible <= 0) {
            return false;
        }

        const cantidadActual = this.getCantidad(producto.id);

        // Ya se alcanzó el máximo de stock
        if (cantidadActual >= stockDisponible) {
            return false;
        }

        this._items.update(items => {
            const existente = items.find(i => i.producto.id === producto.id);
            if (existente) {
                return items.map(i =>
                    i.producto.id === producto.id
                        ? { ...i, cantidad: i.cantidad + 1, producto }
                        : i
                );
            }
            return [...items, { producto, cantidad: 1 }];
        });

        return true;
    }

    /**
     * Elimina una unidad de un producto.
     * Si la cantidad llega a 0, lo remueve del carrito.
     */
    reducir(productoId: number): void {
        this._items.update(items =>
            items
                .map(i =>
                    i.producto.id === productoId
                        ? { ...i, cantidad: i.cantidad - 1 }
                        : i
                )
                .filter(i => i.cantidad > 0)
        );
    }

    /**
     * Elimina completamente un producto del carrito.
     */
    eliminar(productoId: number): void {
        this._items.update(items =>
            items.filter(i => i.producto.id !== productoId)
        );
    }

    /**
     * Establece una cantidad específica para un producto, respetando el límite de stock.
     * Si la cantidad es 0 o negativa, elimina el ítem.
     * Si la cantidad supera el stock, la limita al stock disponible.
     * @returns La cantidad efectivamente asignada.
     */
    setCantidad(productoId: number, cantidad: number): number {
        if (cantidad <= 0) {
            this.eliminar(productoId);
            return 0;
        }

        // Buscar el producto en el carrito para conocer su stock
        const item = this._items().find(i => i.producto.id === productoId);
        if (!item) return 0;

        const stockDisponible = item.producto.stock ?? 0;
        if (stockDisponible <= 0) {
            this.eliminar(productoId);
            return 0;
        }

        const cantidadEfectiva = Math.min(cantidad, stockDisponible);

        this._items.update(items =>
            items.map(i =>
                i.producto.id === productoId
                    ? { ...i, cantidad: cantidadEfectiva }
                    : i
            )
        );

        return cantidadEfectiva;
    }

    /**
     * Vacía el carrito completamente.
     */
    vaciar(): void {
        this._items.set([]);
    }

    /**
     * Verifica si un producto está en el carrito.
     */
    contiene(productoId: number): boolean {
        return this._items().some(i => i.producto.id === productoId);
    }

    /**
     * Obtiene la cantidad de un producto en el carrito.
     * Retorna 0 si no está en el carrito.
     */
    getCantidad(productoId: number): number {
        return this._items().find(i => i.producto.id === productoId)?.cantidad ?? 0;
    }

    /**
     * Verifica si un producto alcanzó su límite de stock en el carrito.
     */
    stockAgotado(producto: ProductResponse): boolean {
        if ((producto.stock ?? 0) <= 0) return true;
        return this.getCantidad(producto.id) >= producto.stock;
    }
}
