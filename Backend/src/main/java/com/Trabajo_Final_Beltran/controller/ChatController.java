package com.Trabajo_Final_Beltran.controller;

import com.Trabajo_Final_Beltran.dto.request.CrearChatRequest;
import com.Trabajo_Final_Beltran.dto.request.EnviarMensajeRequest;
import com.Trabajo_Final_Beltran.dto.response.ChatConversacionResponse;
import com.Trabajo_Final_Beltran.dto.response.ChatMensajeResponse;
import com.Trabajo_Final_Beltran.enums.EstadoConversacion;
import com.Trabajo_Final_Beltran.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chats")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    public ResponseEntity<ChatConversacionResponse> crearChat(@Valid @RequestBody CrearChatRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(chatService.crearOReutilizarChat(request.getPedidoId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChatConversacionResponse> obtenerDetalle(@PathVariable Long id) {
        return ResponseEntity.ok(chatService.obtenerDetalle(id));
    }

    @GetMapping("/{id}/mensajes")
    public ResponseEntity<Page<ChatMensajeResponse>> obtenerMensajes(
            @PathVariable Long id,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return ResponseEntity.ok(chatService.obtenerMensajes(id, pageable));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    public ResponseEntity<Page<ChatConversacionResponse>> listar(
            @RequestParam(required = false) EstadoConversacion estado,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return ResponseEntity.ok(chatService.listarPorEstablecimiento(estado, pageable));
    }

    @PostMapping("/{id}/mensajes")
    public ResponseEntity<ChatMensajeResponse> enviarMensaje(
            @PathVariable Long id,
            @Valid @RequestBody EnviarMensajeRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(chatService.enviarMensaje(id, request));
    }

    @PatchMapping("/{id}/asignar")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    public ResponseEntity<ChatConversacionResponse> asignar(@PathVariable Long id) {
        return ResponseEntity.ok(chatService.asignar(id));
    }

    @PatchMapping("/{id}/cerrar")
    public ResponseEntity<ChatConversacionResponse> cerrar(@PathVariable Long id) {
        return ResponseEntity.ok(chatService.cerrar(id));
    }
}