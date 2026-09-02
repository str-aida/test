package com.Trabajo_Final_Beltran.dto.response;

import com.Trabajo_Final_Beltran.enums.TipoServicio;
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
public class EstablecimientoClienteResponse {

  private Long id;
  private String nombre;
  private TipoServicio tipoServicio;
}
