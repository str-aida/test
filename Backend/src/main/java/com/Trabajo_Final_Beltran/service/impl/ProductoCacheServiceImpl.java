package com.Trabajo_Final_Beltran.service.impl;

import com.Trabajo_Final_Beltran.dto.response.ProductoResponse;
import com.Trabajo_Final_Beltran.entity.Producto;
import com.Trabajo_Final_Beltran.enums.EstadoProducto;
import com.Trabajo_Final_Beltran.mapper.ProductoMapper;
import com.Trabajo_Final_Beltran.repository.ProductoRepository;
import com.Trabajo_Final_Beltran.service.ProductoCacheService;
import com.Trabajo_Final_Beltran.specification.ProductoSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoCacheServiceImpl implements ProductoCacheService {

    private final ProductoRepository productoRepository;

    @Override
    @Cacheable(
        value = "productos",
        key = "#establecimientoId + '-' + #categoriaId + '-' + #estado + '-' + #texto"
    )
    public List<ProductoResponse> buscarProductosFiltrados(
            Long establecimientoId,
            Long categoriaId,
            EstadoProducto estado,
            String texto
    ) {
        Specification<Producto> spec = Specification
                .where(
                        ProductoSpecification.establecimientoId(establecimientoId)
                );

        if (categoriaId != null) {
            spec = spec.and(ProductoSpecification.categoriaId(categoriaId));
        }

        if (estado != null) {
            spec = spec.and(ProductoSpecification.estado(estado));
        }

        if (texto != null && !texto.isBlank()) {
            spec = spec.and(ProductoSpecification.texto(texto));
        }

        List<Producto> productos =
                productoRepository.findAll(spec);

        return productos.stream()
                .map(ProductoMapper::toResponse)
                .toList();
    }
}