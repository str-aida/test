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
@DisplayName("Flujo de Establecimiento - Integration Test")
class EstablecimientoControllerIT {

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
                .nombre("Elena")
                .apellido("Estab")
                .email("elena.estab@test.com")
                .password("Password123")
                .dni("43444555")
                .telefono("1177889900")
                .fechaNacimiento(LocalDate.of(1997, 5, 12))
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
    @DisplayName("1. Cliente autenticado puede obtener la info publica del establecimiento (HTTP 200)")
    void clienteDeberiaObtenerInfoEstablecimiento() throws Exception {
        System.out.println("-> Ejecutando EstablecimientoControllerIT: clienteDeberiaObtenerInfoEstablecimiento");

        mockMvc.perform(get("/establecimiento/info")
                        .header("Authorization", "Bearer " + jwtTokenCliente))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").exists());

        System.out.println("-> OK: Info del establecimiento obtenida correctamente.");
    }

    @Test
    @Order(2)
    @DisplayName("2. CLIENTE no puede acceder al detalle admin del establecimiento (HTTP 403)")
    void clienteNoDeberiaAccederADetalleAdmin() throws Exception {
        System.out.println("-> Ejecutando EstablecimientoControllerIT: clienteNoDeberiaAccederADetalleAdmin");

        mockMvc.perform(get("/establecimiento")
                        .header("Authorization", "Bearer " + jwtTokenCliente))
                .andExpect(status().isForbidden());

        System.out.println("-> OK: Acceso admin rechazado con HTTP 403 para rol CLIENTE.");
    }

    @Test
    @Order(3)
    @DisplayName("3. Sin token debe fallar con HTTP 403")
    void sinTokenDeberiaFallar() throws Exception {
        System.out.println("-> Ejecutando EstablecimientoControllerIT: sinTokenDeberiaFallar");

        mockMvc.perform(get("/establecimiento/info"))
                .andExpect(status().isForbidden());

        System.out.println("-> OK: Peticion sin token rechazada con HTTP 403.");
    }

    @Test
    @Order(4)
    @DisplayName("4. CLIENTE no puede actualizar el establecimiento (HTTP 403)")
    void clienteNoDeberiaActualizarEstablecimiento() throws Exception {
        System.out.println("-> Ejecutando EstablecimientoControllerIT: clienteNoDeberiaActualizarEstablecimiento");

        com.Trabajo_Final_Beltran.dto.request.UpdateDireccionRequest direccionReq = com.Trabajo_Final_Beltran.dto.request.UpdateDireccionRequest.builder()
                .calle("Av. Corrientes")
                .numero("1234")
                .localidad("CABA")
                .build();

        com.Trabajo_Final_Beltran.dto.request.UpdateEstablecimientoRequest request = com.Trabajo_Final_Beltran.dto.request.UpdateEstablecimientoRequest.builder()
                .nombre("Nuevo Nombre")
                .razonSocial("Nueva Razon Social S.A.")
                .email("nuevo@gestia.com")
                .telefono("1122334455")
                .direccion(direccionReq)
                .horarioApertura(LocalTime.of(9, 0))
                .horarioCierre(LocalTime.of(22, 0))
                .diasHabiles(Set.of(DiaSemana.LUNES, DiaSemana.MARTES))
                .tipoServicio(TipoServicio.AMBOS)
                .build();

        mockMvc.perform(put("/establecimiento")
                        .header("Authorization", "Bearer " + jwtTokenCliente)
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        System.out.println("-> OK: Actualizacion rechazada con HTTP 403 para rol CLIENTE.");
    }
}
