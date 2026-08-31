/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.Trabajo_Final_Beltran.service;

import com.Trabajo_Final_Beltran.dto.request.CreateDireccionRequest;
import com.Trabajo_Final_Beltran.dto.request.UpdateDireccionRequest;
import com.Trabajo_Final_Beltran.dto.response.DireccionResponse;

import java.util.List;

public interface DireccionService {

  DireccionResponse crearDireccion(
      CreateDireccionRequest request
  );

  List<DireccionResponse> listarDirecciones();

  DireccionResponse editarDireccion(
      Long id,
      UpdateDireccionRequest request
  );

  void eliminarDireccion(
      Long id
  );

  DireccionResponse marcarComoPrincipal(
      Long id
  );
}
