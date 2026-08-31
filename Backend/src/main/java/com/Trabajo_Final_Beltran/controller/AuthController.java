package com.Trabajo_Final_Beltran.controller;

import com.Trabajo_Final_Beltran.dto.request.LoginRequest;
import com.Trabajo_Final_Beltran.dto.request.RegisterRequest;
import com.Trabajo_Final_Beltran.dto.request.RestablecerPasswordRequest;
import com.Trabajo_Final_Beltran.dto.request.SolicitarRecuperacionRequest;
import com.Trabajo_Final_Beltran.dto.response.AuthResponse;
import com.Trabajo_Final_Beltran.service.AuthService;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final com.Trabajo_Final_Beltran.security.JwtService jwtService;
    private final com.Trabajo_Final_Beltran.security.TokenBlacklistService tokenBlacklistService;
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody LoginRequest request
    ) {

        return ResponseEntity.ok(
                authService.login(request)
        );
    }

    @PostMapping("/registro-admin")
    public ResponseEntity<AuthResponse> registerAdmin(
           @Valid @RequestBody RegisterRequest request
    ) {

        return ResponseEntity.ok(
                authService.registerAdmin(request)
        );
    }

    @PostMapping("/registro-cliente")
    public ResponseEntity<AuthResponse> registerCliente(
          @Valid  @RequestBody RegisterRequest request
    ) {

        return ResponseEntity.ok(
                authService.registerCliente(request)
        );
    }
    @PostMapping("/solicitar-recuperacion")
    public ResponseEntity<String>
    solicitarRecuperacionPassword(

        @RequestBody
        SolicitarRecuperacionRequest request
        ) {

        return ResponseEntity.ok(

            authService
                    .solicitarRecuperacionPassword(
                            request
                    )
    );
}
    @PostMapping("/restablecer-password")
    public ResponseEntity<String>
    restablecerPassword(

        @RequestBody
        RestablecerPasswordRequest request
        ) {

        return ResponseEntity.ok(

            authService
                    .restablecerPassword(
                            request
                    )
    );
}
    @PostMapping("/crear-personal")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> crearPersonal(
            @Valid @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(
                authService.createPersonal(request)
        );
    }
    
    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> logout(
            HttpServletRequest request
    ) {
        String authHeader = request.getHeader("Authorization");
        String token = authHeader.substring(7);

        long segundosRestantes = jwtService.extractSegundosRestantes(token);
        tokenBlacklistService.invalidar(token, segundosRestantes);

        return ResponseEntity.ok("Sesión cerrada correctamente");
    }

    
}