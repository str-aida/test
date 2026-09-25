package com.Trabajo_Final_Beltran.integration.controller;

import com.Trabajo_Final_Beltran.dto.request.CreateDireccionRequest;
import com.Trabajo_Final_Beltran.dto.request.RegisterRequest;
import com.Trabajo_Final_Beltran.dto.request.UpdateDireccionRequest;
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
@DisplayName("Flujo de Direcciones - Integration Test")
class DireccionUsuarioControllerIT {

        @Autowired
        private MockMvc mockMvc;

        private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

        @Autowired
        private EstablecimientoRepository establecimientoRepository;

        @MockitoBean
        private EmailService emailService;

        private String jwtToken;
        private Long direccionId;

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
                                .nombre("Laura")
                                .apellido("Perez")
                                .email("laura.direccion@test.com")
                                .password("Password123")
                                .dni("40111222")
                                .telefono("1144556677")
                                .fechaNacimiento(LocalDate.of(1998, 3, 10))
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
        @DisplayName("1. Debe crear una direccion para el usuario autenticado (HTTP 200)")
        void deberiaCrearDireccionExitosamente() throws Exception {
                System.out.println("-> Ejecutando DireccionUsuarioControllerIT: deberiaCrearDireccionExitosamente");

                CreateDireccionRequest request = CreateDireccionRequest.builder()
                                .nombre("Casa")
                                .calle("Av. Corrientes")
                                .numero("1234")
                                .localidad("CABA")
                                .piso("3")
                                .departamento("B")
                                .codigoPostal("1043")
                                .referencia("Esquina con Callao")
                                .esPrincipal(true)
                                .build();

                String response = mockMvc.perform(post("/direcciones")
                                .header("Authorization", "Bearer " + jwtToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").exists())
                                .andExpect(jsonPath("$.calle").value("Av Corrientes"))
                                .andExpect(jsonPath("$.localidad").value("Caba"))
                                .andReturn().getResponse().getContentAsString();

                Matcher matcher = Pattern.compile("\"id\":(\\d+)").matcher(response);
                if (matcher.find()) {
                        direccionId = Long.parseLong(matcher.group(1));
                }
                assertThat(direccionId).isNotNull();
                System.out.println("-> OK: Direccion creada con ID: " + direccionId);
        }

        @Test
        @Order(2)
        @DisplayName("2. Debe listar las direcciones del usuario autenticado (HTTP 200)")
        void deberiaListarDireccionesDelUsuario() throws Exception {
                System.out.println("-> Ejecutando DireccionUsuarioControllerIT: deberiaListarDireccionesDelUsuario");

                mockMvc.perform(get("/direcciones")
                                .header("Authorization", "Bearer " + jwtToken))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$").isArray())
                                .andExpect(jsonPath("$[0].id").exists());

                System.out.println("-> OK: Listado de direcciones retornado correctamente.");
        }

        @Test
        @Order(3)
        @DisplayName("3. Debe editar una direccion existente (HTTP 200)")
        void deberiaEditarDireccionExitosamente() throws Exception {
                System.out.println("-> Ejecutando DireccionUsuarioControllerIT: deberiaEditarDireccionExitosamente");

                UpdateDireccionRequest request = UpdateDireccionRequest.builder()
                                .nombre("Trabajo")
                                .calle("Florida")
                                .numero("800")
                                .localidad("CABA")
                                .codigoPostal("1005")
                                .build();

                mockMvc.perform(put("/direcciones/" + direccionId)
                                .header("Authorization", "Bearer " + jwtToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.calle").value("Florida"))
                                .andExpect(jsonPath("$.numero").value("800"));

                System.out.println("-> OK: Direccion actualizada correctamente.");
        }

        @Test
        @Order(4)
        @DisplayName("4. Debe marcar una direccion como principal (HTTP 200)")
        void deberiaMarcarDireccionComoPrincipal() throws Exception {
                System.out.println("-> Ejecutando DireccionUsuarioControllerIT: deberiaMarcarDireccionComoPrincipal");

                mockMvc.perform(put("/direcciones/" + direccionId + "/principal")
                                .header("Authorization", "Bearer " + jwtToken))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(direccionId));

                System.out.println("-> OK: Direccion marcada como principal correctamente.");
        }

        @Test
        @Order(5)
        @DisplayName("5. Debe eliminar una direccion (HTTP 204)")
        void deberiaEliminarDireccionExitosamente() throws Exception {
                System.out.println("-> Ejecutando DireccionUsuarioControllerIT: deberiaEliminarDireccionExitosamente");

                mockMvc.perform(delete("/direcciones/" + direccionId)
                                .header("Authorization", "Bearer " + jwtToken))
                                .andExpect(status().isNoContent());

                System.out.println("-> OK: Direccion eliminada correctamente con HTTP 204.");
        }

        @Test
        @Order(6)
        @DisplayName("6. Crear direccion sin token debe fallar con HTTP 403")
        void crearDireccionSinTokenDeberiaFallar() throws Exception {
                System.out.println("-> Ejecutando DireccionUsuarioControllerIT: crearDireccionSinTokenDeberiaFallar");

                CreateDireccionRequest request = CreateDireccionRequest.builder()
                                .calle("Sin Token Street")
                                .numero("0")
                                .localidad("Ninguna")
                                .build();

                mockMvc.perform(post("/direcciones")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isForbidden());

                System.out.println("-> OK: Peticion sin token rechazada con HTTP 403.");
        }
}
