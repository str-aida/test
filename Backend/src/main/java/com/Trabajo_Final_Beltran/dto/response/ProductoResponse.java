package com.Trabajo_Final_Beltran.dto.response;

import com.Trabajo_Final_Beltran.enums.EstadoProducto;
import java.io.Serializable;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoResponse implements Serializable {

    private Long id;

    private String nombre;

    private String descripcion;

    private BigDecimal precio;

    private String categoriaNombre;

    private EstadoProducto estado;

    private Integer stock;

    private String imagenUrl;

    private String codigo;

    private Long categoriaId;
}