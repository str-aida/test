package com.Trabajo_Final_Beltran.mapper;

import com.Trabajo_Final_Beltran.dto.response.ChatConversacionResponse;
import com.Trabajo_Final_Beltran.dto.response.ChatMensajeResponse;
import com.Trabajo_Final_Beltran.entity.ChatConversacion;
import com.Trabajo_Final_Beltran.entity.ChatMensaje;

public final class ChatMapper {

    private ChatMapper() {}

    public static ChatConversacionResponse toResponse(ChatConversacion c, long mensajesNoLeidos) {
        return ChatConversacionResponse.builder()
                .id(c.getId())
                .pedidoId(c.getPedido().getId())
                .numeroPedido(c.getPedido().getNumeroPedido())
                .clienteId(c.getCliente().getId())
                .nombreCliente(c.getCliente().getNombre() + " " + c.getCliente().getApellido())
                .agenteId(c.getAgente() != null ? c.getAgente().getId() : null)
                .nombreAgente(c.getAgente() != null ? c.getAgente().getNombre() + " " + c.getAgente().getApellido() : null)
                .estado(c.getEstado())
                .cerradoPor(c.getCerradoPor())
                .fechaCreacion(c.getFechaCreacion())
                .mensajesNoLeidos(mensajesNoLeidos)
                .build();
    }

    public static ChatMensajeResponse toResponse(ChatMensaje m) {
        return ChatMensajeResponse.builder()
                .id(m.getId())
                .remitenteId(m.getRemitente().getId())
                .nombreRemitente(m.getRemitente().getNombre() + " " + m.getRemitente().getApellido())
                .rolRemitente(m.getRemitente().getRol().name())
                .esSistema(m.isEsSistema())
                .contenido(m.getContenido())
                .fechaEnvio(m.getFechaEnvio())
                .leido(m.isLeido())
                .build();
    }
}