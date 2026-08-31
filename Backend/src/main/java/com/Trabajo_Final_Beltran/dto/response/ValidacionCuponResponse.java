package com.Trabajo_Final_Beltran.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValidacionCuponResponse {

    private boolean valido;
    private String mensaje;
    private String codigoCupon;         
    private BigDecimal montoOriginal;
    private BigDecimal montoDescuento;
    private BigDecimal totalConDescuento; 
}