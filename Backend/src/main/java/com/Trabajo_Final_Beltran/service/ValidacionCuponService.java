package com.Trabajo_Final_Beltran.service;

import com.Trabajo_Final_Beltran.entity.Cupon;
import com.Trabajo_Final_Beltran.entity.Usuario;

public interface ValidacionCuponService {

    Cupon validarCupon(String codigo, Usuario usuario);
}