package com.Trabajo_Final_Beltran.unit.controller;

import com.Trabajo_Final_Beltran.controller.PagoController;
import com.Trabajo_Final_Beltran.dto.response.PagoResponse;
import com.Trabajo_Final_Beltran.enums.EstadoPago;
import com.Trabajo_Final_Beltran.enums.MetodoPago;
import com.Trabajo_Final_Beltran.security.JwtAuthenticationFilter;
import com.Trabajo_Final_Beltran.security.RateLimitFilter;
import com.Trabajo_Final_Beltran.service.PagoService;
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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        value = PagoController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {JwtAuthenticationFilter.class, RateLimitFilter.class}
        ),
        excludeAutoConfiguration = SecurityAutoConfiguration.class
)
@DisplayName("PagoController - Unit Tests")
class PagoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PagoService pagoService;

    @MockitoBean
    private CacheManager cacheManager;

    @Test
    @WithMockUser(roles = "CLIENTE")
    @DisplayName("POST /pagos/{pedidoId} - Debe crear pago con HTTP 200")
    void crearPago_debeRetornarOk() throws Exception {
        System.out.println("-> Ejecutando PagoControllerTest: crearPago_debeRetornarOk");

        PagoResponse response = PagoResponse.builder()
                .id(1L)
                .pedidoId(5L)
                .monto(BigDecimal.valueOf(2500))
                .metodoPago(MetodoPago.TARJETA)
                .estado(EstadoPago.PENDIENTE)
                .urlPago("https://mpago.la/mock")
                .build();

        when(pagoService.crearPago(5L)).thenReturn(response);

        mockMvc.perform(post("/pagos/5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.urlPago").value("https://mpago.la/mock"));

        System.out.println("-> OK: crearPago_debeRetornarOk completado con exito.");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("PUT /pagos/{id}/aprobar - Debe aprobar pago con HTTP 200")
    void aprobarPago_debeRetornarOk() throws Exception {
        System.out.println("-> Ejecutando PagoControllerTest: aprobarPago_debeRetornarOk");

        PagoResponse response = PagoResponse.builder()
                .id(1L)
                .pedidoId(5L)
                .monto(BigDecimal.valueOf(2500))
                .metodoPago(MetodoPago.TARJETA)
                .estado(EstadoPago.APROBADO)
                .build();

        when(pagoService.aprobarPago(1L, "REF-12345")).thenReturn(response);

        mockMvc.perform(put("/pagos/1/aprobar?referencia=REF-12345")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.estado").value("APROBADO"));

        System.out.println("-> OK: aprobarPago_debeRetornarOk completado con exito.");
    }
}
