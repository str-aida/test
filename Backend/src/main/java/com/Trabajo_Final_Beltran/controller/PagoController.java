package com.Trabajo_Final_Beltran.controller;

import com.Trabajo_Final_Beltran.dto.response.PagoResponse;
import com.Trabajo_Final_Beltran.service.PagoService;
import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pagos")
@RequiredArgsConstructor
public class PagoController {

    private final PagoService pagoService;

    @PreAuthorize("hasAnyRole('CLIENTE','ADMIN','EMPLEADO')")
    @PostMapping("/{pedidoId}")
    public ResponseEntity<PagoResponse> crearPago(
            @PathVariable Long pedidoId
    ) {
        return ResponseEntity.ok(
                pagoService.crearPago(pedidoId)
        );
    }

    @PreAuthorize(
            "hasAnyRole('ADMIN','EMPLEADO')"
    )
    @PutMapping("/{id}/aprobar")
    public ResponseEntity<PagoResponse> aprobarPago(
            @PathVariable Long id,
            @RequestParam String referencia
    ) {

        return ResponseEntity.ok(
                pagoService.aprobarPago(
                        id,
                        referencia
                )
        );
    }

    @PreAuthorize(
            "hasAnyRole('ADMIN','EMPLEADO')"
    )
    @PutMapping("/{id}/reembolsar")
    public ResponseEntity<PagoResponse> reembolsarPago(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                pagoService.reembolsarPago(
                        id
                )
        );
    }
}