package com.Trabajo_Final_Beltran.mapper;

import com.Trabajo_Final_Beltran.dto.response.LogSistemaResponse;
import com.Trabajo_Final_Beltran.entity.LogSistema;

public class LogSistemaMapper {

  public static LogSistemaResponse toResponse(
      LogSistema log
  ) {

    return LogSistemaResponse.builder()
        .id(log.getId())
        .tablaAfectada(
            log.getTablaAfectada()
        )
        .idRegistro(
            log.getIdRegistro()
        )
        .referencia(
            log.getReferencia()
        )
        .accion(
            log.getAccion()
        )
        .campoModificado(
            log.getCampoModificado()
        )
        .valorAnterior(
            log.getValorAnterior()
        )
        .valorNuevo(
            log.getValorNuevo()
        )
        .usuario(
            log.getUsuario().getNombre()
                + " "
                + log.getUsuario().getApellido()
        )
        .rol(
            log.getUsuario().getRol()
        )
        .fecha(
            log.getFecha()
        )
        .descripcion(
            log.getDescripcion()
        )
        .tipoOperacion(
            log.getTipoOperacion()
        )
        .build();
  }

}
