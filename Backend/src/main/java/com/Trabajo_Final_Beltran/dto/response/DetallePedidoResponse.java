
package com.Trabajo_Final_Beltran.dto.response;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DetallePedidoResponse {

  private Long productoId;

  private String nombreProducto;

  private Integer cantidad;

  private BigDecimal precioUnitario;

  private BigDecimal subtotal;
}
