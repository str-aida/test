package com.Trabajo_Final_Beltran.unit.controller;

import com.Trabajo_Final_Beltran.controller.EstablecimientoController;
import com.Trabajo_Final_Beltran.dto.response.EstablecimientoClienteResponse;
import com.Trabajo_Final_Beltran.dto.response.EstablecimientoResponse;
import com.Trabajo_Final_Beltran.security.JwtAuthenticationFilter;
import com.Trabajo_Final_Beltran.security.RateLimitFilter;
import com.Trabajo_Final_Beltran.service.EstablecimientoService;
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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        value = EstablecimientoController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {JwtAuthenticationFilter.class, RateLimitFilter.class}
        ),
        excludeAutoConfiguration = SecurityAutoConfiguration.class
)
@DisplayName("EstablecimientoController - Unit Tests")
class EstablecimientoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EstablecimientoService establecimientoService;

    @MockitoBean
    private CacheManager cacheManager;

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /establecimiento - Debe obtener datos del establecimiento con HTTP 200")
    void obtenerEstablecimiento_debeRetornarOk() throws Exception {
        System.out.println("-> Ejecutando EstablecimientoControllerTest: obtenerEstablecimiento_debeRetornarOk");

        EstablecimientoResponse response = EstablecimientoResponse.builder()
                .id(1L)
                .nombre("Gestia Restaurant")
                .cuit("30-12345678-9")
                .build();

        when(establecimientoService.obtenerEstablecimiento()).thenReturn(response);

        mockMvc.perform(get("/establecimiento")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("Gestia Restaurant"));

        System.out.println("-> OK: obtenerEstablecimiento_debeRetornarOk completado con exito.");
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    @DisplayName("GET /establecimiento/info - Debe obtener info pública con HTTP 200")
    void obtenerInfoClienteActual_debeRetornarOk() throws Exception {
        System.out.println("-> Ejecutando EstablecimientoControllerTest: obtenerInfoClienteActual_debeRetornarOk");

        EstablecimientoClienteResponse response = EstablecimientoClienteResponse.builder()
                .id(1L)
                .nombre("Gestia Restaurant")
                .logoUrl("http://localhost/logo.png")
                .build();

        when(establecimientoService.obtenerInfoClienteActual()).thenReturn(response);

        mockMvc.perform(get("/establecimiento/info")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Gestia Restaurant"));

        System.out.println("-> OK: obtenerInfoClienteActual_debeRetornarOk completado con exito.");
    }
}
