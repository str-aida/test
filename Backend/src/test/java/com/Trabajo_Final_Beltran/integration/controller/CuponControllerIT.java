package com.Trabajo_Final_Beltran.integration.controller;

import com.Trabajo_Final_Beltran.dto.request.CreateCuponRequest;
import com.Trabajo_Final_Beltran.dto.request.UpdateCuponRequest;
import com.Trabajo_Final_Beltran.entity.Establecimiento;
import com.Trabajo_Final_Beltran.entity.Usuario;
import com.Trabajo_Final_Beltran.enums.*;
import com.Trabajo_Final_Beltran.repository.CuponRepository;
import com.Trabajo_Final_Beltran.repository.CuponUsuarioRepository;
import com.Trabajo_Final_Beltran.repository.EstablecimientoRepository;
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
@DisplayName("CuponController - Integration Tests")
class CuponControllerIT {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Autowired
    private CuponRepository cuponRepository;

    @Autowired
    private CuponUsuarioRepository cuponUsuarioRepository;

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
    private Long cuponCreadoId;

    @BeforeAll
    void setup() {
        cuponUsuarioRepository.deleteAll();
        cuponRepository.deleteAll();

        Establecimiento est;
        if (establecimientoRepository.count() == 0) {
            est = establecimientoRepository.save(Establecimiento.builder()
                    .nombre("Gestia Cupones")
                    .razonSocial("Gestia Cupones S.A.")
                    .cuit("30-44556677-8")
                    .email("contacto.cupones@gestia.com")
                    .telefono("1144556677")
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

        Usuario adminUser = usuarioRepository.findByEmail("admin.cupones@test.com")
                .orElseGet(() -> usuarioRepository.save(Usuario.builder()
                        .nombre("Admin")
                        .apellido("Cupones")
                        .email("admin.cupones@test.com")
                        .password("$2a$10$dummyhashedpassword12345678901234567890")
                        .dni("81001122")
                        .telefono("1188776655")
                        .fechaNacimiento(LocalDate.of(1987, 5, 20))
                        .rol(Rol.ADMIN)
                        .estado(Estado.ACTIVO)
                        .establecimiento(est)
                        .build()));

        Usuario clienteUser = usuarioRepository.findByEmail("cliente.cupones@test.com")
                .orElseGet(() -> usuarioRepository.save(Usuario.builder()
                        .nombre("Cliente")
                        .apellido("Cupones")
                        .email("cliente.cupones@test.com")
                        .password("$2a$10$dummyhashedpassword12345678901234567890")
                        .dni("82001122")
                        .telefono("1188776656")
                        .fechaNacimiento(LocalDate.of(1996, 6, 15))
                        .rol(Rol.CLIENTE)
                        .estado(Estado.ACTIVO)
                        .establecimiento(est)
                        .build()));

        adminToken = jwtService.generateToken(adminUser);
        clienteToken = jwtService.generateToken(clienteUser);
    }

    @Test
    @Order(1)
    @DisplayName("1. ADMIN puede crear un cupón exitosamente (HTTP 201)")
    void adminDeberiaCrearCupon() throws Exception {
        System.out.println("-> Ejecutando CuponControllerIT: adminDeberiaCrearCupon");

        CreateCuponRequest request = CreateCuponRequest.builder()
                .codigo("BIENVENIDA20")
                .tipoDescuento(TipoDescuento.PORCENTAJE)
                .valor(new BigDecimal("20.00"))
                .fechaInicio(LocalDate.now())
                .fechaFin(LocalDate.now().plusMonths(1))
                .usoMaximo(100)
                .tipoAsignacion(TipoAsignacionCupon.MANUAL)
                .build();

        String response = mockMvc.perform(post("/cupones")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.codigo").value("BIENVENIDA20"))
                .andReturn().getResponse().getContentAsString();

        Matcher matcher = Pattern.compile("\"id\":(\\d+)").matcher(response);
        if (matcher.find()) {
            cuponCreadoId = Long.parseLong(matcher.group(1));
        }

        assertThat(cuponCreadoId).isNotNull();
        System.out.println("-> OK: Cupon creado con ID " + cuponCreadoId);
    }

    @Test
    @Order(2)
    @DisplayName("2. ADMIN puede listar cupones (HTTP 200)")
    void adminDeberiaListarCupones() throws Exception {
        System.out.println("-> Ejecutando CuponControllerIT: adminDeberiaListarCupones");

        mockMvc.perform(get("/cupones")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].codigo").value("BIENVENIDA20"));

        System.out.println("-> OK: Cupones listados correctamente.");
    }

    @Test
    @Order(3)
    @DisplayName("3. ADMIN puede obtener cupón por ID (HTTP 200)")
    void adminDeberiaObtenerCuponPorId() throws Exception {
        System.out.println("-> Ejecutando CuponControllerIT: adminDeberiaObtenerCuponPorId");

        mockMvc.perform(get("/cupones/" + cuponCreadoId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value("BIENVENIDA20"));

        System.out.println("-> OK: Cupon obtenido por ID correctamente.");
    }

    @Test
    @Order(4)
    @DisplayName("4. ADMIN puede editar cupón (HTTP 200)")
    void adminDeberiaEditarCupon() throws Exception {
        System.out.println("-> Ejecutando CuponControllerIT: adminDeberiaEditarCupon");

        UpdateCuponRequest request = UpdateCuponRequest.builder()
                .tipoDescuento(TipoDescuento.PORCENTAJE)
                .valor(new BigDecimal("25.00"))
                .fechaInicio(LocalDate.now())
                .fechaFin(LocalDate.now().plusMonths(2))
                .usoMaximo(200)
                .estado(EstadoCupon.ACTIVO)
                .build();

        mockMvc.perform(put("/cupones/" + cuponCreadoId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valor").value(25.00));

        System.out.println("-> OK: Cupon editado correctamente.");
    }

    @Test
    @Order(5)
    @DisplayName("5. CLIENTE puede consultar mis cupones (HTTP 200)")
    void clienteDeberiaConsultarMisCupones() throws Exception {
        System.out.println("-> Ejecutando CuponControllerIT: clienteDeberiaConsultarMisCupones");

        mockMvc.perform(get("/cupones/mis-cupones")
                        .header("Authorization", "Bearer " + clienteToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        System.out.println("-> OK: Mis cupones consultados exitosamente.");
    }

    @Test
    @Order(6)
    @DisplayName("6. ADMIN puede desactivar un cupón (HTTP 204)")
    void adminDeberiaDesactivarCupon() throws Exception {
        System.out.println("-> Ejecutando CuponControllerIT: adminDeberiaDesactivarCupon");

        mockMvc.perform(put("/cupones/" + cuponCreadoId + "/desactivar")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        System.out.println("-> OK: Cupon desactivado correctamente.");
    }

    @Test
    @Order(7)
    @DisplayName("7. CLIENTE no puede crear cupones (HTTP 403)")
    void clienteNoDeberiaCrearCupones() throws Exception {
        System.out.println("-> Ejecutando CuponControllerIT: clienteNoDeberiaCrearCupones");

        CreateCuponRequest request = CreateCuponRequest.builder()
                .codigo("HACK50")
                .tipoDescuento(TipoDescuento.PORCENTAJE)
                .valor(new BigDecimal("50.00"))
                .fechaInicio(LocalDate.now())
                .fechaFin(LocalDate.now().plusMonths(1))
                .tipoAsignacion(TipoAsignacionCupon.BIENVENIDA)
                .build();

        mockMvc.perform(post("/cupones")
                        .header("Authorization", "Bearer " + clienteToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        System.out.println("-> OK: Creacion de cupon rechazada con HTTP 403 para rol CLIENTE.");
    }

    @Test
    @Order(8)
    @DisplayName("8. Peticion sin token debe fallar con HTTP 403")
    void sinTokenDeberiaFallar() throws Exception {
        System.out.println("-> Ejecutando CuponControllerIT: sinTokenDeberiaFallar");

        mockMvc.perform(get("/cupones"))
                .andExpect(status().isForbidden());

        System.out.println("-> OK: Peticion sin token rechazada con HTTP 403.");
    }
}
