package com.Trabajo_Final_Beltran.integration.controller;

import com.Trabajo_Final_Beltran.dto.request.RegisterRequest;
import com.Trabajo_Final_Beltran.dto.request.UpdatePerfilRequest;
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
@DisplayName("Flujo de Perfil - Integration Test")
class PerfilControllerIT {

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
                .nombre("Sofia")
                .apellido("Perfil")
                .email("sofia.perfil@test.com")
                .password("Password123")
                .dni("42333444")
                .telefono("1166778899")
                .fechaNacimiento(LocalDate.of(1995, 7, 22))
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
    @DisplayName("1. Debe obtener el perfil del usuario autenticado (HTTP 200)")
    void deberiaObtenerMiPerfilCorrectamente() throws Exception {
        System.out.println("-> Ejecutando PerfilControllerIT: deberiaObtenerMiPerfilCorrectamente");

        mockMvc.perform(get("/perfil")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("sofia.perfil@test.com"))
                .andExpect(jsonPath("$.nombre").value("Sofia"))
                .andExpect(jsonPath("$.apellido").value("Perfil"));

        System.out.println("-> OK: Perfil del usuario obtenido correctamente.");
    }

    @Test
    @Order(2)
    @DisplayName("2. Debe actualizar el perfil del usuario autenticado (HTTP 200)")
    void deberiaActualizarMiPerfilCorrectamente() throws Exception {
        System.out.println("-> Ejecutando PerfilControllerIT: deberiaActualizarMiPerfilCorrectamente");

        UpdatePerfilRequest request = new UpdatePerfilRequest();
        request.setNombre("Sofia Actualizada");
        request.setApellido("Perfil Editado");
        request.setTelefono("1199887766");
        request.setFechaNacimiento(LocalDate.of(1995, 8, 30));

        mockMvc.perform(put("/perfil")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Sofia Actualizada"))
                .andExpect(jsonPath("$.apellido").value("Perfil Editado"));

        System.out.println("-> OK: Perfil actualizado correctamente.");
    }

    @Test
    @Order(3)
    @DisplayName("3. Cambiar password con datos incorrectos debe fallar con HTTP 400")
    void cambiarPasswordConDatosIncorrectosDeberiaFallar() throws Exception {
        System.out.println("-> Ejecutando PerfilControllerIT: cambiarPasswordConDatosIncorrectosDeberiaFallar");

        // Enviar body vacio - los campos son obligatorios
        mockMvc.perform(put("/perfil/password")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        System.out.println("-> OK: Cambio de password rechazado correctamente con HTTP 400.");
    }

    @Test
    @Order(4)
    @DisplayName("4. Listar todos los usuarios sin rol ADMIN debe fallar con HTTP 403")
    void listarUsuariosSinRolAdminDeberiaFallar() throws Exception {
        System.out.println("-> Ejecutando PerfilControllerIT: listarUsuariosSinRolAdminDeberiaFallar");

        // El usuario es CLIENTE, el endpoint requiere ADMIN
        mockMvc.perform(get("/perfil/usuarios")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isForbidden());

        System.out.println("-> OK: Listado de usuarios rechazado con HTTP 403 para rol CLIENTE.");
    }

    @Test
    @Order(5)
    @DisplayName("5. Obtener un usuario por ID sin rol ADMIN debe fallar con HTTP 403")
    void obtenerUsuarioPorIdSinRolAdminDeberiaFallar() throws Exception {
        System.out.println("-> Ejecutando PerfilControllerIT: obtenerUsuarioPorIdSinRolAdminDeberiaFallar");

        mockMvc.perform(get("/perfil/usuarios/1")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isForbidden());

        System.out.println("-> OK: Consulta de usuario por ID rechazada con HTTP 403 para rol CLIENTE.");
    }

    @Test
    @Order(6)
    @DisplayName("6. Obtener perfil sin token debe fallar con HTTP 403")
    void obtenerPerfilSinTokenDeberiaFallar() throws Exception {
        System.out.println("-> Ejecutando PerfilControllerIT: obtenerPerfilSinTokenDeberiaFallar");

        mockMvc.perform(get("/perfil"))
                .andExpect(status().isForbidden());

        System.out.println("-> OK: Peticion sin token rechazada con HTTP 403.");
    }
}
