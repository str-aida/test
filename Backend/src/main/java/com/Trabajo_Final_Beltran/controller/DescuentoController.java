package com.Trabajo_Final_Beltran.controller;

import com.Trabajo_Final_Beltran.dto.request.CreateDescuentoRequest;
import com.Trabajo_Final_Beltran.dto.request.UpdateDescuentoRequest;
import com.Trabajo_Final_Beltran.dto.response.DescuentoResponse;
import com.Trabajo_Final_Beltran.dto.response.MensajeResponse;
import com.Trabajo_Final_Beltran.service.DescuentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/descuentos")
@RequiredArgsConstructor
public class DescuentoController {

    private final DescuentoService descuentoService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<DescuentoResponse> crearDescuento(
            @Valid @RequestBody CreateDescuentoRequest request
    ) {
        DescuentoResponse response = descuentoService.crearDescuento(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<DescuentoResponse> editarDescuento(
            @PathVariable Long id,
            @Valid @RequestBody UpdateDescuentoRequest request
    ) {
        DescuentoResponse response = descuentoService.editarDescuento(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<List<DescuentoResponse>> listarDescuentos() {
        return ResponseEntity.ok(descuentoService.listarDescuentos());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<DescuentoResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(descuentoService.obtenerPorId(id));
    }

    @PatchMapping("/{id}/activar")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<MensajeResponse> activar(@PathVariable Long id) {
        return ResponseEntity.ok(descuentoService.activar(id));
    }

    @PatchMapping("/{id}/desactivar")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<MensajeResponse> desactivar(@PathVariable Long id) {
        return ResponseEntity.ok(descuentoService.desactivar(id));
    }
}