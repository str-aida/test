/**
 * Respuesta paginada del backend (PageResponse<T>).
 * Campos exactos devueltos por el backend.
 */
export interface PageResponse<T> {
    content: T[];
    pagina: number;
    size: number;
    totalElementos: number;
    totalPaginas: number;
    primera: boolean;
    ultima: boolean;
}
