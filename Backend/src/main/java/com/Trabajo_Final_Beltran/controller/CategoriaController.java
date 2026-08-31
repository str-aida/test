
package com.Trabajo_Final_Beltran.controller;


import com.Trabajo_Final_Beltran.dto.request.CreateCategoriaRequest;
import com.Trabajo_Final_Beltran.dto.request.UpdateCategoriaRequest;
import com.Trabajo_Final_Beltran.dto.response.CategoriaResponse;
import com.Trabajo_Final_Beltran.dto.response.MensajeResponse;
import com.Trabajo_Final_Beltran.service.CategoriaService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/categorias")
@RequiredArgsConstructor
public class CategoriaController {

  private final CategoriaService categoriaService;

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping
  public ResponseEntity<MensajeResponse> crearCategoria(
      @Valid @RequestBody CreateCategoriaRequest request) {

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(
            categoriaService.crearCategoria(request)
        );
  }

  @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO','CLIENTE')")
  @GetMapping
  public ResponseEntity<List<CategoriaResponse>> listarCategorias() {

    return ResponseEntity.ok(
        categoriaService.listarCategorias()
    );
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping("/{id}")
  public ResponseEntity<CategoriaResponse> editarCategoria(
      @PathVariable Long id,
      @Valid @RequestBody UpdateCategoriaRequest request) {

    return ResponseEntity.ok(
        categoriaService.editarCategoria(
            id,
            request
        )
    );
  }

  @PreAuthorize("hasRole('ADMIN')")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> eliminarCategoria(@PathVariable Long id) {

    categoriaService.eliminarCategoria(id);

    return ResponseEntity
        .noContent()
        .build();
  }
}
