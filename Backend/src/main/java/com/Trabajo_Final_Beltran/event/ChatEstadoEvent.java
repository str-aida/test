package com.Trabajo_Final_Beltran.event;

import com.Trabajo_Final_Beltran.dto.response.ChatConversacionResponse;

public record ChatEstadoEvent(
        Long conversacionId, 
        ChatConversacionResponse respuesta) {
}