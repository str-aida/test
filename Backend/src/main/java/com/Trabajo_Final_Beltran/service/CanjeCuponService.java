package com.Trabajo_Final_Beltran.service;

import com.Trabajo_Final_Beltran.entity.Cupon;
import com.Trabajo_Final_Beltran.entity.Usuario;

public interface CanjeCuponService {

    void canjear(Cupon cupon, Usuario usuario);
    void deshacerCanje(Cupon cupon, Usuario usuario);
}