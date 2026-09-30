package com.Trabajo_Final_Beltran.unit.controller;

import com.Trabajo_Final_Beltran.controller.DescuentoController;
import com.Trabajo_Final_Beltran.dto.request.CreateDescuentoRequest;
import com.Trabajo_Final_Beltran.dto.request.ProductoDescuentoRequest;
import com.Trabajo_Final_Beltran.dto.response.DescuentoResponse;
import com.Trabajo_Final_Beltran.enums.EstadoDescuento;
import com.Trabajo_Final_Beltran.enums.TipoCampanaDescuento;
import com.Trabajo_Final_Beltran.security.JwtAuthenticationFilter;
import com.Trabajo_Final_Beltran.security.RateLimitFilter;
import com.Trabajo_Final_Beltran.service.DescuentoService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        value = DescuentoController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {JwtAuthenticationFilter.class, RateLimitFilter.class}
        ),
        excludeAutoConfiguration = SecurityAutoConfiguration.class
)
@DisplayName("DescuentoController - Unit Tests")
class DescuentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @MockitoBean
    private DescuentoService descuentoService;

    @MockitoBean
    private CacheManager cacheManager;

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /descuentos - Debe crear descuento con HTTP 201")
    void crearDescuento_debeRetornarCreated() throws Exception {
        System.out.println("-> Ejecutando DescuentoControllerTest: crearDescuento_debeRetornarCreated");

        ProductoDescuentoRequest prodReq = ProductoDescuentoRequest.builder()
                .productoId(1L)
                .porcentaje(BigDecimal.valueOf(10.0))
                .build();

        CreateDescuentoRequest request = CreateDescuentoRequest.builder()
                .nombre("Happy Hour")
                .tipo(TipoCampanaDescuento.GENERAL)
                .fechaInicio(LocalDate.now())
                .fechaFin(LocalDate.now().plusDays(10))
                .productos(Collections.singletonList(prodReq))
                .build();

        DescuentoResponse response = DescuentoResponse.builder()
                .id(1L)
                .nombre("Happy Hour")
                .tipo(TipoCampanaDescuento.GENERAL)
                .estado(EstadoDescuento.ACTIVO)
                .build();

        when(descuentoService.crearDescuento(any(CreateDescuentoRequest.class))).thenReturn(response);

        mockMvc.perform(post("/descuentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("Happy Hour"));

        System.out.println("-> OK: crearDescuento_debeRetornarCreated completado con exito.");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /descuentos - Debe listar descuentos con HTTP 200")
    void listarDescuentos_debeRetornarOk() throws Exception {
        System.out.println("-> Ejecutando DescuentoControllerTest: listarDescuentos_debeRetornarOk");

        when(descuentoService.listarDescuentos()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/descuentos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        System.out.println("-> OK: listarDescuentos_debeRetornarOk completado con exito.");
    }
}
