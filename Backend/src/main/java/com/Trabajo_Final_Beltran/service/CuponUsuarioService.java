package com.Trabajo_Final_Beltran.service;

import com.Trabajo_Final_Beltran.dto.response.CuponUsuarioResponse;
import com.Trabajo_Final_Beltran.entity.Cupon;
import com.Trabajo_Final_Beltran.entity.Usuario;
import java.util.List;

public interface CuponUsuarioService {
    
    
    void asignarCupon(Usuario usuario, Cupon cupon);
    
    List<CuponUsuarioResponse> obtenerCuponesActivos(Long usuarioId);
}