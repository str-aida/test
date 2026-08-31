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
public class ClienteInactivoResponse {

  private Long idCliente;

  private String nombreCompleto;

  private String email;

  private LocalDateTime ultimaCompra;

  private BigDecimal montoUltimaCompra;

  private BigDecimal totalGastado;

  private Long cantidadPedidos;

}