package com.Trabajo_Final_Beltran.integration.controller;

import com.Trabajo_Final_Beltran.dto.request.LoginRequest;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Flujo de Autenticación - Integration Test")
class AuthControllerIT {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Autowired
    private EstablecimientoRepository establecimientoRepository;

    @MockitoBean
    private EmailService emailService;

    private static String jwtToken;

    @BeforeAll
    void setupEstablecimiento() {
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
                    .diasHabiles(Set.of(DiaSemana.LUNES, DiaSemana.MARTES, DiaSemana.MIERCOLES, DiaSemana.JUEVES, DiaSemana.VIERNES))
                    .tipoServicio(TipoServicio.AMBOS)
                    .estado(EstadoEstablecimiento.ACTIVO)
                    .build();
            establecimientoRepository.save(est);
        }
    }

    @Test
    @Order(1)
    @DisplayName("1. Debe registrar un nuevo cliente exitosamente y retornar token")
    void deberiaRegistrarClienteYRetornarToken() throws Exception {
        System.out.println("-> Ejecutando AuthControllerIT: deberiaRegistrarClienteYRetornarToken");
        RegisterRequest request = RegisterRequest.builder()
                .nombre("Martin")
                .apellido("Gomez")
                .email("martin.gomez@test.com")
                .password("Password123")
                .dni("38123456")
                .telefono("1133445566")
                .fechaNacimiento(LocalDate.of(1994, 6, 20))
                .build();

        String response = mockMvc.perform(post("/auth/registro-cliente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn().getResponse().getContentAsString();

        Matcher matcher = Pattern.compile("\"token\":\"(.*?)\"").matcher(response);
        if (matcher.find()) {
            jwtToken = matcher.group(1);
        }

        assertThat(jwtToken).isNotNull().isNotBlank();
        System.out.println("-> OK: Cliente registrado y token obtenido correctamente.");
    }

    @Test
    @Order(2)
    @DisplayName("2. Login con contraseña incorrecta debe fallar con HTTP 400")
    void loginConPasswordIncorrectoDeberiaFallar() throws Exception {
        System.out.println("-> Ejecutando AuthControllerIT: loginConPasswordIncorrectoDeberiaFallar");
        LoginRequest request = new LoginRequest("martin.gomez@test.com", "PasswordErroneo1");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Credenciales inválidas"));
        System.out.println("-> OK: Login rechazado correctamente con HTTP 400 por credenciales invalidas.");
    }

    @Test
    @Order(3)
    @DisplayName("3. Login con credenciales correctas debe funcionar y retornar nuevo token")
    void loginConCredencialesCorrectasDeberiaFuncionar() throws Exception {
        System.out.println("-> Ejecutando AuthControllerIT: loginConCredencialesCorrectasDeberiaFuncionar");
        LoginRequest request = new LoginRequest("martin.gomez@test.com", "Password123");

        String response = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn().getResponse().getContentAsString();

        Matcher matcher = Pattern.compile("\"token\":\"(.*?)\"").matcher(response);
        if (matcher.find()) {
            jwtToken = matcher.group(1);
        }

        assertThat(jwtToken).isNotNull().isNotBlank();
        System.out.println("-> OK: Login exitoso y nuevo token obtenido.");
    }

    @Test
    @Order(4)
    @DisplayName("4. Debe acceder a endpoint protegido (/pedidos) con token JWT válido")
    void deberiaAccederAEndpointProtegidoConTokenValido() throws Exception {
        System.out.println("-> Ejecutando AuthControllerIT: deberiaAccederAEndpointProtegidoConTokenValido");
        mockMvc.perform(get("/pedidos")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());
        System.out.println("-> OK: Acceso a endpoint protegido exitoso.");
    }

    @Test
    @Order(5)
    @DisplayName("5. Debe rechazar petición a endpoint protegido si no se envía token")
    void deberiaFallarAccesoSinToken() throws Exception {
        System.out.println("-> Ejecutando AuthControllerIT: deberiaFallarAccesoSinToken");
        mockMvc.perform(get("/pedidos"))
                .andExpect(status().isForbidden());
        System.out.println("-> OK: Peticion sin token rechazada con HTTP 403 Forbidden.");
    }

    @Test
    @Order(6)
    @DisplayName("6. Debe cerrar sesión invalidando el token JWT")
    void deberiaCerrarSesionCorrectamente() throws Exception {
        System.out.println("-> Ejecutando AuthControllerIT: deberiaCerrarSesionCorrectamente");
        mockMvc.perform(post("/auth/logout")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());
        System.out.println("-> OK: Sesion cerrada correctamente.");
    }
}
