package com.Trabajo_Final_Beltran.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
public class ClienteAnaliticaResponse {

  private Long id;

  private String nombreCompleto;

  private Long cantidadPedidos;

  private BigDecimal totalGastado;

  private LocalDateTime ultimaCompra;

}