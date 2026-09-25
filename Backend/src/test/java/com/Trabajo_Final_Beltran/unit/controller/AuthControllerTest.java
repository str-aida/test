package com.Trabajo_Final_Beltran.unit.controller;

import com.Trabajo_Final_Beltran.controller.AuthController;
import com.Trabajo_Final_Beltran.dto.request.LoginRequest;
import com.Trabajo_Final_Beltran.dto.request.RegisterRequest;
import com.Trabajo_Final_Beltran.dto.response.AuthResponse;
import com.Trabajo_Final_Beltran.security.JwtAuthenticationFilter;
import com.Trabajo_Final_Beltran.security.JwtService;
import com.Trabajo_Final_Beltran.security.RateLimitFilter;
import com.Trabajo_Final_Beltran.security.TokenBlacklistService;
import com.Trabajo_Final_Beltran.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        value = AuthController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {JwtAuthenticationFilter.class, RateLimitFilter.class}
        ),
        excludeAutoConfiguration = SecurityAutoConfiguration.class
)
@DisplayName("AuthController - Unit Tests")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private TokenBlacklistService tokenBlacklistService;

    @MockitoBean
    private CacheManager cacheManager;

    @Test
    @DisplayName("POST /auth/login - Debe retornar token con HTTP 200")
    void login_debeRetornarToken() throws Exception {
        System.out.println("-> Ejecutando test: login_debeRetornarToken");
        LoginRequest request = new LoginRequest("test@email.com", "Password123");
        AuthResponse response = new AuthResponse("dummy-jwt-token");

        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("dummy-jwt-token"));
        System.out.println("-> OK: login_debeRetornarToken completado con exito.");
    }

    @Test
    @DisplayName("POST /auth/registro-cliente - Debe retornar token al registrar cliente")
    void registroCliente_debeRetornarToken() throws Exception {
        System.out.println("-> Ejecutando test: registroCliente_debeRetornarToken");
        RegisterRequest request = RegisterRequest.builder()
                .nombre("Juan")
                .apellido("Perez")
                .email("juan@test.com")
                .password("Password1")
                .dni("12345678")
                .telefono("1122334455")
                .fechaNacimiento(LocalDate.of(1995, 5, 15))
                .build();

        AuthResponse response = new AuthResponse("client-jwt-token");
        when(authService.registerCliente(any(RegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/auth/registro-cliente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("client-jwt-token"));
        System.out.println("-> OK: registroCliente_debeRetornarToken completado con exito.");
    }

    @Test
    @DisplayName("POST /auth/registro-cliente - Debe fallar con 400 si faltan campos obligatorios")
    void registroCliente_debeFallarConValidacionInvalida() throws Exception {
        System.out.println("-> Ejecutando test: registroCliente_debeFallarConValidacionInvalida");
        RegisterRequest invalidRequest = RegisterRequest.builder()
                .nombre("") 
                .email("invalido") 
                .build();

        mockMvc.perform(post("/auth/registro-cliente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
        System.out.println("-> OK: Validacion fallida esperada (HTTP 400 Bad Request) recibida correctamente.");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /auth/crear-personal - Debe crear empleado cuando es ADMIN")
    void crearPersonal_debeRetornarMensajeExitoso() throws Exception {
        System.out.println("-> Ejecutando test: crearPersonal_debeRetornarMensajeExitoso");
        RegisterRequest request = RegisterRequest.builder()
                .nombre("Carlos")
                .apellido("Gomez")
                .email("carlos@test.com")
                .password("Password1")
                .dni("87654321")
                .telefono("1199887766")
                .fechaNacimiento(LocalDate.of(1990, 1, 10))
                .build();

        when(authService.createPersonal(any(RegisterRequest.class)))
                .thenReturn("Empleado creado correctamente");

        mockMvc.perform(post("/auth/crear-personal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
        System.out.println("-> OK: crearPersonal_debeRetornarMensajeExitoso completado con exito.");
    }
}
