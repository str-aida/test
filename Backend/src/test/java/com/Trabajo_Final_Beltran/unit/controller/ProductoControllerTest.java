package com.Trabajo_Final_Beltran.unit.controller;

import com.Trabajo_Final_Beltran.controller.ProductoController;
import com.Trabajo_Final_Beltran.dto.response.ProductoResponse;
import com.Trabajo_Final_Beltran.enums.EstadoProducto;
import com.Trabajo_Final_Beltran.security.JwtAuthenticationFilter;
import com.Trabajo_Final_Beltran.security.RateLimitFilter;
import com.Trabajo_Final_Beltran.service.ProductoService;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        value = ProductoController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {JwtAuthenticationFilter.class, RateLimitFilter.class}
        ),
        excludeAutoConfiguration = SecurityAutoConfiguration.class
)
@DisplayName("ProductoController - Unit Tests")
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductoService productoService;

    @MockitoBean
    private CacheManager cacheManager;

    @Test
    @WithMockUser(roles = "CLIENTE")
    @DisplayName("GET /productos - Debe listar productos con HTTP 200")
    void listarProductos_debeRetornarOk() throws Exception {
        System.out.println("-> Ejecutando ProductoControllerTest: listarProductos_debeRetornarOk");

        ProductoResponse prod = ProductoResponse.builder()
                .id(1L)
                .nombre("Hamburguesa Completa")
                .precio(BigDecimal.valueOf(4500))
                .stock(20)
                .estado(EstadoProducto.ACTIVO)
                .build();

        when(productoService.listarProductos(any(), any(), any())).thenReturn(Collections.singletonList(prod));

        mockMvc.perform(get("/productos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].nombre").value("Hamburguesa Completa"));

        System.out.println("-> OK: listarProductos_debeRetornarOk completado con exito.");
    }
}
