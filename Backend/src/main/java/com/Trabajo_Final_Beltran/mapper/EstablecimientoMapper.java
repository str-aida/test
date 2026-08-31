package com.Trabajo_Final_Beltran.mapper;

import com.Trabajo_Final_Beltran.dto.response.EstablecimientoResponse;
import com.Trabajo_Final_Beltran.entity.Establecimiento;

public class EstablecimientoMapper {

  public static EstablecimientoResponse toResponse(
      Establecimiento establecimiento
  ) {

    return EstablecimientoResponse.builder()
        .id(establecimiento.getId())
        .nombre(establecimiento.getNombre())
        .razonSocial(establecimiento.getRazonSocial())
        .cuit(establecimiento.getCuit())
        .email(establecimiento.getEmail())
        .telefono(establecimiento.getTelefono())
        .direccion(
            DireccionMapper.toResponse(
                establecimiento.getDireccion()
            )
        )
        .horarioApertura(establecimiento.getHorarioApertura())
        .horarioCierre(establecimiento.getHorarioCierre())
        .diasHabiles(establecimiento.getDiasHabiles())
        .descripcion(establecimiento.getDescripcion())
        .tipoServicio(establecimiento.getTipoServicio())
        .estado(establecimiento.getEstado())
        .fechaCreacion(establecimiento.getFechaCreacion())
        .build();
  }
}