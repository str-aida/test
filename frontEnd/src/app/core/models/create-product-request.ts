export interface CreateProductRequest {

    nombre: string;
    descripcion?: string;
    precio: number;
    categoriaId: number;
    stock?: number;
    codigo?: string;

}