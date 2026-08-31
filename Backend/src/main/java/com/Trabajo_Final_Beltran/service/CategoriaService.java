package com.Trabajo_Final_Beltran.service;


import com.Trabajo_Final_Beltran.dto.request.CreateCategoriaRequest;
import com.Trabajo_Final_Beltran.dto.request.UpdateCategoriaRequest;
import com.Trabajo_Final_Beltran.dto.response.CategoriaResponse;
import com.Trabajo_Final_Beltran.dto.response.MensajeResponse;
import java.util.List;

public interface CategoriaService {

  MensajeResponse crearCategoria(CreateCategoriaRequest request);

  List<CategoriaResponse> listarCategorias();

  CategoriaResponse editarCategoria(
      Long id,
      UpdateCategoriaRequest request
  );
  void eliminarCategoria(Long id);

}

