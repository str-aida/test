package com.Trabajo_Final_Beltran.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMensajeResponse {
    private Long id;
    private Long remitenteId;
    private String nombreRemitente;
    private String rolRemitente;
    private boolean esSistema;
    private String contenido;
    private LocalDateTime fechaEnvio;
    private boolean leido;
}