package com.Trabajo_Final_Beltran.service.strategy.cupon;

import com.Trabajo_Final_Beltran.entity.Cupon;
import com.Trabajo_Final_Beltran.entity.Usuario;

import java.util.List;

public interface AsignacionCuponStrategy {

    List<Cupon> obtenerCupones(Usuario usuario);

}