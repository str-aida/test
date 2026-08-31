package com.Trabajo_Final_Beltran.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class AplicarCuponRequest {

    @NotBlank(message = "El código del cupón es obligatorio")
    private String codigo;

    @NotNull(message = "El pedido es obligatorio")
    private Long pedidoId;
}