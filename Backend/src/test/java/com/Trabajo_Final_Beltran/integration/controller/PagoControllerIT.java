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
@DisplayName("Flujo de Pagos - Integration Test")
class PagoControllerIT {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Autowired
    private EstablecimientoRepository establecimientoRepository;

    @MockitoBean
    private EmailService emailService;

    private String jwtTokenCliente;

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

        RegisterRequest cliente = RegisterRequest.builder()
                .nombre("Pago")
                .apellido("Cliente")
                .email("pago.cliente@test.com")
                .password("Password123")
                .dni("45666777")
                .telefono("1199001122")
                .fechaNacimiento(LocalDate.of(1991, 8, 18))
                .build();

        String resp = mockMvc.perform(post("/auth/registro-cliente")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cliente)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Matcher m = Pattern.compile("\"token\":\"(.*?)\"").matcher(resp);
        if (m.find()) jwtTokenCliente = m.group(1);
        assertThat(jwtTokenCliente).isNotNull().isNotBlank();
    }

    @Test
    @Order(1)
    @DisplayName("1. Crear pago para pedido inexistente debe retornar HTTP 400 (Pedido no encontrado)")
    void crearPagoParaPedidoInexistenteDeberiaFallar() throws Exception {
        System.out.println("-> Ejecutando PagoControllerIT: crearPagoParaPedidoInexistenteDeberiaFallar");

        mockMvc.perform(post("/pagos/99999")
                        .header("Authorization", "Bearer " + jwtTokenCliente))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Pedido no encontrado"));

        System.out.println("-> OK: Pago para pedido inexistente retorna HTTP 400 correctamente.");
    }

    @Test
    @Order(2)
    @DisplayName("2. CLIENTE no puede aprobar un pago (HTTP 403)")
    void clienteNoDeberiaAprobarPago() throws Exception {
        System.out.println("-> Ejecutando PagoControllerIT: clienteNoDeberiaAprobarPago");

        mockMvc.perform(put("/pagos/1/aprobar")
                        .header("Authorization", "Bearer " + jwtTokenCliente)
                        .param("referencia", "REF-001"))
                .andExpect(status().isForbidden());

        System.out.println("-> OK: Aprobacion de pago rechazada con HTTP 403 para rol CLIENTE.");
    }

    @Test
    @Order(3)
    @DisplayName("3. CLIENTE no puede reembolsar un pago (HTTP 403)")
    void clienteNoDeberiaReembolsarPago() throws Exception {
        System.out.println("-> Ejecutando PagoControllerIT: clienteNoDeberiaReembolsarPago");

        mockMvc.perform(put("/pagos/1/reembolsar")
                        .header("Authorization", "Bearer " + jwtTokenCliente))
                .andExpect(status().isForbidden());

        System.out.println("-> OK: Reembolso rechazado con HTTP 403 para rol CLIENTE.");
    }

    @Test
    @Order(4)
    @DisplayName("4. Acceder a pagos sin token debe fallar con HTTP 403")
    void sinTokenDeberiaFallar() throws Exception {
        System.out.println("-> Ejecutando PagoControllerIT: sinTokenDeberiaFallar");

        mockMvc.perform(post("/pagos/1"))
                .andExpect(status().isForbidden());

        System.out.println("-> OK: Peticion sin token rechazada con HTTP 403.");
    }
}
