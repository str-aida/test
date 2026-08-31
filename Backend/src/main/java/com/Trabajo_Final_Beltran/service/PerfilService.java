
package com.Trabajo_Final_Beltran.service;

import com.Trabajo_Final_Beltran.dto.request.UpdatePerfilRequest;
import com.Trabajo_Final_Beltran.dto.response.UsuarioPerfilResponse;
import com.Trabajo_Final_Beltran.dto.request.UpdatePasswordRequest;


public interface PerfilService {
    void solicitarCambioPassword();
    
    UsuarioPerfilResponse obtenerPerfil();
    
    UsuarioPerfilResponse actualizarPerfil(UpdatePerfilRequest request);

    void cambiarPassword(UpdatePasswordRequest request);
}