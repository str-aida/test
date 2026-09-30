package com.Trabajo_Final_Beltran.integration.controller;

import com.Trabajo_Final_Beltran.dto.request.UpdateReglaCuponRequest;
import com.Trabajo_Final_Beltran.entity.Establecimiento;
import com.Trabajo_Final_Beltran.entity.ReglaCupon;
import com.Trabajo_Final_Beltran.entity.Usuario;
import com.Trabajo_Final_Beltran.enums.*;
import com.Trabajo_Final_Beltran.repository.EstablecimientoRepository;
import com.Trabajo_Final_Beltran.repository.ReglaCuponRepository;
import com.Trabajo_Final_Beltran.repository.UsuarioRepository;
import com.Trabajo_Final_Beltran.security.JwtService;
import com.Trabajo_Final_Beltran.service.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("ReglaCuponController - Integration Tests")
class ReglaCuponControllerIT {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Autowired
    private ReglaCuponRepository reglaCuponRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EstablecimientoRepository establecimientoRepository;

    @Autowired
    private JwtService jwtService;

    @MockitoBean
    private EmailService emailService;

    private String adminToken;
    private String clienteToken;

    @BeforeAll
    void setup() {
        reglaCuponRepository.deleteAll();

        Establecimiento est;
        if (establecimientoRepository.count() == 0) {
            est = establecimientoRepository.save(Establecimiento.builder()
                    .nombre("Gestia Reglas")
                    .razonSocial("Gestia Reglas S.A.")
                    .cuit("30-66778899-0")
                    .email("contacto.reglas@gestia.com")
                    .telefono("1166778899")
                    .logoUrl("http://localhost/logo.png")
                    .horarioApertura(LocalTime.of(8, 0))
                    .horarioCierre(LocalTime.of(23, 0))
                    .diasHabiles(Set.of(DiaSemana.LUNES, DiaSemana.MARTES, DiaSemana.MIERCOLES))
                    .tipoServicio(TipoServicio.AMBOS)
                    .estado(EstadoEstablecimiento.ACTIVO)
                    .build());
        } else {
            est = establecimientoRepository.findAll().get(0);
        }

        Usuario adminUser = usuarioRepository.findByEmail("admin.reglas@test.com")
                .orElseGet(() -> usuarioRepository.save(Usuario.builder()
                        .nombre("Admin")
                        .apellido("Reglas")
                        .email("admin.reglas@test.com")
                        .password("$2a$10$dummyhashedpassword12345678901234567890")
                        .dni("61001122")
                        .telefono("1166554433")
                        .fechaNacimiento(LocalDate.of(1986, 7, 12))
                        .rol(Rol.ADMIN)
                        .estado(Estado.ACTIVO)
                        .establecimiento(est)
                        .build()));

        Usuario clienteUser = usuarioRepository.findByEmail("cliente.reglas@test.com")
                .orElseGet(() -> usuarioRepository.save(Usuario.builder()
                        .nombre("Cliente")
                        .apellido("Reglas")
                        .email("cliente.reglas@test.com")
                        .password("$2a$10$dummyhashedpassword12345678901234567890")
                        .dni("62001122")
                        .telefono("1166554434")
                        .fechaNacimiento(LocalDate.of(1993, 8, 25))
                        .rol(Rol.CLIENTE)
                        .estado(Estado.ACTIVO)
                        .establecimiento(est)
                        .build()));

        adminToken = jwtService.generateToken(adminUser);
        clienteToken = jwtService.generateToken(clienteUser);

        reglaCuponRepository.save(ReglaCupon.builder()
                .tipoAsignacion(TipoAsignacionCupon.BIENVENIDA)
                .activo(true)
                .tipoDescuento(TipoDescuento.PORCENTAJE)
                .valor(new BigDecimal("15.00"))
                .diasValidez(15)
                .descripcion("Cupón de bienvenida 15% OFF")
                .build());
    }

    @Test
    @Order(1)
    @DisplayName("1. ADMIN puede listar las reglas de cupón (HTTP 200)")
    void adminDeberiaListarReglas() throws Exception {
        System.out.println("-> Ejecutando ReglaCuponControllerIT: adminDeberiaListarReglas");

        mockMvc.perform(get("/cupones/reglas")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].tipoAsignacion").value("BIENVENIDA"));

        System.out.println("-> OK: Reglas listadas correctamente por ADMIN.");
    }

    @Test
    @Order(2)
    @DisplayName("2. ADMIN puede obtener una regla por tipo de asignación (HTTP 200)")
    void adminDeberiaObtenerReglaPorTipo() throws Exception {
        System.out.println("-> Ejecutando ReglaCuponControllerIT: adminDeberiaObtenerReglaPorTipo");

        mockMvc.perform(get("/cupones/reglas/BIENVENIDA")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipoAsignacion").value("BIENVENIDA"))
                .andExpect(jsonPath("$.valor").value(15.00));

        System.out.println("-> OK: Regla obtenida correctamente.");
    }

    @Test
    @Order(3)
    @DisplayName("3. ADMIN puede actualizar una regla de cupón (HTTP 200)")
    void adminDeberiaActualizarRegla() throws Exception {
        System.out.println("-> Ejecutando ReglaCuponControllerIT: adminDeberiaActualizarRegla");

        UpdateReglaCuponRequest request = UpdateReglaCuponRequest.builder()
                .activo(true)
                .tipoDescuento(TipoDescuento.PORCENTAJE)
                .valor(new BigDecimal("20.00"))
                .diasValidez(30)
                .descripcion("Cupón de bienvenida actualizado a 20% OFF")
                .build();

        mockMvc.perform(put("/cupones/reglas/BIENVENIDA")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valor").value(20.00))
                .andExpect(jsonPath("$.diasValidez").value(30));

        System.out.println("-> OK: Regla actualizada correctamente.");
    }

    @Test
    @Order(4)
    @DisplayName("4. CLIENTE no puede listar reglas de cupón (HTTP 403)")
    void clienteNoDeberiaListarReglas() throws Exception {
        System.out.println("-> Ejecutando ReglaCuponControllerIT: clienteNoDeberiaListarReglas");

        mockMvc.perform(get("/cupones/reglas")
                        .header("Authorization", "Bearer " + clienteToken))
                .andExpect(status().isForbidden());

        System.out.println("-> OK: Listado de reglas rechazado con HTTP 403 para rol CLIENTE.");
    }

    @Test
    @Order(5)
    @DisplayName("5. CLIENTE no puede obtener una regla (HTTP 403)")
    void clienteNoDeberiaObtenerRegla() throws Exception {
        System.out.println("-> Ejecutando ReglaCuponControllerIT: clienteNoDeberiaObtenerRegla");

        mockMvc.perform(get("/cupones/reglas/BIENVENIDA")
                        .header("Authorization", "Bearer " + clienteToken))
                .andExpect(status().isForbidden());

        System.out.println("-> OK: Consulta de regla rechazada con HTTP 403 para rol CLIENTE.");
    }

    @Test
    @Order(6)
    @DisplayName("6. CLIENTE no puede actualizar una regla de cupón (HTTP 403)")
    void clienteNoDeberiaActualizarRegla() throws Exception {
        System.out.println("-> Ejecutando ReglaCuponControllerIT: clienteNoDeberiaActualizarRegla");

        UpdateReglaCuponRequest request = UpdateReglaCuponRequest.builder()
                .activo(true)
                .tipoDescuento(TipoDescuento.PORCENTAJE)
                .valor(new BigDecimal("50.00"))
                .diasValidez(10)
                .descripcion("Intento de modificación no autorizada")
                .build();

        mockMvc.perform(put("/cupones/reglas/BIENVENIDA")
                        .header("Authorization", "Bearer " + clienteToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        System.out.println("-> OK: Actualizacion de regla rechazada con HTTP 403 para rol CLIENTE.");
    }

    @Test
    @Order(7)
    @DisplayName("7. Petición sin token debe fallar con HTTP 403")
    void sinTokenDeberiaFallar() throws Exception {
        System.out.println("-> Ejecutando ReglaCuponControllerIT: sinTokenDeberiaFallar");

        mockMvc.perform(get("/cupones/reglas"))
                .andExpect(status().isForbidden());

        System.out.println("-> OK: Peticion sin token rechazada con HTTP 403.");
    }
}
