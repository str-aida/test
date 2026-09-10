package com.Trabajo_Final_Beltran.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatErrorPayloadResponse {
    private String mensaje;
    private LocalDateTime fecha;
}