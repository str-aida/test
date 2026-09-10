package com.Trabajo_Final_Beltran.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EnviarMensajeRequest {
    @NotBlank
    @Size(max = 2000)
    private String contenido;
}