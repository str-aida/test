package com.Trabajo_Final_Beltran.mapper;

import com.Trabajo_Final_Beltran.dto.request.CreateProductoRequest;
import com.Trabajo_Final_Beltran.dto.request.UpdateProductoRequest;
import com.Trabajo_Final_Beltran.dto.response.ProductoResponse;
import com.Trabajo_Final_Beltran.entity.Producto;


public class ProductoMapper {

    public static Producto toEntity(
            CreateProductoRequest request,
            String imagenUrl
    ) {

        return Producto.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .precio(request.getPrecio())
                .stock(request.getStock())
                .imagenUrl(imagenUrl)
                .codigo(request.getCodigo())
                .build();
    }

    public static ProductoResponse toResponse(
            Producto producto
    ) {

        return ProductoResponse.builder()
                .id(producto.getId())
                .nombre(producto.getNombre())
                .descripcion(producto.getDescripcion())
                .precio(producto.getPrecio())
                .categoriaNombre(
                        producto.getCategoria().getNombre()
                )
                .estado(producto.getEstado())
                .stock(producto.getStock())
                .imagenUrl(producto.getImagenUrl())
                .codigo(producto.getCodigo())
                .categoriaId(producto.getCategoria().getId())
                .build();
    }

    public static void updateProductoFromRequest(
            Producto producto,
            UpdateProductoRequest request
    ) {

        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecio(request.getPrecio());
        producto.setEstado(request.getEstado());
        producto.setStock(request.getStock());
        producto.setCodigo(request.getCodigo());
    }
}