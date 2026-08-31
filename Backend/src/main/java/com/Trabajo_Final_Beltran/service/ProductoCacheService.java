package com.Trabajo_Final_Beltran.service;

import com.Trabajo_Final_Beltran.dto.response.ProductoResponse;
import com.Trabajo_Final_Beltran.enums.EstadoProducto;

import java.util.List;

public interface ProductoCacheService {

    List<ProductoResponse> buscarProductosFiltrados(
            Long establecimientoId,
            Long categoriaId,
            EstadoProducto estado,
            String texto
    );
}