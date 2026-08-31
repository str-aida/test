package com.Trabajo_Final_Beltran.mapper;

import com.Trabajo_Final_Beltran.dto.response.CategoriaResponse;
import com.Trabajo_Final_Beltran.entity.Categoria;
import com.Trabajo_Final_Beltran.dto.request.CreateCategoriaRequest;


public class CategoriaMapper {
  public static Categoria toEntity
      (CreateCategoriaRequest request
      ){

    return  Categoria.builder()
            .nombre(request.getNombre())
            .descripcion(request.getDescripcion())
            .build();



  }

  public static CategoriaResponse toResponse
      (Categoria categoria
      ){
    return   CategoriaResponse.builder()
            .id(categoria.getId())
            .nombre(categoria.getNombre())
            .descripcion(categoria.getDescripcion())
            .estado(categoria.getEstado())
            .build();

  }
}
