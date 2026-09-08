package com.Trabajo_Final_Beltran.service;

import com.Trabajo_Final_Beltran.dto.response.ProductoResponse;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface DescuentoResolverService {

    Map<Long, BigDecimal> obtenerDescuentosVigentes(Long establecimientoId);

    List<ProductoResponse> aplicarDescuentos(List<ProductoResponse> productos, Long establecimientoId);
}