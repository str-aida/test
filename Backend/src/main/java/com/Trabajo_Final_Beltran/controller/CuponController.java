package com.Trabajo_Final_Beltran.controller;

import com.Trabajo_Final_Beltran.dto.request.AsignarCuponRequest;
import com.Trabajo_Final_Beltran.dto.request.CreateCuponRequest;
import com.Trabajo_Final_Beltran.dto.request.UpdateCuponRequest;
import com.Trabajo_Final_Beltran.dto.response.CuponResponse;
import com.Trabajo_Final_Beltran.dto.response.CuponUsuarioResponse;
import com.Trabajo_Final_Beltran.entity.Usuario;
import com.Trabajo_Final_Beltran.security.SecurityUtils;
import com.Trabajo_Final_Beltran.service.AsignacionCuponService;
import com.Trabajo_Final_Beltran.service.CuponService;
import com.Trabajo_Final_Beltran.service.CuponUsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cupones")
@RequiredArgsConstructor
public class CuponController {

    private final CuponService cuponService;
    private final CuponUsuarioService cuponUsuarioService;
    private final AsignacionCuponService asignacionCuponService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CuponResponse> crearCupon(
            @Valid @RequestBody CreateCuponRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(cuponService.crearCupon(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CuponResponse> editarCupon(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCuponRequest request
    ) {
        return ResponseEntity.ok(cuponService.editarCupon(id, request));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CuponResponse>> listarCupones() {
        return ResponseEntity.ok(cuponService.listarCupones());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CuponResponse> obtenerCuponPorId(@PathVariable Long id) {
        return ResponseEntity.ok(cuponService.obtenerCuponPorId(id));
    }

    @PutMapping("/{id}/desactivar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> desactivarCupon(@PathVariable Long id) {
        cuponService.desactivarCupon(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/mis-cupones")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<List<CuponUsuarioResponse>> misCupones() {
        Usuario usuario = SecurityUtils.obtenerUsuarioAutenticado();
        return ResponseEntity.ok(
                cuponUsuarioService.obtenerCuponesActivos(usuario.getId())
        );
    }

    @PostMapping("/asignar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> asignarCupon(
            @Valid @RequestBody AsignarCuponRequest request
    ) {
        asignacionCuponService.asignarCuponManual(request);
        return ResponseEntity.ok().build();
    }
}