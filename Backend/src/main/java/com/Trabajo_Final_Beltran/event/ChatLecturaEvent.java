package com.Trabajo_Final_Beltran.event;

import com.Trabajo_Final_Beltran.dto.response.ChatLecturaResponse;

public record ChatLecturaEvent(
        Long conversacionId,
        ChatLecturaResponse lectura) {
}
