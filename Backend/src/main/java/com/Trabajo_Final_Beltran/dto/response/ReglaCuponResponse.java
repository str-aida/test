package com.Trabajo_Final_Beltran.dto.response;

import com.Trabajo_Final_Beltran.enums.TipoAsignacionCupon;
import com.Trabajo_Final_Beltran.enums.TipoDescuento;
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
public class ReglaCuponResponse {

    private Long id;
    private TipoAsignacionCupon tipoAsignacion;
    private Boolean activo;
    private TipoDescuento tipoDescuento;
    private BigDecimal valor;
    private Integer diasValidez;
    private Integer cantidadComprasRequeridas;
    private String descripcion;
}
