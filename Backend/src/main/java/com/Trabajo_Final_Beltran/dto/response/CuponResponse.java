package com.Trabajo_Final_Beltran.dto.response;

import com.Trabajo_Final_Beltran.enums.EstadoCupon;
import com.Trabajo_Final_Beltran.enums.TipoDescuento;
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
public class CuponResponse {

    private Long id;
    private String codigo;
    private TipoDescuento tipoDescuento;
    private BigDecimal valor;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Integer usoMaximo;
    private Integer usosActuales;
    private EstadoCupon estado;
}