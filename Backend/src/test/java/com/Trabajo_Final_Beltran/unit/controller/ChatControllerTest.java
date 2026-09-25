package com.Trabajo_Final_Beltran.unit.controller;

import com.Trabajo_Final_Beltran.controller.ChatController;
import com.Trabajo_Final_Beltran.dto.request.CrearChatRequest;
import com.Trabajo_Final_Beltran.dto.request.EnviarMensajeRequest;
import com.Trabajo_Final_Beltran.dto.response.ChatConversacionResponse;
import com.Trabajo_Final_Beltran.dto.response.ChatMensajeResponse;
import com.Trabajo_Final_Beltran.enums.EstadoConversacion;
import com.Trabajo_Final_Beltran.security.JwtAuthenticationFilter;
import com.Trabajo_Final_Beltran.security.RateLimitFilter;
import com.Trabajo_Final_Beltran.service.ChatService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        value = ChatController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {JwtAuthenticationFilter.class, RateLimitFilter.class}
        ),
        excludeAutoConfiguration = SecurityAutoConfiguration.class
)
@DisplayName("ChatController - Unit Tests")
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @MockitoBean
    private ChatService chatService;

    @MockitoBean
    private CacheManager cacheManager;

    @Test
    @WithMockUser(roles = "CLIENTE")
    @DisplayName("POST /chats - Debe crear o reutilizar chat con HTTP 201")
    void crearChat_debeRetornarCreated() throws Exception {
        System.out.println("-> Ejecutando ChatControllerTest: crearChat_debeRetornarCreated");

        CrearChatRequest request = new CrearChatRequest(1L);
        ChatConversacionResponse response = ChatConversacionResponse.builder()
                .id(10L)
                .pedidoId(1L)
                .estado(EstadoConversacion.ABIERTA)
                .build();

        when(chatService.crearOReutilizarChat(1L)).thenReturn(response);

        mockMvc.perform(post("/chats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10L));

        System.out.println("-> OK: crearChat_debeRetornarCreated completado con exito.");
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    @DisplayName("GET /chats/{id} - Debe obtener detalle de conversación con HTTP 200")
    void obtenerDetalle_debeRetornarOk() throws Exception {
        System.out.println("-> Ejecutando ChatControllerTest: obtenerDetalle_debeRetornarOk");

        ChatConversacionResponse response = ChatConversacionResponse.builder()
                .id(10L)
                .pedidoId(1L)
                .estado(EstadoConversacion.ABIERTA)
                .build();

        when(chatService.obtenerDetalle(10L)).thenReturn(response);

        mockMvc.perform(get("/chats/10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L));

        System.out.println("-> OK: obtenerDetalle_debeRetornarOk completado con exito.");
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    @DisplayName("POST /chats/{id}/mensajes - Debe enviar mensaje con HTTP 201")
    void enviarMensaje_debeRetornarCreated() throws Exception {
        System.out.println("-> Ejecutando ChatControllerTest: enviarMensaje_debeRetornarCreated");

        EnviarMensajeRequest request = new EnviarMensajeRequest("Hola, tengo una consulta");
        ChatMensajeResponse response = ChatMensajeResponse.builder()
                .id(100L)
                .contenido("Hola, tengo una consulta")
                .build();

        when(chatService.enviarMensaje(eq(10L), any(EnviarMensajeRequest.class))).thenReturn(response);

        mockMvc.perform(post("/chats/10/mensajes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100L))
                .andExpect(jsonPath("$.contenido").value("Hola, tengo una consulta"));

        System.out.println("-> OK: enviarMensaje_debeRetornarCreated completado con exito.");
    }
}
