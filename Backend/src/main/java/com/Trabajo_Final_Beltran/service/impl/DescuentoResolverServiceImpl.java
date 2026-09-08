package com.Trabajo_Final_Beltran.service.impl;

import com.Trabajo_Final_Beltran.dto.response.ProductoResponse;
import com.Trabajo_Final_Beltran.entity.DescuentoProducto;
import com.Trabajo_Final_Beltran.mapper.DescuentoMapper;
import com.Trabajo_Final_Beltran.repository.DescuentoProductoRepository;
import com.Trabajo_Final_Beltran.service.DescuentoResolverService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DescuentoResolverServiceImpl implements DescuentoResolverService {

    private final DescuentoProductoRepository descuentoProductoRepository;

    @Override
    public Map<Long, BigDecimal> obtenerDescuentosVigentes(Long establecimientoId) {
        List<DescuentoProducto> vigentes =
                descuentoProductoRepository.findVigentesPorEstablecimiento(establecimientoId);

        return DescuentoMapper.toMapaPorcentajes(vigentes);
    }

    @Override
    public List<ProductoResponse> aplicarDescuentos(List<ProductoResponse> productos, Long establecimientoId) {
        Map<Long, BigDecimal> descuentos = obtenerDescuentosVigentes(establecimientoId);

        if (descuentos.isEmpty()) {
            return productos; 
        }

        productos.forEach(p -> aplicarDescuentoIndividual(p, descuentos));
        return productos;
    }

    private void aplicarDescuentoIndividual(ProductoResponse response, Map<Long, BigDecimal> descuentos) {
        BigDecimal porcentaje = descuentos.get(response.getId());
        if (porcentaje == null) {
            return;
        }

        BigDecimal factor = BigDecimal.ONE.subtract(
                porcentaje.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)
        );

        BigDecimal precioConDescuento = response.getPrecio()
                .multiply(factor)
                .setScale(2, RoundingMode.HALF_UP);

        response.setDescuentoPorcentaje(porcentaje);
        response.setPrecioConDescuento(precioConDescuento);
    }
}