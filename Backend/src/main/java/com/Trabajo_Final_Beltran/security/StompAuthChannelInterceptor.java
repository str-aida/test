package com.Trabajo_Final_Beltran.security;

import com.Trabajo_Final_Beltran.entity.Usuario;
import com.Trabajo_Final_Beltran.exception.BusinessException;
import com.Trabajo_Final_Beltran.repository.UsuarioRepository;
import com.Trabajo_Final_Beltran.security.JwtService;
import com.Trabajo_Final_Beltran.security.TokenBlacklistService;
import com.Trabajo_Final_Beltran.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
@Slf4j
public class StompAuthChannelInterceptor implements ChannelInterceptor {

    private static final Pattern CHAT_TOPIC = Pattern.compile("/topic/chat/(\\d+)(?:/.*)?");
    
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;
    private final ChatService chatService;
    private final TokenBlacklistService tokenBlacklistService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand command = accessor.getCommand();

        if (command == null) {
            return message;
        }

        try {
            switch (command) {
                case CONNECT -> autenticar(accessor);
                case SUBSCRIBE -> autorizarSuscripcion(accessor);
                case SEND -> validarAutenticado(accessor);
                default -> { }
            }
        } catch (Exception ex) {
            log.error("Error en WebSocket STOMP command [{}]: {}", command, ex.getMessage(), ex);
            throw ex;
        }
        return message;
    }

    private void autenticar(StompHeaderAccessor accessor) {
        String token = accessor.getFirstNativeHeader("token");

        if (token == null || token.isBlank()) {
            token = accessor.getFirstNativeHeader("Authorization");
        }

        if (token == null || token.isBlank()) {
            throw new BusinessException("Token inválido o ausente en conexión WebSocket");
        }

        if (token.startsWith("Bearer ")) {
            token = token.substring(7).trim();
        }

        if (!jwtService.isTokenValid(token)) {
            throw new BusinessException("Token inválido o expirado");
        }

        if (tokenBlacklistService.estaInvalidado(token)) {
            throw new BusinessException("Token inválido: la sesión fue cerrada");
        }

        String email = jwtService.extractUsername(token);
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Usuario no encontrado"));

        List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name())
        );

        accessor.setUser(new UsernamePasswordAuthenticationToken(email, null, authorities));
        accessor.getSessionAttributes().put("usuario", usuario);
    }

    private void autorizarSuscripcion(StompHeaderAccessor accessor) {
        String destino = accessor.getDestination();
        Usuario usuario = (Usuario) accessor.getSessionAttributes().get("usuario");

        if (usuario == null) {
            throw new BusinessException("No autenticado");
        }
        if (destino == null) {
            throw new BusinessException("Destino de suscripción inválido");
        }

        if (destino.startsWith("/user/")) {
            return;
        }

        Matcher matcher = CHAT_TOPIC.matcher(destino);
        if (matcher.matches()) {
            Long conversacionId = Long.valueOf(matcher.group(1));
            chatService.obtenerConversacionAutorizada(conversacionId, usuario);
            return;
        }

        throw new BusinessException("No está permitido suscribirse al destino: " + destino);
    }

    private void validarAutenticado(StompHeaderAccessor accessor) {
        if (accessor.getUser() != null) {
            return;
        }

        // Si por ciclo de vida de STOMP no está en accessor.getUser(), verificar sessionAttributes
        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
        if (sessionAttributes != null && sessionAttributes.get("usuario") != null) {
            Usuario usuario = (Usuario) sessionAttributes.get("usuario");
            List<GrantedAuthority> authorities = List.of(
                    new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name())
            );
            accessor.setUser(new UsernamePasswordAuthenticationToken(usuario.getEmail(), null, authorities));
            return;
        }

        // Si el cliente mandó header token en el frame SEND
        String token = accessor.getFirstNativeHeader("token");
        if (token == null || token.isBlank()) {
            token = accessor.getFirstNativeHeader("Authorization");
        }

        if (token != null && !token.isBlank()) {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7).trim();
            }
            if (jwtService.isTokenValid(token)) {
                String email = jwtService.extractUsername(token);
                Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);
                if (usuario != null) {
                    List<GrantedAuthority> authorities = List.of(
                            new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name())
                    );
                    accessor.setUser(new UsernamePasswordAuthenticationToken(email, null, authorities));
                    if (sessionAttributes != null) {
                        sessionAttributes.put("usuario", usuario);
                    }
                    return;
                }
            }
        }

        throw new BusinessException("No autenticado");
    }
}
