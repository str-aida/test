package com.Trabajo_Final_Beltran.dto.response;

import com.Trabajo_Final_Beltran.enums.EstadoPedido;
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
public class EstadoPedidoResponse {

  private EstadoPedido estado;

  private Long cantidad;

}