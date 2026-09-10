package com.Trabajo_Final_Beltran.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CrearChatRequest {
    @NotNull
    private Long pedidoId;
}