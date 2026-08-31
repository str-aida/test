package com.Trabajo_Final_Beltran.controller;

import com.Trabajo_Final_Beltran.dto.request.UpdatePasswordRequest;
import com.Trabajo_Final_Beltran.dto.request.UpdatePerfilRequest;
import com.Trabajo_Final_Beltran.dto.request.UpdateUsuarioRequest;
import com.Trabajo_Final_Beltran.dto.response.UsuarioPerfilResponse;
import com.Trabajo_Final_Beltran.enums.Rol;
import com.Trabajo_Final_Beltran.service.PerfilService;
import com.Trabajo_Final_Beltran.service.UsuarioService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/perfil")
@RequiredArgsConstructor
public class PerfilController {

    private final UsuarioService usuarioService;
    private final PerfilService perfilService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<UsuarioPerfilResponse> obtenerMiPerfil() {
        return ResponseEntity.ok(
                perfilService.obtenerPerfil()
        );
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping
    public ResponseEntity<UsuarioPerfilResponse> actualizarMiPerfil(
            @Valid @RequestBody UpdatePerfilRequest request
    ) {
        return ResponseEntity.ok(
                perfilService.actualizarPerfil(request)
        );
    }
    
   @PreAuthorize("isAuthenticated()")
   @PutMapping("/password")
   public ResponseEntity<String> cambiarPassword(
            @Valid @RequestBody UpdatePasswordRequest request
   ) {
        perfilService.cambiarPassword(request);
        return ResponseEntity.ok("Contraseña actualizada correctamente");
    } 

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/password")
    public ResponseEntity<String> solicitarCambioPassword() {
        perfilService.solicitarCambioPassword();
        return ResponseEntity.ok(
            "Se envió el correo para cambiar la contraseña"
        );
    }

    //  Gestión de otros usuarios (solo ADMIN) ===

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/usuarios")
    public ResponseEntity<List<UsuarioPerfilResponse>> listarUsuarios(
            @RequestParam(required = false) Rol rol,
            @RequestParam(required = false) String texto
    ) {
        return ResponseEntity.ok(
                usuarioService.listarUsuarios(rol, texto)
        );
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/usuarios/{id}")
    public ResponseEntity<UsuarioPerfilResponse> obtenerUsuarioPorId(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                usuarioService.obtenerUsuarioPorId(id)
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/usuarios/{id}")
    public ResponseEntity<UsuarioPerfilResponse> editarUsuario(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUsuarioRequest request
    ) {
        return ResponseEntity.ok(
                usuarioService.editarUsuario(id, request)
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<String> eliminarUsuario(
            @PathVariable Long id
    ) {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.ok(
                "Usuario eliminado correctamente"
        );
    }
}