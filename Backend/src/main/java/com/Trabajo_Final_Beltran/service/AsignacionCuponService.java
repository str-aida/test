package com.Trabajo_Final_Beltran.service;

import com.Trabajo_Final_Beltran.dto.request.AsignarCuponRequest;
import com.Trabajo_Final_Beltran.entity.Usuario;

public interface AsignacionCuponService {
    
    void asignarCupones(Usuario usuario);
    
    void asignarCuponManual(AsignarCuponRequest request);

    
}