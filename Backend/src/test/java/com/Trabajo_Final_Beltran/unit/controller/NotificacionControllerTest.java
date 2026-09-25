package com.Trabajo_Final_Beltran.unit.controller;

import com.Trabajo_Final_Beltran.controller.NotificacionController;
import com.Trabajo_Final_Beltran.dto.response.NotificacionResponse;
import com.Trabajo_Final_Beltran.enums.TipoNotificacion;
import com.Trabajo_Final_Beltran.security.JwtAuthenticationFilter;
import com.Trabajo_Final_Beltran.security.RateLimitFilter;
import com.Trabajo_Final_Beltran.service.NotificacionService;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        value = NotificacionController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {JwtAuthenticationFilter.class, RateLimitFilter.class}
        ),
        excludeAutoConfiguration = SecurityAutoConfiguration.class
)
@DisplayName("NotificacionController - Unit Tests")
class NotificacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NotificacionService notificacionService;

    @MockitoBean
    private CacheManager cacheManager;

    @Test
    @WithMockUser(roles = "CLIENTE")
    @DisplayName("GET /notificaciones - Debe listar notificaciones con HTTP 200")
    void listarMisNotificaciones_debeRetornarOk() throws Exception {
        System.out.println("-> Ejecutando NotificacionControllerTest: listarMisNotificaciones_debeRetornarOk");

        NotificacionResponse notif = NotificacionResponse.builder()
                .id(1L)
                .titulo("Pedido Confirmado")
                .mensaje("Tu pedido está en preparación")
                .tipo(TipoNotificacion.PEDIDO)
                .leida(false)
                .fecha(LocalDateTime.now())
                .build();

        when(notificacionService.listarMisNotificaciones()).thenReturn(List.of(notif));

        mockMvc.perform(get("/notificaciones")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].titulo").value("Pedido Confirmado"));

        System.out.println("-> OK: listarMisNotificaciones_debeRetornarOk completado con exito.");
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    @DisplayName("GET /notificaciones/no-leidas - Debe retornar cantidad no leídas con HTTP 200")
    void contarNoLeidas_debeRetornarOk() throws Exception {
        System.out.println("-> Ejecutando NotificacionControllerTest: contarNoLeidas_debeRetornarOk");

        when(notificacionService.contarNoLeidas()).thenReturn(3L);

        mockMvc.perform(get("/notificaciones/no-leidas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(3L));

        System.out.println("-> OK: contarNoLeidas_debeRetornarOk completado con exito.");
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    @DisplayName("PATCH /notificaciones/{id}/leida - Debe marcar como leída con HTTP 204")
    void marcarComoLeida_debeRetornarNoContent() throws Exception {
        System.out.println("-> Ejecutando NotificacionControllerTest: marcarComoLeida_debeRetornarNoContent");

        mockMvc.perform(patch("/notificaciones/1/leida"))
                .andExpect(status().isNoContent());

        System.out.println("-> OK: marcarComoLeida_debeRetornarNoContent completado con exito.");
    }
}
