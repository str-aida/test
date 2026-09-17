package com.Trabajo_Final_Beltran.dto.request;

import com.Trabajo_Final_Beltran.enums.TipoDescuento;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
public class UpdateReglaCuponRequest {

    @NotNull(message = "El estado activo es obligatorio")
    private Boolean activo;

    @NotNull(message = "El tipo de descuento es obligatorio")
    private TipoDescuento tipoDescuento;

    @NotNull(message = "El valor es obligatorio")
    @DecimalMin(value = "0.01", message = "El valor debe ser mayor a 0")
    private BigDecimal valor;

    @NotNull(message = "Los días de validez son obligatorios")
    @Min(value = 1, message = "Los días de validez deben ser al menos 1")
    private Integer diasValidez;

    @Min(value = 1, message = "La cantidad de compras requeridas debe ser al menos 1")
    private Integer cantidadComprasRequeridas;

    private String descripcion;
}
