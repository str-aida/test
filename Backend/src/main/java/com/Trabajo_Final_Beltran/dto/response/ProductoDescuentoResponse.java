package com.Trabajo_Final_Beltran.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoDescuentoResponse {
    private Long productoId;
    private String nombreProducto;
    private BigDecimal porcentaje;
}
