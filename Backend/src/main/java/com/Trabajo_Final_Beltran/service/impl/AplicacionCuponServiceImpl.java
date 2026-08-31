package com.Trabajo_Final_Beltran.service.impl;

import com.Trabajo_Final_Beltran.dto.response.ValidacionCuponResponse;
import com.Trabajo_Final_Beltran.entity.Cupon;
import com.Trabajo_Final_Beltran.enums.TipoDescuento;
import com.Trabajo_Final_Beltran.service.AplicacionCuponService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class AplicacionCuponServiceImpl implements AplicacionCuponService {

    @Override
    public ValidacionCuponResponse calcularDescuento(
            Cupon cupon,
            BigDecimal montoOriginal
    ) {
        BigDecimal montoDescuento = calcularMontoDescuento(cupon, montoOriginal);


        if (montoDescuento.compareTo(montoOriginal) > 0) {
            montoDescuento = montoOriginal;
        }

        BigDecimal totalConDescuento = montoOriginal.subtract(montoDescuento);

        return ValidacionCuponResponse.builder()
                .valido(true)
                .mensaje("Cupón aplicado correctamente")
                .codigoCupon(cupon.getCodigo())
                .montoOriginal(montoOriginal)
                .montoDescuento(montoDescuento)
                .totalConDescuento(totalConDescuento)
                .build();
    }

    private BigDecimal calcularMontoDescuento(Cupon cupon, BigDecimal montoOriginal) {

        if (cupon.getTipoDescuento() == TipoDescuento.PORCENTAJE) {

            BigDecimal porcentaje = cupon.getValor()
                    .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);

            return montoOriginal
                    .multiply(porcentaje)
                    .setScale(2, RoundingMode.HALF_UP);
        }

        return cupon.getValor();
    }
}