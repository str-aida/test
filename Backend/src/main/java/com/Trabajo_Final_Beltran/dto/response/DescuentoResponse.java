package com.Trabajo_Final_Beltran.dto.response;

import com.Trabajo_Final_Beltran.enums.EstadoDescuento;
import com.Trabajo_Final_Beltran.enums.TipoCampanaDescuento;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DescuentoResponse {
    private Long id;
    private String nombre;
    private TipoCampanaDescuento tipo;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private EstadoDescuento estado;
    private List<ProductoDescuentoResponse> productos;
}