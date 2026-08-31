package com.Trabajo_Final_Beltran.service;

import com.Trabajo_Final_Beltran.dto.response.ValidacionCuponResponse;
import com.Trabajo_Final_Beltran.entity.Cupon;

import java.math.BigDecimal;

public interface AplicacionCuponService {


    ValidacionCuponResponse calcularDescuento(Cupon cupon, BigDecimal montoOriginal);
}