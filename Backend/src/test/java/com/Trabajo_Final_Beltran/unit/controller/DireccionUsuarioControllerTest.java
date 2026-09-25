package com.Trabajo_Final_Beltran.unit.controller;

import com.Trabajo_Final_Beltran.controller.DireccionUsuarioController;
import com.Trabajo_Final_Beltran.dto.request.CreateDireccionRequest;
import com.Trabajo_Final_Beltran.dto.request.UpdateDireccionRequest;
import com.Trabajo_Final_Beltran.dto.response.DireccionResponse;
import com.Trabajo_Final_Beltran.security.JwtAuthenticationFilter;
import com.Trabajo_Final_Beltran.security.RateLimitFilter;
import com.Trabajo_Final_Beltran.service.DireccionService;
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

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        value = DireccionUsuarioController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {JwtAuthenticationFilter.class, RateLimitFilter.class}
        ),
        excludeAutoConfiguration = SecurityAutoConfiguration.class
)
@DisplayName("DireccionUsuarioController - Unit Tests")
class DireccionUsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @MockitoBean
    private DireccionService direccionService;

    @MockitoBean
    private CacheManager cacheManager;

    @Test
    @WithMockUser(roles = "CLIENTE")
    @DisplayName("POST /direcciones - Debe crear direccion con HTTP 200")
    void crearDireccion_debeRetornarOk() throws Exception {
        System.out.println("-> Ejecutando DireccionUsuarioControllerTest: crearDireccion_debeRetornarOk");

        CreateDireccionRequest request = CreateDireccionRequest.builder()
                .calle("Av Corrientes")
                .numero("1234")
                .localidad("Caba")
                .build();

        DireccionResponse response = DireccionResponse.builder()
                .id(1L)
                .calle("Av Corrientes")
                .numero("1234")
                .localidad("Caba")
                .esPrincipal(true)
                .build();

        when(direccionService.crearDireccion(any(CreateDireccionRequest.class))).thenReturn(response);

        mockMvc.perform(post("/direcciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.calle").value("Av Corrientes"));

        System.out.println("-> OK: crearDireccion_debeRetornarOk completado con exito.");
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    @DisplayName("GET /direcciones - Debe listar direcciones del usuario con HTTP 200")
    void listarDirecciones_debeRetornarOk() throws Exception {
        System.out.println("-> Ejecutando DireccionUsuarioControllerTest: listarDirecciones_debeRetornarOk");

        when(direccionService.listarDirecciones()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/direcciones")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        System.out.println("-> OK: listarDirecciones_debeRetornarOk completado con exito.");
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    @DisplayName("PUT /direcciones/{id} - Debe editar direccion con HTTP 200")
    void editarDireccion_debeRetornarOk() throws Exception {
        System.out.println("-> Ejecutando DireccionUsuarioControllerTest: editarDireccion_debeRetornarOk");

        UpdateDireccionRequest request = UpdateDireccionRequest.builder()
                .calle("Florida")
                .numero("800")
                .localidad("Caba")
                .build();

        DireccionResponse response = DireccionResponse.builder()
                .id(1L)
                .calle("Florida")
                .numero("800")
                .localidad("Caba")
                .build();

        when(direccionService.editarDireccion(eq(1L), any(UpdateDireccionRequest.class))).thenReturn(response);

        mockMvc.perform(put("/direcciones/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.calle").value("Florida"));

        System.out.println("-> OK: editarDireccion_debeRetornarOk completado con exito.");
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    @DisplayName("DELETE /direcciones/{id} - Debe eliminar direccion con HTTP 204")
    void eliminarDireccion_debeRetornarNoContent() throws Exception {
        System.out.println("-> Ejecutando DireccionUsuarioControllerTest: eliminarDireccion_debeRetornarNoContent");

        mockMvc.perform(delete("/direcciones/1"))
                .andExpect(status().isNoContent());

        System.out.println("-> OK: eliminarDireccion_debeRetornarNoContent completado con exito.");
    }
}
