package com.Trabajo_Final_Beltran.unit.controller;

import com.Trabajo_Final_Beltran.controller.AnaliticaController;
import com.Trabajo_Final_Beltran.dto.response.ResumenEjecutivoResponse;
import com.Trabajo_Final_Beltran.security.JwtAuthenticationFilter;
import com.Trabajo_Final_Beltran.security.RateLimitFilter;
import com.Trabajo_Final_Beltran.service.AnaliticaService;
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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        value = AnaliticaController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {JwtAuthenticationFilter.class, RateLimitFilter.class}
        ),
        excludeAutoConfiguration = SecurityAutoConfiguration.class
)
@DisplayName("AnaliticaController - Unit Tests")
class AnaliticaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AnaliticaService analiticaService;

    @MockitoBean
    private CacheManager cacheManager;

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /analitica/resumen - Debe retornar resumen ejecutivo con HTTP 200")
    void obtenerResumenEjecutivo_debeRetornarOk() throws Exception {
        System.out.println("-> Ejecutando AnaliticaControllerTest: obtenerResumenEjecutivo_debeRetornarOk");

        ResumenEjecutivoResponse response = ResumenEjecutivoResponse.builder()
                .ventasTotales(BigDecimal.valueOf(150000))
                .totalPedidos(120L)
                .ticketPromedio(BigDecimal.valueOf(1250))
                .pedidosPendientes(10L)
                .pedidosEntregados(110L)
                .clientesRegistrados(50L)
                .build();

        when(analiticaService.obtenerResumenEjecutivo()).thenReturn(response);

        mockMvc.perform(get("/analitica/resumen")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPedidos").value(120L));

        System.out.println("-> OK: obtenerResumenEjecutivo_debeRetornarOk completado con exito.");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /analitica/clientes/mejores - Debe retornar mejores clientes con HTTP 200")
    void obtenerMejoresClientes_debeRetornarOk() throws Exception {
        System.out.println("-> Ejecutando AnaliticaControllerTest: obtenerMejoresClientes_debeRetornarOk");

        when(analiticaService.obtenerMejoresClientes(5)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/analitica/clientes/mejores?limite=5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        System.out.println("-> OK: obtenerMejoresClientes_debeRetornarOk completado con exito.");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /analitica/pedidos/estados - Debe retornar pedidos por estado con HTTP 200")
    void obtenerPedidosPorEstado_debeRetornarOk() throws Exception {
        System.out.println("-> Ejecutando AnaliticaControllerTest: obtenerPedidosPorEstado_debeRetornarOk");

        when(analiticaService.obtenerPedidosPorEstado()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/analitica/pedidos/estados")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        System.out.println("-> OK: obtenerPedidosPorEstado_debeRetornarOk completado con exito.");
    }
}
