
package com.Trabajo_Final_Beltran.controller;
import com.Trabajo_Final_Beltran.dto.request.CreateDireccionRequest;
import com.Trabajo_Final_Beltran.dto.request.UpdateDireccionRequest;
import com.Trabajo_Final_Beltran.dto.response.DireccionResponse;
import com.Trabajo_Final_Beltran.service.DireccionService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/direcciones")
@RequiredArgsConstructor
public class DireccionUsuarioController {

  private final DireccionService
      direccionUsuarioService;

  @PostMapping
  @PreAuthorize(
      "hasAnyRole('CLIENTE','ADMIN','EMPLEADO')"
  )
  public ResponseEntity<DireccionResponse>
  crearDireccion(
      @Valid
      @RequestBody
      CreateDireccionRequest request
  ) {

    return ResponseEntity.ok(
        direccionUsuarioService
            .crearDireccion(
                request
            )
    );
  }

  @GetMapping
  @PreAuthorize(
      "hasAnyRole('CLIENTE','ADMIN','EMPLEADO')"
  )
  public ResponseEntity<
      List<DireccionResponse>
      > listarDirecciones() {

    return ResponseEntity.ok(
        direccionUsuarioService
            .listarDirecciones()
    );
  }

  @PutMapping("/{id}")
  @PreAuthorize(
      "hasAnyRole('CLIENTE','ADMIN','EMPLEADO')"
  )
  public ResponseEntity<DireccionResponse>
  editarDireccion(
      @PathVariable Long id,
      @Valid
      @RequestBody
      UpdateDireccionRequest request
  ) {

    return ResponseEntity.ok(
        direccionUsuarioService
            .editarDireccion(
                id,
                request
            )
    );
  }

  @PutMapping("/{id}/principal")
  @PreAuthorize(
      "hasAnyRole('CLIENTE','ADMIN','EMPLEADO')"
  )
  public ResponseEntity<DireccionResponse>
  marcarComoPrincipal(
      @PathVariable Long id
  ) {

    return ResponseEntity.ok(
        direccionUsuarioService
            .marcarComoPrincipal(
                id
            )
    );
  }

  @DeleteMapping("/{id}")
  @PreAuthorize(
      "hasAnyRole('CLIENTE','ADMIN','EMPLEADO')"
  )
  public ResponseEntity<Void>
  eliminarDireccion(
      @PathVariable Long id
  ) {

    direccionUsuarioService
        .eliminarDireccion(
            id
        );

    return ResponseEntity
        .noContent()
        .build();
  }
}