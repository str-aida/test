package com.Trabajo_Final_Beltran.mapper;

import com.Trabajo_Final_Beltran.dto.request.CreateDireccionEstablecimientoRequest;
import com.Trabajo_Final_Beltran.dto.request.CreateDireccionRequest;
import com.Trabajo_Final_Beltran.dto.response.DireccionResponse;
import com.Trabajo_Final_Beltran.entity.Direccion;

public class DireccionMapper {

  public static Direccion toEntity(
      CreateDireccionRequest request
  ) {

    return Direccion.builder()
        .nombre(request.getNombre())
        .calle(request.getCalle())
        .numero(request.getNumero())
        .localidad(request.getLocalidad())
        .piso(request.getPiso())
        .departamento(request.getDepartamento())
        .codigoPostal(request.getCodigoPostal())
        .referencia(request.getReferencia())
        .build();
  }

  public static Direccion toEntity(
      CreateDireccionEstablecimientoRequest request
  ) {

    return Direccion.builder()
        .nombre(request.getNombre())
        .calle(request.getCalle())
        .numero(request.getNumero())
        .localidad(request.getLocalidad())
        .piso(request.getPiso())
        .departamento(request.getDepartamento())
        .codigoPostal(request.getCodigoPostal())
        .referencia(request.getReferencia())
        .build();
  }

  public static DireccionResponse toResponse(
      Direccion direccion
  ) {

    return DireccionResponse.builder()
        .id(direccion.getId())
        .nombre(direccion.getNombre())
        .calle(direccion.getCalle())
        .numero(direccion.getNumero())
        .localidad(direccion.getLocalidad())
        .piso(direccion.getPiso())
        .departamento(direccion.getDepartamento())
        .codigoPostal(direccion.getCodigoPostal())
        .referencia(direccion.getReferencia())
        .esPrincipal(direccion.getEsPrincipal())
        .build();
  }
}