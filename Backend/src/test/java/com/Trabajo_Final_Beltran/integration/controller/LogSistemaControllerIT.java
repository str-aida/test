package com.Trabajo_Final_Beltran.integration.controller;

import com.Trabajo_Final_Beltran.entity.Establecimiento;
import com.Trabajo_Final_Beltran.entity.LogSistema;
import com.Trabajo_Final_Beltran.entity.Usuario;
import com.Trabajo_Final_Beltran.enums.*;
import com.Trabajo_Final_Beltran.repository.EstablecimientoRepository;
import com.Trabajo_Final_Beltran.repository.LogSistemaRepository;
import com.Trabajo_Final_Beltran.repository.UsuarioRepository;
import com.Trabajo_Final_Beltran.security.JwtService;
import com.Trabajo_Final_Beltran.service.EmailService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("LogSistemaController - Integration Tests")
class LogSistemaControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LogSistemaRepository logSistemaRepository;

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
        logSistemaRepository.deleteAll();

        Establecimiento est;
        if (establecimientoRepository.count() == 0) {
            est = establecimientoRepository.save(Establecimiento.builder()
                    .nombre("Gestia Logs")
                    .razonSocial("Gestia Logs S.A.")
                    .cuit("30-99887766-5")
                    .email("contacto.logs@gestia.com")
                    .telefono("1133445566")
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

        Usuario adminUser = usuarioRepository.findByEmail("admin.logs@test.com")
                .orElseGet(() -> usuarioRepository.save(Usuario.builder()
                        .nombre("Admin")
                        .apellido("Logs")
                        .email("admin.logs@test.com")
                        .password("$2a$10$dummyhashedpassword12345678901234567890")
                        .dni("91001122")
                        .telefono("1199887766")
                        .fechaNacimiento(LocalDate.of(1988, 1, 1))
                        .rol(Rol.ADMIN)
                        .estado(Estado.ACTIVO)
                        .establecimiento(est)
                        .build()));

        Usuario clienteUser = usuarioRepository.findByEmail("cliente.logs@test.com")
                .orElseGet(() -> usuarioRepository.save(Usuario.builder()
                        .nombre("Cliente")
                        .apellido("Logs")
                        .email("cliente.logs@test.com")
                        .password("$2a$10$dummyhashedpassword12345678901234567890")
                        .dni("92001122")
                        .telefono("1199887767")
                        .fechaNacimiento(LocalDate.of(1995, 2, 2))
                        .rol(Rol.CLIENTE)
                        .estado(Estado.ACTIVO)
                        .establecimiento(est)
                        .build()));

        adminToken = jwtService.generateToken(adminUser);
        clienteToken = jwtService.generateToken(clienteUser);

        logSistemaRepository.save(LogSistema.builder()
                .tablaAfectada("productos")
                .idRegistro(1L)
                .referencia("PROD-001")
                .accion("CREAR")
                .campoModificado("precio")
                .valorAnterior("100")
                .valorNuevo("150")
                .usuario(adminUser)
                .fecha(LocalDateTime.now())
                .descripcion("Creación de nuevo producto")
                .tipoOperacion(TipoOperacion.INSERT)
                .build());
    }

    @Test
    @Order(1)
    @DisplayName("1. ADMIN puede listar logs de auditoría exitosamente (HTTP 200)")
    void adminDeberiaListarLogs() throws Exception {
        System.out.println("-> Ejecutando LogSistemaControllerIT: adminDeberiaListarLogs");

        mockMvc.perform(get("/logs")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());

        System.out.println("-> OK: Logs de auditoría listados correctamente por ADMIN.");
    }

    @Test
    @Order(2)
    @DisplayName("2. ADMIN puede exportar logs a PDF (HTTP 200)")
    void adminDeberiaExportarPdf() throws Exception {
        System.out.println("-> Ejecutando LogSistemaControllerIT: adminDeberiaExportarPdf");

        mockMvc.perform(get("/logs/exportar-pdf")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=auditoria.pdf"))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));

        System.out.println("-> OK: PDF de logs generado y descargado correctamente.");
    }

    @Test
    @Order(3)
    @DisplayName("3. CLIENTE no puede acceder a logs de auditoría (HTTP 403)")
    void clienteNoDeberiaAccederALogs() throws Exception {
        System.out.println("-> Ejecutando LogSistemaControllerIT: clienteNoDeberiaAccederALogs");

        mockMvc.perform(get("/logs")
                .header("Authorization", "Bearer " + clienteToken))
                .andExpect(status().isForbidden());

        System.out.println("-> OK: Acceso denegado con HTTP 403 para rol CLIENTE.");
    }

    @Test
    @Order(4)
    @DisplayName("4. Petición sin token a logs debe fallar con HTTP 403")
    void sinTokenDeberiaFallar() throws Exception {
        System.out.println("-> Ejecutando LogSistemaControllerIT: sinTokenDeberiaFallar");

        mockMvc.perform(get("/logs"))
                .andExpect(status().isForbidden());

        System.out.println("-> OK: Peticion sin token rechazada con HTTP 403.");
    }
}
