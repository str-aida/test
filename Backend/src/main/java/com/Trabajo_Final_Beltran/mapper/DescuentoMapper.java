package com.Trabajo_Final_Beltran.mapper;

import com.Trabajo_Final_Beltran.dto.request.CreateDescuentoRequest;
import com.Trabajo_Final_Beltran.dto.request.ProductoDescuentoRequest;
import com.Trabajo_Final_Beltran.dto.request.UpdateDescuentoRequest;
import com.Trabajo_Final_Beltran.dto.response.DescuentoResponse;
import com.Trabajo_Final_Beltran.dto.response.ProductoDescuentoResponse;
import com.Trabajo_Final_Beltran.entity.Descuento;
import com.Trabajo_Final_Beltran.entity.DescuentoProducto;
import com.Trabajo_Final_Beltran.entity.Establecimiento;
import com.Trabajo_Final_Beltran.entity.Producto;
import com.Trabajo_Final_Beltran.enums.EstadoDescuento;

import java.util.List;
import java.util.Map;

public final class DescuentoMapper {

    private DescuentoMapper() {
    }

    public static Descuento toEntity(CreateDescuentoRequest request, Establecimiento establecimiento) {
        return Descuento.builder()
                .nombre(request.getNombre())
                .tipo(request.getTipo())
                .fechaInicio(request.getFechaInicio())
                .fechaFin(request.getFechaFin())
                .estado(EstadoDescuento.ACTIVO)
                .establecimiento(establecimiento)
                .build();
    }

    public static void updateFromRequest(Descuento descuento, UpdateDescuentoRequest request) {
        descuento.setNombre(request.getNombre());
        descuento.setTipo(request.getTipo());
        descuento.setFechaInicio(request.getFechaInicio());
        descuento.setFechaFin(request.getFechaFin());
    }

    public static DescuentoProducto toDescuentoProducto(
            ProductoDescuentoRequest request,
            Descuento descuento,
            Producto producto
    ) {
        return DescuentoProducto.builder()
                .descuento(descuento)
                .producto(producto)
                .porcentaje(request.getPorcentaje())
                .build();
    }

    public static DescuentoResponse toResponse(Descuento descuento) {
        List<ProductoDescuentoResponse> productos = descuento.getProductos().stream()
                .map(DescuentoMapper::toProductoDescuentoResponse)
                .toList();

        return DescuentoResponse.builder()
                .id(descuento.getId())
                .nombre(descuento.getNombre())
                .tipo(descuento.getTipo())
                .fechaInicio(descuento.getFechaInicio())
                .fechaFin(descuento.getFechaFin())
                .estado(descuento.getEstado())
                .productos(productos)
                .build();
    }

    private static ProductoDescuentoResponse toProductoDescuentoResponse(DescuentoProducto dp) {
        return ProductoDescuentoResponse.builder()
                .productoId(dp.getProducto().getId())
                .nombreProducto(dp.getProducto().getNombre())
                .porcentaje(dp.getPorcentaje())
                .build();
    }


    public static Map<Long, java.math.BigDecimal> toMapaPorcentajes(List<DescuentoProducto> vigentes) {
        return vigentes.stream()
                .collect(java.util.stream.Collectors.toMap(
                        dp -> dp.getProducto().getId(),
                        DescuentoProducto::getPorcentaje,
                        (a, b) -> a // en caso de duplicado por bug de datos, se queda con el primero
                ));
    }
}