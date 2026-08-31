package com.Trabajo_Final_Beltran.dto.request;

import com.Trabajo_Final_Beltran.enums.MetodoPago;
import com.Trabajo_Final_Beltran.enums.TipoEntrega;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
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
public class CreatePedidoRequest {
  @NotNull(message = "El tipo de entrega es obligatorio")
  private TipoEntrega tipoEntrega;

  private Long direccionId;

  @NotEmpty(message = "Debe incluir al menos un producto")
  @Valid
  private List<CreateDetallePedidoRequest> detalles;

  @NotNull(message = "El método de pago es obligatorio")
  private MetodoPago metodoPago;
}
