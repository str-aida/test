package com.Trabajo_Final_Beltran.controller;

import com.Trabajo_Final_Beltran.dto.request.EnviarMensajeRequest;
import com.Trabajo_Final_Beltran.dto.response.ChatErrorPayloadResponse;
import com.Trabajo_Final_Beltran.dto.response.ChatMensajeResponse;
import com.Trabajo_Final_Beltran.entity.Usuario;
import com.Trabajo_Final_Beltran.exception.BusinessException;
import com.Trabajo_Final_Beltran.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    private final com.Trabajo_Final_Beltran.repository.UsuarioRepository usuarioRepository;

    @MessageMapping("/chat/{conversacionId}/enviar")
    public void enviarMensaje(
            @DestinationVariable Long conversacionId,
            @Payload EnviarMensajeRequest request,
            SimpMessageHeaderAccessor accessor
    ) {
        Usuario usuario = null;
        if (accessor.getSessionAttributes() != null) {
            usuario = (Usuario) accessor.getSessionAttributes().get("usuario");
        }

        if (usuario == null && accessor.getUser() != null) {
            String email = accessor.getUser().getName();
            usuario = usuarioRepository.findByEmail(email).orElse(null);
        }

        if (usuario == null) {
            throw new BusinessException("Usuario no autenticado");
        }

        ChatMensajeResponse mensaje = chatService.enviarMensaje(conversacionId, request, usuario);

        messagingTemplate.convertAndSend(
            "/topic/chat/" + conversacionId + "/mensajes",
            mensaje );   
    }

    @MessageMapping("/chat/{conversacionId}/leer")
    public void marcarComoLeido(
            @DestinationVariable Long conversacionId,
            SimpMessageHeaderAccessor accessor
    ) {
        Usuario usuario = null;
        if (accessor.getSessionAttributes() != null) {
            usuario = (Usuario) accessor.getSessionAttributes().get("usuario");
        }

        if (usuario == null && accessor.getUser() != null) {
            String email = accessor.getUser().getName();
            usuario = usuarioRepository.findByEmail(email).orElse(null);
        }

        if (usuario == null) {
            throw new BusinessException("Usuario no autenticado");
        }

        chatService.marcarMensajesComoLeidos(conversacionId, usuario);
    }

    @MessageExceptionHandler(BusinessException.class)
    @SendToUser("/queue/errors")
    public ChatErrorPayloadResponse manejarError(BusinessException ex) {
        return ChatErrorPayloadResponse.builder()
                .mensaje(ex.getMessage())
                .fecha(LocalDateTime.now())
                .build();
    }
}