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
import org.springframework.http.MediaType;
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
@DisplayName("Flujo de Chat - Integration Test")
class ChatControllerIT {

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

        RegisterRequest register = RegisterRequest.builder()
                .nombre("Carlos")
                .apellido("Chat")
                .email("carlos.chat@test.com")
                .password("Password123")
                .dni("41222333")
                .telefono("1155667788")
                .fechaNacimiento(LocalDate.of(2000, 1, 15))
                .build();

        String response = mockMvc.perform(post("/auth/registro-cliente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Matcher matcher = Pattern.compile("\"token\":\"(.*?)\"").matcher(response);
        if (matcher.find()) {
            jwtToken = matcher.group(1);
        }
        assertThat(jwtToken).isNotNull().isNotBlank();
    }

    @Test
    @Order(1)
    @DisplayName("1. Crear un chat sin pedidoId debe fallar con HTTP 400")
    void crearChatSinPedidoIdDeberiaFallar() throws Exception {
        System.out.println("-> Ejecutando ChatControllerIT: crearChatSinPedidoIdDeberiaFallar");

        // Enviar body vacio (pedidoId es @NotNull)
        mockMvc.perform(post("/chats")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        System.out.println("-> OK: Solicitud rechazada correctamente con HTTP 400 por pedidoId nulo.");
    }

    @Test
    @Order(2)
    @DisplayName("2. Obtener detalle de chat inexistente debe retornar HTTP 400")
    void obtenerDetalleChatInexistenteDeberiaFallar() throws Exception {
        System.out.println("-> Ejecutando ChatControllerIT: obtenerDetalleChatInexistenteDeberiaFallar");

        mockMvc.perform(get("/chats/99999")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isBadRequest());

        System.out.println("-> OK: Chat inexistente retorna HTTP 400 correctamente.");
    }

    @Test
    @Order(3)
    @DisplayName("3. Obtener mensajes de chat inexistente debe retornar HTTP 400")
    void obtenerMensajesChatInexistenteDeberiaFallar() throws Exception {
        System.out.println("-> Ejecutando ChatControllerIT: obtenerMensajesChatInexistenteDeberiaFallar");

        mockMvc.perform(get("/chats/99999/mensajes")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isBadRequest());

        System.out.println("-> OK: Mensajes de chat inexistente retorna HTTP 400 correctamente.");
    }

    @Test
    @Order(4)
    @DisplayName("4. Listar chats sin rol ADMIN/EMPLEADO debe fallar con HTTP 403")
    void listarChatsSinRolAdminDeberiaFallar() throws Exception {
        System.out.println("-> Ejecutando ChatControllerIT: listarChatsSinRolAdminDeberiaFallar");

        // El usuario es CLIENTE, el endpoint requiere ADMIN o EMPLEADO
        mockMvc.perform(get("/chats")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isForbidden());

        System.out.println("-> OK: Listado de chats rechazado con HTTP 403 para rol CLIENTE.");
    }

    @Test
    @Order(5)
    @DisplayName("5. Acceder a /chats sin token debe fallar con HTTP 403")
    void accederChatssinTokenDeberiaFallar() throws Exception {
        System.out.println("-> Ejecutando ChatControllerIT: accederChatssinTokenDeberiaFallar");

        mockMvc.perform(get("/chats"))
                .andExpect(status().isForbidden());

        System.out.println("-> OK: Peticion sin token rechazada con HTTP 403.");
    }
}
