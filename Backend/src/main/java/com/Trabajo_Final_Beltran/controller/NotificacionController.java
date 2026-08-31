package com.Trabajo_Final_Beltran.controller;

import com.Trabajo_Final_Beltran.dto.response.NotificacionResponse;
import com.Trabajo_Final_Beltran.service.NotificacionService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notificaciones")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','EMPLEADO','CLIENTE')")
public class NotificacionController {

  private final NotificacionService notificacionService;

  @GetMapping
  public ResponseEntity<List<NotificacionResponse>>
  listarMisNotificaciones() {

    return ResponseEntity.ok(
        notificacionService.listarMisNotificaciones()
    );
  }


  @GetMapping("/no-leidas")
  public ResponseEntity<Long>
  contarNoLeidas() {

    return ResponseEntity.ok(
        notificacionService.contarNoLeidas()
    );
  }


  @PatchMapping("/{id}/leida")
  public ResponseEntity<Void>
  marcarComoLeida(
      @PathVariable Long id
  ) {

    notificacionService.marcarComoLeida(id);

    return ResponseEntity.noContent().build();
  }
}