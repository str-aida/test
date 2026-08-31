package com.Trabajo_Final_Beltran.mapper;

import com.Trabajo_Final_Beltran.dto.response.CuponUsuarioResponse;
import com.Trabajo_Final_Beltran.entity.CuponUsuario;

public class CuponUsuarioMapper {

    private CuponUsuarioMapper() {
    }

    public static CuponUsuarioResponse toResponse(CuponUsuario cuponUsuario) {
        return CuponUsuarioResponse.builder()
                .id(cuponUsuario.getId())
                .cupon(CuponMapper.toResponse(cuponUsuario.getCupon()))
                .usado(cuponUsuario.getUsado())
                .fechaAsignacion(cuponUsuario.getFechaAsignacion())
                .fechaUso(cuponUsuario.getFechaUso())
                .build();
    }
}