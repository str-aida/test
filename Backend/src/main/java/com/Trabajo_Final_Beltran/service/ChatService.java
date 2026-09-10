package com.Trabajo_Final_Beltran.service;

import com.Trabajo_Final_Beltran.dto.request.EnviarMensajeRequest;
import com.Trabajo_Final_Beltran.dto.response.ChatConversacionResponse;
import com.Trabajo_Final_Beltran.dto.response.ChatMensajeResponse;
import com.Trabajo_Final_Beltran.entity.ChatConversacion;
import com.Trabajo_Final_Beltran.entity.Usuario;
import com.Trabajo_Final_Beltran.enums.EstadoConversacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChatService {

    ChatConversacion obtenerConversacionAutorizada(Long conversacionId, Usuario usuario);

    ChatConversacionResponse crearOReutilizarChat(Long pedidoId);

    ChatConversacionResponse obtenerDetalle(Long conversacionId);

    Page<ChatMensajeResponse> obtenerMensajes(Long conversacionId, Pageable pageable);

    Page<ChatConversacionResponse> listarPorEstablecimiento(EstadoConversacion estado, Pageable pageable);

    ChatMensajeResponse enviarMensaje(Long conversacionId, EnviarMensajeRequest request, Usuario usuario);
    
    ChatMensajeResponse enviarMensaje(Long conversacionId, EnviarMensajeRequest request);
    
    ChatConversacionResponse asignar(Long conversacionId);

    ChatConversacionResponse cerrar(Long conversacionId);

    com.Trabajo_Final_Beltran.dto.response.ChatLecturaResponse marcarMensajesComoLeidos(Long conversacionId, Usuario usuario);
}