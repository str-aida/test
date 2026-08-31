
package com.Trabajo_Final_Beltran.service;

import com.Trabajo_Final_Beltran.dto.request.UpdateUsuarioRequest;
import com.Trabajo_Final_Beltran.dto.response.UsuarioPerfilResponse;
import com.Trabajo_Final_Beltran.enums.Rol;
import java.util.List;

public interface UsuarioService {

    UsuarioPerfilResponse editarUsuario(Long id, UpdateUsuarioRequest request);
    
    List<UsuarioPerfilResponse> listarUsuarios(Rol rol, String texto);
    
    void eliminarUsuario(Long id);
    
    UsuarioPerfilResponse obtenerUsuarioPorId(Long id);
    
    
}