package com.Trabajo_Final_Beltran.dto.response;


import com.Trabajo_Final_Beltran.enums.MetodoPago;
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
public class VentaMetodoPagoResponse {

  private MetodoPago metodoPago;

  private Long cantidadPedidos;

  private BigDecimal ventasTotales;

}