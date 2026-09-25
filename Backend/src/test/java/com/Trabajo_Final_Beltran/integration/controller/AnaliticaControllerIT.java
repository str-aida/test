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
@DisplayName("Flujo de Analitica - Integration Test")
class AnaliticaControllerIT {

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
                .nombre("Ana")
                .apellido("Litica")
                .email("ana.litica@test.com")
                .password("Password123")
                .dni("47888999")
                .telefono("1111223344")
                .fechaNacimiento(LocalDate.of(1996, 4, 7))
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
    @DisplayName("1. CLIENTE no puede ver el resumen ejecutivo (HTTP 403)")
    void clienteNoDeberiaVerResumen() throws Exception {
        System.out.println("-> Ejecutando AnaliticaControllerIT: clienteNoDeberiaVerResumen");

        mockMvc.perform(get("/analitica/resumen")
                        .header("Authorization", "Bearer " + jwtTokenCliente))
                .andExpect(status().isForbidden());

        System.out.println("-> OK: Resumen ejecutivo rechazado con HTTP 403 para rol CLIENTE.");
    }

    @Test
    @Order(2)
    @DisplayName("2. CLIENTE no puede ver mejores clientes (HTTP 403)")
    void clienteNoDeberiaVerMejoresClientes() throws Exception {
        System.out.println("-> Ejecutando AnaliticaControllerIT: clienteNoDeberiaVerMejoresClientes");

        mockMvc.perform(get("/analitica/clientes/mejores")
                        .header("Authorization", "Bearer " + jwtTokenCliente))
                .andExpect(status().isForbidden());

        System.out.println("-> OK: Mejores clientes rechazado con HTTP 403 para rol CLIENTE.");
    }

    @Test
    @Order(3)
    @DisplayName("3. CLIENTE no puede ver pedidos por estado (HTTP 403)")
    void clienteNoDeberiaVerPedidosPorEstado() throws Exception {
        System.out.println("-> Ejecutando AnaliticaControllerIT: clienteNoDeberiaVerPedidosPorEstado");

        mockMvc.perform(get("/analitica/pedidos/estados")
                        .header("Authorization", "Bearer " + jwtTokenCliente))
                .andExpect(status().isForbidden());

        System.out.println("-> OK: Pedidos por estado rechazado con HTTP 403 para rol CLIENTE.");
    }

    @Test
    @Order(4)
    @DisplayName("4. CLIENTE no puede ver productos mas vendidos (HTTP 403)")
    void clienteNoDeberiaVerProductosMasVendidos() throws Exception {
        System.out.println("-> Ejecutando AnaliticaControllerIT: clienteNoDeberiaVerProductosMasVendidos");

        mockMvc.perform(get("/analitica/productos/mas-vendidos")
                        .header("Authorization", "Bearer " + jwtTokenCliente))
                .andExpect(status().isForbidden());

        System.out.println("-> OK: Productos mas vendidos rechazado con HTTP 403 para rol CLIENTE.");
    }

    @Test
    @Order(5)
    @DisplayName("5. CLIENTE no puede ver ventas por periodo (HTTP 403)")
    void clienteNoDeberiaVerVentasPorPeriodo() throws Exception {
        System.out.println("-> Ejecutando AnaliticaControllerIT: clienteNoDeberiaVerVentasPorPeriodo");

        mockMvc.perform(get("/analitica/ventas/periodo")
                        .header("Authorization", "Bearer " + jwtTokenCliente))
                .andExpect(status().isForbidden());

        System.out.println("-> OK: Ventas por periodo rechazado con HTTP 403 para rol CLIENTE.");
    }

    @Test
    @Order(6)
    @DisplayName("6. Sin token debe fallar con HTTP 403")
    void sinTokenDeberiaFallar() throws Exception {
        System.out.println("-> Ejecutando AnaliticaControllerIT: sinTokenDeberiaFallar");

        mockMvc.perform(get("/analitica/resumen"))
                .andExpect(status().isForbidden());

        System.out.println("-> OK: Peticion sin token rechazada con HTTP 403.");
    }
}
