package com.Trabajo_Final_Beltran.dto.response;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.Trabajo_Final_Beltran.enums.EstadoProducto;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoAnaliticaResponse {

  private Long idProducto;

  private String nombreProducto;

  private Long cantidadVendida;

  private BigDecimal totalVendido;

  private EstadoProducto estado;
}