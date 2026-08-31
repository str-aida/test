/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.Trabajo_Final_Beltran.controller;

import com.Trabajo_Final_Beltran.dto.response.LogSistemaResponse;
import com.Trabajo_Final_Beltran.dto.response.PageResponse;
import com.Trabajo_Final_Beltran.enums.Rol;
import com.Trabajo_Final_Beltran.service.LogSistemaPdfService;
import com.Trabajo_Final_Beltran.service.LogSistemaService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/logs")
@RequiredArgsConstructor
public class LogSistemaController {

  private final LogSistemaService logSistemaService;

  private final LogSistemaPdfService logSistemaPdfService;

  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<PageResponse<LogSistemaResponse>>
  listarLogs(
      @RequestParam(required = false) String accion,
      @RequestParam(required = false) Rol rol,
      @RequestParam(required = false) String usuario,
      @RequestParam(defaultValue = "0")
      int page,
      @RequestParam(defaultValue = "20")
      int size
  ) {

    return ResponseEntity.ok(
        logSistemaService.listarLogs(
            accion,
            rol,
            usuario,
            page,
            size
        )
    );
  }

  @GetMapping("/exportar-pdf")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<byte[]> exportarPdf(
      @RequestParam(required = false) String accion,
      @RequestParam(required = false) Rol rol,
      @RequestParam(required = false) String usuario
  ) {

    List<LogSistemaResponse> logs =
        logSistemaService.obtenerLogsParaExportacion(
            accion,
            rol,
            usuario
        );

    byte[] pdf =
        logSistemaPdfService.generarPdf(
            logs
        );

    return ResponseEntity.ok()
        .header(
            HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=auditoria.pdf"
        )
        .contentType(
            MediaType.APPLICATION_PDF
        )
        .body(pdf);
  }
}