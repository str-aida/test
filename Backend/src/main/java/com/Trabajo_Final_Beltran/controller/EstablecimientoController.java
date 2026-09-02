package com.Trabajo_Final_Beltran.controller;

import com.Trabajo_Final_Beltran.dto.request.UpdateEstablecimientoRequest;
import com.Trabajo_Final_Beltran.dto.response.EstablecimientoClienteResponse;
import com.Trabajo_Final_Beltran.dto.response.EstablecimientoResponse;
import com.Trabajo_Final_Beltran.service.EstablecimientoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/establecimiento")
@RequiredArgsConstructor
public class EstablecimientoController {

  private final EstablecimientoService establecimientoService;

  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<EstablecimientoResponse> obtenerEstablecimiento() {

    return ResponseEntity.ok(
        establecimientoService.obtenerEstablecimiento()
    );
  }

  @GetMapping("/info")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<EstablecimientoClienteResponse> obtenerInfoClienteActual() {
    return ResponseEntity.ok(
        establecimientoService.obtenerInfoClienteActual()
    );
  }

  @GetMapping("/{id}/info")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<EstablecimientoClienteResponse> obtenerInfoCliente(
      @PathVariable Long id
  ) {
    return ResponseEntity.ok(
        establecimientoService.obtenerInfoCliente(id)
    );
  }


  @PutMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<EstablecimientoResponse> actualizarEstablecimiento(
      @Valid @RequestBody UpdateEstablecimientoRequest request) {

    EstablecimientoResponse response =
        establecimientoService.actualizarEstablecimiento(request);

    return ResponseEntity.ok(response);
  }
}