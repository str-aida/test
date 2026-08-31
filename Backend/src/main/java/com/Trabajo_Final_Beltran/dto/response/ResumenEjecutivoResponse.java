package com.Trabajo_Final_Beltran.dto.response;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumenEjecutivoResponse {

  private Long totalPedidos;

  private BigDecimal ventasTotales;

  private BigDecimal ticketPromedio;

  private Long pedidosPendientes;

  private Long pedidosEntregados;

  private Long clientesRegistrados;

}