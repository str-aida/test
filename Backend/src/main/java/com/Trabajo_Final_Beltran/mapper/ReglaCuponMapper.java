package com.Trabajo_Final_Beltran.mapper;

import com.Trabajo_Final_Beltran.dto.request.UpdateReglaCuponRequest;
import com.Trabajo_Final_Beltran.dto.response.ReglaCuponResponse;
import com.Trabajo_Final_Beltran.entity.ReglaCupon;

public class ReglaCuponMapper {

    private ReglaCuponMapper() {
    }

    public static ReglaCuponResponse toResponse(ReglaCupon regla) {
        if (regla == null) {
            return null;
        }

        return ReglaCuponResponse.builder()
                .id(regla.getId())
                .tipoAsignacion(regla.getTipoAsignacion())
                .activo(regla.getActivo())
                .tipoDescuento(regla.getTipoDescuento())
                .valor(regla.getValor())
                .diasValidez(regla.getDiasValidez())
                .cantidadComprasRequeridas(regla.getCantidadComprasRequeridas())
                .descripcion(regla.getDescripcion())
                .build();
    }

    public static void updateEntity(ReglaCupon regla, UpdateReglaCuponRequest request) {
        regla.setActivo(request.getActivo());
        regla.setTipoDescuento(request.getTipoDescuento());
        regla.setValor(request.getValor());
        regla.setDiasValidez(request.getDiasValidez());
        regla.setCantidadComprasRequeridas(request.getCantidadComprasRequeridas());
        if (request.getDescripcion() != null) {
            regla.setDescripcion(request.getDescripcion());
        }
    }
}
