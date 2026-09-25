package com.Trabajo_Final_Beltran.unit.controller;

import com.Trabajo_Final_Beltran.controller.PedidoController;
import com.Trabajo_Final_Beltran.dto.request.CreateDetallePedidoRequest;
import com.Trabajo_Final_Beltran.dto.request.CreatePedidoRequest;
import com.Trabajo_Final_Beltran.dto.response.PedidoDetalleResponse;
import com.Trabajo_Final_Beltran.enums.EstadoPedido;
import com.Trabajo_Final_Beltran.enums.MetodoPago;
import com.Trabajo_Final_Beltran.enums.TipoEntrega;
import com.Trabajo_Final_Beltran.security.JwtAuthenticationFilter;
import com.Trabajo_Final_Beltran.security.RateLimitFilter;
import com.Trabajo_Final_Beltran.service.PedidoService;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        value = PedidoController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {JwtAuthenticationFilter.class, RateLimitFilter.class}
        ),
        excludeAutoConfiguration = SecurityAutoConfiguration.class
)
@DisplayName("PedidoController - Unit Tests")
class PedidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @MockitoBean
    private PedidoService pedidoService;

    @MockitoBean
    private CacheManager cacheManager;

    @Test
    @WithMockUser(roles = "CLIENTE")
    @DisplayName("POST /pedidos - Debe crear pedido con HTTP 201")
    void crearPedido_debeRetornarCreated() throws Exception {
        System.out.println("-> Ejecutando PedidoControllerTest: crearPedido_debeRetornarCreated");

        CreateDetallePedidoRequest detalle = CreateDetallePedidoRequest.builder()
                .productoId(1L)
                .cantidad(2)
                .build();

        CreatePedidoRequest request = CreatePedidoRequest.builder()
                .tipoEntrega(TipoEntrega.RETIRO)
                .metodoPago(MetodoPago.EFECTIVO)
                .detalles(Collections.singletonList(detalle))
                .build();

        PedidoDetalleResponse response = PedidoDetalleResponse.builder()
                .id(1L)
                .numeroPedido("PED-0001")
                .estado(EstadoPedido.PENDIENTE)
                .total(BigDecimal.valueOf(3500))
                .build();

        when(pedidoService.crearPedido(any(CreatePedidoRequest.class))).thenReturn(response);

        mockMvc.perform(post("/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.numeroPedido").value("PED-0001"));

        System.out.println("-> OK: crearPedido_debeRetornarCreated completado con exito.");
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    @DisplayName("GET /pedidos/{id} - Debe obtener pedido por id con HTTP 200")
    void obtenerPedidoPorId_debeRetornarOk() throws Exception {
        System.out.println("-> Ejecutando PedidoControllerTest: obtenerPedidoPorId_debeRetornarOk");

        PedidoDetalleResponse response = PedidoDetalleResponse.builder()
                .id(1L)
                .numeroPedido("PED-0001")
                .estado(EstadoPedido.PENDIENTE)
                .total(BigDecimal.valueOf(3500))
                .build();

        when(pedidoService.obtenerPedidoPorId(1L)).thenReturn(response);

        mockMvc.perform(get("/pedidos/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        System.out.println("-> OK: obtenerPedidoPorId_debeRetornarOk completado con exito.");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("PUT /pedidos/{id}/aceptar - Debe aceptar pedido con HTTP 200")
    void aceptarPedido_debeRetornarOk() throws Exception {
        System.out.println("-> Ejecutando PedidoControllerTest: aceptarPedido_debeRetornarOk");

        PedidoDetalleResponse response = PedidoDetalleResponse.builder()
                .id(1L)
                .estado(EstadoPedido.ACEPTADO)
                .build();

        when(pedidoService.aceptarPedido(1L)).thenReturn(response);

        mockMvc.perform(put("/pedidos/1/aceptar")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ACEPTADO"));

        System.out.println("-> OK: aceptarPedido_debeRetornarOk completado con exito.");
    }
}
