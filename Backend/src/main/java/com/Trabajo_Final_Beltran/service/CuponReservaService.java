package com.Trabajo_Final_Beltran.service;

import com.Trabajo_Final_Beltran.entity.Cupon;
import com.Trabajo_Final_Beltran.entity.Usuario;

public interface CuponReservaService {
    void reservar(Cupon cupon, Usuario usuario, Long pedidoId);
    void liberarReserva(Cupon cupon, Usuario usuario);
}