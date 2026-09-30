package com.Trabajo_Final_Beltran.unit.controller;

import com.Trabajo_Final_Beltran.controller.CategoriaController;
import com.Trabajo_Final_Beltran.dto.request.CreateCategoriaRequest;
import com.Trabajo_Final_Beltran.dto.response.CategoriaResponse;
import com.Trabajo_Final_Beltran.dto.response.MensajeResponse;
import com.Trabajo_Final_Beltran.enums.EstadoCategoria;
import com.Trabajo_Final_Beltran.security.JwtAuthenticationFilter;
import com.Trabajo_Final_Beltran.security.RateLimitFilter;
import com.Trabajo_Final_Beltran.service.CategoriaService;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        value = CategoriaController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {JwtAuthenticationFilter.class, RateLimitFilter.class}
        ),
        excludeAutoConfiguration = SecurityAutoConfiguration.class
)
@DisplayName("CategoriaController - Unit Tests")
class CategoriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @MockitoBean
    private CategoriaService categoriaService;

    @MockitoBean
    private CacheManager cacheManager;

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /categorias - Debe crear categoría con HTTP 201")
    void crearCategoria_debeRetornarCreated() throws Exception {
        System.out.println("-> Ejecutando CategoriaControllerTest: crearCategoria_debeRetornarCreated");

        CreateCategoriaRequest request = CreateCategoriaRequest.builder()
                .nombre("Pizzas")
                .descripcion("Pizzas artesanales")
                .build();

        MensajeResponse response = new MensajeResponse("Categoría creada exitosamente");

        when(categoriaService.crearCategoria(any(CreateCategoriaRequest.class))).thenReturn(response);

        mockMvc.perform(post("/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.mensaje").value("Categoría creada exitosamente"));

        System.out.println("-> OK: crearCategoria_debeRetornarCreated completado con exito.");
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    @DisplayName("GET /categorias - Debe listar categorías con HTTP 200")
    void listarCategorias_debeRetornarOk() throws Exception {
        System.out.println("-> Ejecutando CategoriaControllerTest: listarCategorias_debeRetornarOk");

        CategoriaResponse cat = CategoriaResponse.builder()
                .id(1L)
                .nombre("Bebidas")
                .estado(EstadoCategoria.ACTIVO)
                .build();

        when(categoriaService.listarCategorias()).thenReturn(Collections.singletonList(cat));

        mockMvc.perform(get("/categorias")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Bebidas"));

        System.out.println("-> OK: listarCategorias_debeRetornarOk completado con exito.");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("DELETE /categorias/{id} - Debe eliminar categoría con HTTP 204")
    void eliminarCategoria_debeRetornarNoContent() throws Exception {
        System.out.println("-> Ejecutando CategoriaControllerTest: eliminarCategoria_debeRetornarNoContent");

        mockMvc.perform(delete("/categorias/1"))
                .andExpect(status().isNoContent());

        System.out.println("-> OK: eliminarCategoria_debeRetornarNoContent completado con exito.");
    }
}
