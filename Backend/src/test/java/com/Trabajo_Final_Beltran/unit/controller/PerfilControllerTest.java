package com.Trabajo_Final_Beltran.unit.controller;

import com.Trabajo_Final_Beltran.controller.PerfilController;
import com.Trabajo_Final_Beltran.dto.request.UpdatePerfilRequest;
import com.Trabajo_Final_Beltran.dto.response.UsuarioPerfilResponse;
import com.Trabajo_Final_Beltran.enums.Rol;
import com.Trabajo_Final_Beltran.security.JwtAuthenticationFilter;
import com.Trabajo_Final_Beltran.security.RateLimitFilter;
import com.Trabajo_Final_Beltran.service.PerfilService;
import com.Trabajo_Final_Beltran.service.UsuarioService;
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
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        value = PerfilController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {JwtAuthenticationFilter.class, RateLimitFilter.class}
        ),
        excludeAutoConfiguration = SecurityAutoConfiguration.class
)
@DisplayName("PerfilController - Unit Tests")
class PerfilControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @MockitoBean
    private UsuarioService usuarioService;

    @MockitoBean
    private PerfilService perfilService;

    @MockitoBean
    private CacheManager cacheManager;

    @Test
    @WithMockUser(roles = "CLIENTE")
    @DisplayName("GET /perfil - Debe obtener perfil del usuario autenticado con HTTP 200")
    void obtenerMiPerfil_debeRetornarOk() throws Exception {
        System.out.println("-> Ejecutando PerfilControllerTest: obtenerMiPerfil_debeRetornarOk");

        UsuarioPerfilResponse response = UsuarioPerfilResponse.builder()
                .id(1L)
                .nombre("Martin")
                .apellido("Gomez")
                .email("martin@test.com")
                .rol(Rol.CLIENTE)
                .build();

        when(perfilService.obtenerPerfil()).thenReturn(response);

        mockMvc.perform(get("/perfil")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("martin@test.com"));

        System.out.println("-> OK: obtenerMiPerfil_debeRetornarOk completado con exito.");
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    @DisplayName("PUT /perfil - Debe actualizar perfil con HTTP 200")
    void actualizarMiPerfil_debeRetornarOk() throws Exception {
        System.out.println("-> Ejecutando PerfilControllerTest: actualizarMiPerfil_debeRetornarOk");

        UpdatePerfilRequest request = new UpdatePerfilRequest();
        request.setNombre("Martin Actualizado");
        request.setApellido("Gomez");
        request.setTelefono("1122334455");
        request.setFechaNacimiento(LocalDate.of(1995, 5, 10));

        UsuarioPerfilResponse response = UsuarioPerfilResponse.builder()
                .id(1L)
                .nombre("Martin Actualizado")
                .apellido("Gomez")
                .email("martin@test.com")
                .rol(Rol.CLIENTE)
                .build();

        when(perfilService.actualizarPerfil(any(UpdatePerfilRequest.class))).thenReturn(response);

        mockMvc.perform(put("/perfil")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Martin Actualizado"));

        System.out.println("-> OK: actualizarMiPerfil_debeRetornarOk completado con exito.");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /perfil/usuarios - Debe listar usuarios cuando es ADMIN con HTTP 200")
    void listarUsuarios_debeRetornarOk() throws Exception {
        System.out.println("-> Ejecutando PerfilControllerTest: listarUsuarios_debeRetornarOk");

        when(usuarioService.listarUsuarios(any(), any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/perfil/usuarios")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        System.out.println("-> OK: listarUsuarios_debeRetornarOk completado con exito.");
    }
}
