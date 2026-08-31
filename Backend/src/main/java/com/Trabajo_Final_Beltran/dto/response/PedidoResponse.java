package com.Trabajo_Final_Beltran.dto.response;

import com.Trabajo_Final_Beltran.enums.EstadoPago;
import com.Trabajo_Final_Beltran.enums.EstadoPedido;
import com.Trabajo_Final_Beltran.enums.MetodoPago;
import com.Trabajo_Final_Beltran.enums.TipoEntrega;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
public class PedidoResponse {

  private Long id;

  private String nombreCliente;

  private String telefonoCliente;

  private LocalDateTime fechaHora;

  private BigDecimal total;

  private EstadoPedido estado;

  private TipoEntrega tipoEntrega;

  private MetodoPago metodoPago;

  private EstadoPago estadoPago;

  private String numeroPedido;

}
