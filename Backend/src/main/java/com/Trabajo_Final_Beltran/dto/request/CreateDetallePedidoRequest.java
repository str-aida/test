package com.Trabajo_Final_Beltran.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
public class CreateDetallePedidoRequest {

  @NotNull(message = "El producto es obligatorio")
  private Long productoId;

  @NotNull(message = "La cantidad es obligatoria")
  @Min(value = 1, message = "La cantidad debe ser mayor a cero")
  private Integer cantidad;
}
