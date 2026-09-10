package com.Trabajo_Final_Beltran.service.impl;

import com.Trabajo_Final_Beltran.repository.ChatMensajeRepository;
import com.Trabajo_Final_Beltran.dto.request.EnviarMensajeRequest;
import com.Trabajo_Final_Beltran.dto.response.ChatConversacionResponse;
import com.Trabajo_Final_Beltran.dto.response.ChatMensajeResponse;
import com.Trabajo_Final_Beltran.entity.*;
import com.Trabajo_Final_Beltran.enums.CerradoPor;
import com.Trabajo_Final_Beltran.enums.EstadoConversacion;
import com.Trabajo_Final_Beltran.enums.Rol;
import com.Trabajo_Final_Beltran.event.ChatEstadoEvent;
import com.Trabajo_Final_Beltran.event.ChatLecturaEvent;
import com.Trabajo_Final_Beltran.exception.BusinessException;
import com.Trabajo_Final_Beltran.mapper.ChatMapper;
import com.Trabajo_Final_Beltran.repository.*;
import com.Trabajo_Final_Beltran.security.SecurityUtils;
import com.Trabajo_Final_Beltran.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.ApplicationEventPublisher;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatConversacionRepository chatConversacionRepository;
    private final ChatMensajeRepository chatMensajeRepository;
    private final PedidoRepository pedidoRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public ChatConversacion obtenerConversacionAutorizada(Long conversacionId, Usuario usuario) {
        ChatConversacion conversacion = chatConversacionRepository.findById(conversacionId)
                .orElseThrow(() -> new BusinessException("Conversación no encontrada"));

        if (!tieneAcceso(conversacion, usuario)) {
            throw new BusinessException("No tenés acceso a esta conversación");
        }
        return conversacion;
    }

    private boolean tieneAcceso(ChatConversacion conversacion, Usuario usuario) {
        boolean esPropietario = conversacion.getCliente() != null 
                && conversacion.getCliente().getId().equals(usuario.getId());

        boolean esAgenteDelEstablecimiento =
                (usuario.getRol() == Rol.ADMIN || usuario.getRol() == Rol.EMPLEADO)
                        && usuario.getEstablecimiento() != null
                        && conversacion.getEstablecimiento() != null
                        && conversacion.getEstablecimiento().getId().equals(usuario.getEstablecimiento().getId());

        return esPropietario || esAgenteDelEstablecimiento;
    }

    @Override
    @Transactional
    public ChatConversacionResponse crearOReutilizarChat(Long pedidoId) {
        Usuario usuario = SecurityUtils.obtenerUsuarioAutenticado();

        Pedido pedido = pedidoRepository.findByIdForUpdate(pedidoId)
                .orElseThrow(() -> new BusinessException("Pedido no encontrado"));

        if (!pedido.getUsuario().getId().equals(usuario.getId())) {
            throw new BusinessException("No podés iniciar un chat sobre un pedido que no es tuyo");
        }

        ChatConversacion existente = chatConversacionRepository
                .findByPedidoIdAndEstadoNot(pedidoId, EstadoConversacion.CERRADA)
                .orElse(null);

        if (existente != null) {
            return ChatMapper.toResponse(existente, contarNoLeidos(existente.getId(), usuario));
        }

        ChatConversacion nueva = ChatConversacion.builder()
                .establecimiento(pedido.getEstablecimiento())
                .pedido(pedido)
                .cliente(usuario)
                .estado(EstadoConversacion.ABIERTA)
                .build();

        try {
            nueva = chatConversacionRepository.save(nueva);
        } catch (DataIntegrityViolationException e) {
            return chatConversacionRepository.findByPedidoIdAndEstadoNot(pedidoId, EstadoConversacion.CERRADA)
                    .map(c -> ChatMapper.toResponse(c, contarNoLeidos(c.getId(), usuario)))
                    .orElseThrow(() -> new BusinessException("No se pudo crear la conversación"));
        }

        crearMensajeSistemaConSnapshot(nueva, pedido);

        return ChatMapper.toResponse(nueva, 0);
    }

    private void crearMensajeSistemaConSnapshot(ChatConversacion conversacion, Pedido pedido) {
        String snapshotJson = construirSnapshotPedido(pedido);

        ChatMensaje mensajeSistema = ChatMensaje.builder()
                .conversacion(conversacion)
                .remitente(pedido.getUsuario())
                .esSistema(true)
                .contenido(snapshotJson)
                .leido(false)
                .build();

        chatMensajeRepository.save(mensajeSistema);
    }

    private String construirSnapshotPedido(Pedido pedido) {

        return """
            {"numeroPedido":"%s","total":%s,"estado":"%s","cantidadItems":%d}
            """.formatted(
                pedido.getNumeroPedido(),
                pedido.getTotal(),
                pedido.getEstado().name(),
                pedido.getDetalles().size()
        ).trim();
    }

    @Override
    public ChatConversacionResponse obtenerDetalle(Long conversacionId) {
        Usuario usuario = SecurityUtils.obtenerUsuarioAutenticado();
        ChatConversacion conversacion = obtenerConversacionAutorizada(conversacionId, usuario);
        return ChatMapper.toResponse(conversacion, contarNoLeidos(conversacionId, usuario));
    }

    @Override
    @Transactional
    public Page<ChatMensajeResponse> obtenerMensajes(Long conversacionId, Pageable pageable) {
        Usuario usuario = SecurityUtils.obtenerUsuarioAutenticado();
        obtenerConversacionAutorizada(conversacionId, usuario);

        int actualizados = chatMensajeRepository.marcarLeidosDelOtroParticipante(conversacionId, usuario.getId());
        if (actualizados > 0) {
            com.Trabajo_Final_Beltran.dto.response.ChatLecturaResponse lectura =
                    com.Trabajo_Final_Beltran.dto.response.ChatLecturaResponse.builder()
                            .conversacionId(conversacionId)
                            .lectorId(usuario.getId())
                            .fechaLectura(java.time.LocalDateTime.now())
                            .build();

            eventPublisher.publishEvent(new ChatLecturaEvent(conversacionId, lectura));
        }

        return chatMensajeRepository.findByConversacionIdOrderByFechaEnvioDesc(conversacionId, pageable)
                .map(ChatMapper::toResponse);
    }

    @Override
    @Transactional
    public com.Trabajo_Final_Beltran.dto.response.ChatLecturaResponse marcarMensajesComoLeidos(Long conversacionId, Usuario usuario) {
        obtenerConversacionAutorizada(conversacionId, usuario);

        int actualizados = chatMensajeRepository.marcarLeidosDelOtroParticipante(conversacionId, usuario.getId());
        com.Trabajo_Final_Beltran.dto.response.ChatLecturaResponse lectura =
                com.Trabajo_Final_Beltran.dto.response.ChatLecturaResponse.builder()
                        .conversacionId(conversacionId)
                        .lectorId(usuario.getId())
                        .fechaLectura(java.time.LocalDateTime.now())
                        .build();

        if (actualizados > 0) {
            eventPublisher.publishEvent(new ChatLecturaEvent(conversacionId, lectura));
        }

        return lectura;
    }

    @Override
    public Page<ChatConversacionResponse> listarPorEstablecimiento(EstadoConversacion estado, Pageable pageable) {
        Usuario usuario = SecurityUtils.obtenerUsuarioAutenticado();

        if (usuario.getRol() != Rol.ADMIN && usuario.getRol() != Rol.EMPLEADO) {
            throw new BusinessException("No tenés permisos para ver la bandeja de soporte");
        }

        Long establecimientoId = usuario.getEstablecimiento().getId();

        return chatConversacionRepository
                .findByEstablecimientoIdAndEstado(establecimientoId, estado, pageable)
                .map(c -> ChatMapper.toResponse(c, contarNoLeidos(c.getId(), usuario)));
    }

    @Override
    @Transactional
    public ChatMensajeResponse enviarMensaje(Long conversacionId, EnviarMensajeRequest request) {
        return enviarMensaje(conversacionId, request, SecurityUtils.obtenerUsuarioAutenticado());
    }

    @Override
    @Transactional
    public ChatMensajeResponse enviarMensaje(Long conversacionId, EnviarMensajeRequest request, Usuario usuario) {
        if (request.getContenido() == null || request.getContenido().isBlank()) {
            throw new BusinessException("El mensaje no puede estar vacío");
        }

        ChatConversacion conversacion = obtenerConversacionAutorizada(conversacionId, usuario);

        if (conversacion.getEstado() == EstadoConversacion.CERRADA) {
            throw new BusinessException("No se pueden enviar mensajes en una conversación cerrada");
        }

        ChatMensaje mensaje = ChatMensaje.builder()
                .conversacion(conversacion)
                .remitente(usuario)
                .esSistema(false)
                .contenido(request.getContenido())
                .leido(false)
                .build();

        return ChatMapper.toResponse(chatMensajeRepository.save(mensaje));
    }

    @Override
    @Transactional
    public ChatConversacionResponse asignar(Long conversacionId) {
        Usuario usuario = SecurityUtils.obtenerUsuarioAutenticado();

        if (usuario.getRol() != Rol.ADMIN && usuario.getRol() != Rol.EMPLEADO) {
            throw new BusinessException("No tenés permisos para tomar conversaciones de soporte");
        }

        int filas = chatConversacionRepository.asignarSiEstaAbierta(conversacionId, usuario.getId());
        if (filas == 0) {
            throw new BusinessException("El chat ya fue tomado por otro agente o no está disponible");
        }

        ChatConversacion actualizada = chatConversacionRepository.findById(conversacionId)
                .orElseThrow(() -> new BusinessException("Conversación no encontrada"));

        ChatConversacionResponse respuesta =
                ChatMapper.toResponse(actualizada, contarNoLeidos(conversacionId, usuario));

        eventPublisher.publishEvent(new ChatEstadoEvent(conversacionId, respuesta));

        return respuesta;
    }
    

    @Override
    @Transactional
    public ChatConversacionResponse cerrar(Long conversacionId) {
        Usuario usuario = SecurityUtils.obtenerUsuarioAutenticado();
        ChatConversacion conversacion = obtenerConversacionAutorizada(conversacionId, usuario);

        if (conversacion.getEstado() == EstadoConversacion.CERRADA) {
            throw new BusinessException("La conversación ya está cerrada");
        }

        boolean esCliente = conversacion.getCliente().getId().equals(usuario.getId());
        conversacion.setCerradoPor(esCliente ? CerradoPor.CLIENTE : CerradoPor.AGENTE);
        conversacion.setEstado(EstadoConversacion.CERRADA);

        ChatConversacion actualizada = chatConversacionRepository.save(conversacion);

        ChatConversacionResponse respuesta =
                ChatMapper.toResponse(actualizada, contarNoLeidos(conversacionId, usuario));

        eventPublisher.publishEvent(new ChatEstadoEvent(conversacionId, respuesta));

        return respuesta;
    }

    private long contarNoLeidos(Long conversacionId, Usuario usuario) {
        return chatMensajeRepository.countByConversacionIdAndLeidoFalse(conversacionId);
    }
}