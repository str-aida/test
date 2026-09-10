package com.Trabajo_Final_Beltran.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatLecturaResponse {
    private Long conversacionId;
    private Long lectorId;
    private LocalDateTime fechaLectura;
}
