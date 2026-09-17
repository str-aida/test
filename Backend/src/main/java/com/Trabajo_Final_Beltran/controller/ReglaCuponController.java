package com.Trabajo_Final_Beltran.controller;

import com.Trabajo_Final_Beltran.dto.request.UpdateReglaCuponRequest;
import com.Trabajo_Final_Beltran.dto.response.ReglaCuponResponse;
import com.Trabajo_Final_Beltran.enums.TipoAsignacionCupon;
import com.Trabajo_Final_Beltran.service.ReglaCuponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cupones/reglas")
@RequiredArgsConstructor
public class ReglaCuponController {

    private final ReglaCuponService reglaCuponService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ReglaCuponResponse>> listarReglas() {
        return ResponseEntity.ok(reglaCuponService.listarReglas());
    }

    @GetMapping("/{tipoAsignacion}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReglaCuponResponse> obtenerRegla(@PathVariable TipoAsignacionCupon tipoAsignacion) {
        return ResponseEntity.ok(reglaCuponService.obtenerReglaPorTipo(tipoAsignacion));
    }

    @PutMapping("/{tipoAsignacion}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReglaCuponResponse> actualizarRegla(
            @PathVariable TipoAsignacionCupon tipoAsignacion,
            @Valid @RequestBody UpdateReglaCuponRequest request
    ) {
        return ResponseEntity.ok(reglaCuponService.actualizarRegla(tipoAsignacion, request));
    }
}
