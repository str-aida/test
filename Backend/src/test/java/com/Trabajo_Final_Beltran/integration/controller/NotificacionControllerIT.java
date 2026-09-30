package com.Trabajo_Final_Beltran.integration.controller;

import com.Trabajo_Final_Beltran.dto.request.RegisterRequest;
import com.Trabajo_Final_Beltran.entity.Establecimiento;
import com.Trabajo_Final_Beltran.enums.DiaSemana;
import com.Trabajo_Final_Beltran.enums.EstadoEstablecimiento;
import com.Trabajo_Final_Beltran.enums.TipoServicio;
import com.Trabajo_Final_Beltran.repository.EstablecimientoRepository;
import com.Trabajo_Final_Beltran.service.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Flujo de Notificaciones - Integration Test")
class NotificacionControllerIT {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Autowired
    private EstablecimientoRepository establecimientoRepository;

    @MockitoBean
    private EmailService emailService;

    private String jwtToken;

    @BeforeAll
    void setupEstablecimientoYUsuario() throws Exception {
        if (establecimientoRepository.count() == 0) {
            Establecimiento est = Establecimiento.builder()
                    .nombre("Gestia Restaurant")
                    .razonSocial("Gestia S.A.")
                    .cuit("30-12345678-9")
                    .email("contacto@gestia.com")
                    .telefono("1122334455")
                    .logoUrl("http://localhost/logo.png")
                    .horarioApertura(LocalTime.of(8, 0))
                    .horarioCierre(LocalTime.of(23, 0))
                    .diasHabiles(Set.of(DiaSemana.LUNES, DiaSemana.MARTES, DiaSemana.MIERCOLES,
                            DiaSemana.JUEVES, DiaSemana.VIERNES))
                    .tipoServicio(TipoServicio.AMBOS)
                    .estado(EstadoEstablecimiento.ACTIVO)
                    .build();
            establecimientoRepository.save(est);
        }

        RegisterRequest registro = RegisterRequest.builder()
                .nombre("Noti")
                .apellido("Ficacion")
                .email("noti.ficacion@test.com")
                .password("Password123")
                .dni("44555666")
                .telefono("1188990011")
                .fechaNacimiento(LocalDate.of(1993, 11, 3))
                .build();

        String resp = mockMvc.perform(post("/auth/registro-cliente")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registro)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Matcher m = Pattern.compile("\"token\":\"(.*?)\"").matcher(resp);
        if (m.find()) jwtToken = m.group(1);
        assertThat(jwtToken).isNotNull().isNotBlank();
    }

    @Test
    @Order(1)
    @DisplayName("1. Debe listar notificaciones del usuario autenticado (HTTP 200)")
    void deberiaListarMisNotificaciones() throws Exception {
        System.out.println("-> Ejecutando NotificacionControllerIT: deberiaListarMisNotificaciones");

        mockMvc.perform(get("/notificaciones")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        System.out.println("-> OK: Listado de notificaciones retornado correctamente.");
    }

    @Test
    @Order(2)
    @DisplayName("2. Debe contar notificaciones no leidas (HTTP 200)")
    void deberiaContarNoLeidas() throws Exception {
        System.out.println("-> Ejecutando NotificacionControllerIT: deberiaContarNoLeidas");

        mockMvc.perform(get("/notificaciones/no-leidas")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());

        System.out.println("-> OK: Contador de notificaciones no leidas obtenido correctamente.");
    }

    @Test
    @Order(3)
    @DisplayName("3. Marcar notificacion inexistente como leida debe retornar HTTP 400 (Notificación no encontrada)")
    void marcarNotificacionInexistenteDeberiaFallar() throws Exception {
        System.out.println("-> Ejecutando NotificacionControllerIT: marcarNotificacionInexistenteDeberiaFallar");

        mockMvc.perform(patch("/notificaciones/99999/leida")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Notificación no encontrada"));

        System.out.println("-> OK: Notificacion inexistente retorna HTTP 400 correctamente.");
    }

    @Test
    @Order(4)
    @DisplayName("4. Marcar todas las notificaciones como leidas debe retornar HTTP 204")
    void deberiaMarcarTodasComoLeidas() throws Exception {
        System.out.println("-> Ejecutando NotificacionControllerIT: deberiaMarcarTodasComoLeidas");

        mockMvc.perform(patch("/notificaciones/marcar-todas-leidas")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNoContent());

        System.out.println("-> OK: Todas las notificaciones marcadas como leidas con HTTP 204.");
    }

    @Test
    @Order(5)
    @DisplayName("5. Acceder sin token debe fallar con HTTP 403")
    void sinTokenDeberiaFallar() throws Exception {
        System.out.println("-> Ejecutando NotificacionControllerIT: sinTokenDeberiaFallar");

        mockMvc.perform(get("/notificaciones"))
                .andExpect(status().isForbidden());

        System.out.println("-> OK: Peticion sin token rechazada con HTTP 403.");
    }
}
