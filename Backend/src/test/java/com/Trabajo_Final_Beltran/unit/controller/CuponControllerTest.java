package com.Trabajo_Final_Beltran.unit.controller;

import com.Trabajo_Final_Beltran.controller.CuponController;
import com.Trabajo_Final_Beltran.dto.request.CreateCuponRequest;
import com.Trabajo_Final_Beltran.dto.response.CuponResponse;
import com.Trabajo_Final_Beltran.enums.EstadoCupon;
import com.Trabajo_Final_Beltran.enums.TipoAsignacionCupon;
import com.Trabajo_Final_Beltran.enums.TipoDescuento;
import com.Trabajo_Final_Beltran.security.JwtAuthenticationFilter;
import com.Trabajo_Final_Beltran.security.RateLimitFilter;
import com.Trabajo_Final_Beltran.service.AsignacionCuponService;
import com.Trabajo_Final_Beltran.service.CuponService;
import com.Trabajo_Final_Beltran.service.CuponUsuarioService;
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
import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        value = CuponController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {JwtAuthenticationFilter.class, RateLimitFilter.class}
        ),
        excludeAutoConfiguration = SecurityAutoConfiguration.class
)
@DisplayName("CuponController - Unit Tests")
class CuponControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @MockitoBean
    private CuponService cuponService;

    @MockitoBean
    private CuponUsuarioService cuponUsuarioService;

    @MockitoBean
    private AsignacionCuponService asignacionCuponService;

    @MockitoBean
    private CacheManager cacheManager;

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /cupones - Debe crear cupon con HTTP 201")
    void crearCupon_debeRetornarCreated() throws Exception {
        System.out.println("-> Ejecutando CuponControllerTest: crearCupon_debeRetornarCreated");

        CreateCuponRequest request = CreateCuponRequest.builder()
                .codigo("PROMO2026")
                .tipoDescuento(TipoDescuento.PORCENTAJE)
                .valor(BigDecimal.valueOf(15))
                .fechaInicio(LocalDate.now())
                .fechaFin(LocalDate.now().plusDays(30))
                .tipoAsignacion(TipoAsignacionCupon.MANUAL)
                .build();

        CuponResponse response = CuponResponse.builder()
                .id(1L)
                .codigo("PROMO2026")
                .tipoDescuento(TipoDescuento.PORCENTAJE)
                .valor(BigDecimal.valueOf(15))
                .estado(EstadoCupon.ACTIVO)
                .build();

        when(cuponService.crearCupon(any(CreateCuponRequest.class))).thenReturn(response);

        mockMvc.perform(post("/cupones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.codigo").value("PROMO2026"));

        System.out.println("-> OK: crearCupon_debeRetornarCreated completado con exito.");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /cupones - Debe listar cupones con HTTP 200")
    void listarCupones_debeRetornarOk() throws Exception {
        System.out.println("-> Ejecutando CuponControllerTest: listarCupones_debeRetornarOk");

        when(cuponService.listarCupones()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/cupones")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        System.out.println("-> OK: listarCupones_debeRetornarOk completado con exito.");
    }
}
