package com.Trabajo_Final_Beltran.unit.controller;

import com.Trabajo_Final_Beltran.controller.ReglaCuponController;
import com.Trabajo_Final_Beltran.dto.request.UpdateReglaCuponRequest;
import com.Trabajo_Final_Beltran.dto.response.ReglaCuponResponse;
import com.Trabajo_Final_Beltran.enums.TipoAsignacionCupon;
import com.Trabajo_Final_Beltran.enums.TipoDescuento;
import com.Trabajo_Final_Beltran.security.JwtAuthenticationFilter;
import com.Trabajo_Final_Beltran.security.RateLimitFilter;
import com.Trabajo_Final_Beltran.service.ReglaCuponService;
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

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        value = ReglaCuponController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {JwtAuthenticationFilter.class, RateLimitFilter.class}
        ),
        excludeAutoConfiguration = SecurityAutoConfiguration.class
)
@DisplayName("ReglaCuponController - Unit Tests")
class ReglaCuponControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @MockitoBean
    private ReglaCuponService reglaCuponService;

    @MockitoBean
    private CacheManager cacheManager;

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /cupones/reglas - Debe listar reglas cuando es ADMIN con HTTP 200")
    void listarReglas_debeRetornarOk() throws Exception {
        System.out.println("-> Ejecutando ReglaCuponControllerTest: listarReglas_debeRetornarOk");

        when(reglaCuponService.listarReglas()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/cupones/reglas")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        System.out.println("-> OK: listarReglas_debeRetornarOk completado con exito.");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /cupones/reglas/{tipoAsignacion} - Debe obtener regla con HTTP 200")
    void obtenerRegla_debeRetornarOk() throws Exception {
        System.out.println("-> Ejecutando ReglaCuponControllerTest: obtenerRegla_debeRetornarOk");

        ReglaCuponResponse response = ReglaCuponResponse.builder()
                .id(1L)
                .tipoAsignacion(TipoAsignacionCupon.BIENVENIDA)
                .tipoDescuento(TipoDescuento.PORCENTAJE)
                .valor(BigDecimal.valueOf(15))
                .activo(true)
                .diasValidez(30)
                .build();

        when(reglaCuponService.obtenerReglaPorTipo(TipoAsignacionCupon.BIENVENIDA)).thenReturn(response);

        mockMvc.perform(get("/cupones/reglas/BIENVENIDA")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.tipoAsignacion").value("BIENVENIDA"));

        System.out.println("-> OK: obtenerRegla_debeRetornarOk completado con exito.");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("PUT /cupones/reglas/{tipoAsignacion} - Debe actualizar regla con HTTP 200")
    void actualizarRegla_debeRetornarOk() throws Exception {
        System.out.println("-> Ejecutando ReglaCuponControllerTest: actualizarRegla_debeRetornarOk");

        UpdateReglaCuponRequest request = UpdateReglaCuponRequest.builder()
                .tipoDescuento(TipoDescuento.PORCENTAJE)
                .valor(BigDecimal.valueOf(20))
                .diasValidez(45)
                .activo(true)
                .build();

        ReglaCuponResponse response = ReglaCuponResponse.builder()
                .id(1L)
                .tipoAsignacion(TipoAsignacionCupon.BIENVENIDA)
                .tipoDescuento(TipoDescuento.PORCENTAJE)
                .valor(BigDecimal.valueOf(20))
                .activo(true)
                .diasValidez(45)
                .build();

        when(reglaCuponService.actualizarRegla(eq(TipoAsignacionCupon.BIENVENIDA), any(UpdateReglaCuponRequest.class)))
                .thenReturn(response);

        mockMvc.perform(put("/cupones/reglas/BIENVENIDA")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valor").value(20));

        System.out.println("-> OK: actualizarRegla_debeRetornarOk completado con exito.");
    }
}
