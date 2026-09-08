
package com.Trabajo_Final_Beltran.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ProductoDescuentoRequest {
    @NotNull
    private Long productoId;

    @NotNull
    @DecimalMin(value = "0.01") @DecimalMax(value = "100.00")
    private BigDecimal porcentaje;
}