package com.Trabajo_Final_Beltran.dto.request;

import com.Trabajo_Final_Beltran.enums.EstadoCupon;
import com.Trabajo_Final_Beltran.enums.TipoDescuento;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCuponRequest {

    @NotNull(message = "El tipo de descuento es obligatorio")
    private TipoDescuento tipoDescuento;

    @NotNull(message = "El valor es obligatorio")
    @DecimalMin(value = "0.01", message = "El valor debe ser mayor a 0")
    private BigDecimal valor;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDate fechaFin;

    @Min(value = 1, message = "El uso máximo debe ser al menos 1")
    private Integer usoMaximo;

    @NotNull(message = "El estado es obligatorio")
    private EstadoCupon estado;
}