package com.Trabajo_Final_Beltran.unit.controller;

import com.Trabajo_Final_Beltran.controller.LogSistemaController;
import com.Trabajo_Final_Beltran.dto.response.PageResponse;
import com.Trabajo_Final_Beltran.security.JwtAuthenticationFilter;
import com.Trabajo_Final_Beltran.security.RateLimitFilter;
import com.Trabajo_Final_Beltran.service.LogSistemaPdfService;
import com.Trabajo_Final_Beltran.service.LogSistemaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        value = LogSistemaController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {JwtAuthenticationFilter.class, RateLimitFilter.class}
        ),
        excludeAutoConfiguration = SecurityAutoConfiguration.class
)
@DisplayName("LogSistemaController - Unit Tests")
class LogSistemaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LogSistemaService logSistemaService;

    @MockitoBean
    private LogSistemaPdfService logSistemaPdfService;

    @MockitoBean
    private CacheManager cacheManager;

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /logs - Debe listar logs con HTTP 200")
    void listarLogs_debeRetornarOk() throws Exception {
        System.out.println("-> Ejecutando LogSistemaControllerTest: listarLogs_debeRetornarOk");

        PageResponse response = PageResponse.builder()
                .content(Collections.emptyList())
                .totalElementos(0L)
                .totalPaginas(0)
                .pagina(0)
                .size(20)
                .build();

        when(logSistemaService.listarLogs(any(), any(), any(), anyInt(), anyInt())).thenReturn(response);

        mockMvc.perform(get("/logs")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());

        System.out.println("-> OK: listarLogs_debeRetornarOk completado con exito.");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /logs/exportar-pdf - Debe exportar archivo PDF con HTTP 200")
    void exportarPdf_debeRetornarPdf() throws Exception {
        System.out.println("-> Ejecutando LogSistemaControllerTest: exportarPdf_debeRetornarPdf");

        byte[] fakePdf = "%PDF-1.4 mock".getBytes();
        when(logSistemaService.obtenerLogsParaExportacion(any(), any(), any())).thenReturn(Collections.emptyList());
        when(logSistemaPdfService.generarPdf(any())).thenReturn(fakePdf);

        mockMvc.perform(get("/logs/exportar-pdf"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=auditoria.pdf"))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));

        System.out.println("-> OK: exportarPdf_debeRetornarPdf completado con exito.");
    }
}
