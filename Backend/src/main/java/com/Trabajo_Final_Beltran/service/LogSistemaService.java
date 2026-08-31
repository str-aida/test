/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.Trabajo_Final_Beltran.service;


import com.Trabajo_Final_Beltran.dto.response.LogSistemaResponse;
import com.Trabajo_Final_Beltran.enums.Rol;
import com.Trabajo_Final_Beltran.enums.TipoOperacion;
import com.Trabajo_Final_Beltran.dto.response.PageResponse;
import java.util.List;

public interface LogSistemaService {

  void registrarLog(
      String tablaAfectada,
      Long idRegistro,
      String referencia,
      String accion,
      String descripcion,
      TipoOperacion tipoOperacion
  );

  void registrarAuditoria(
      String tablaAfectada,
      Long idRegistro,
      String referencia,
      String campoModificado,
      String valorAnterior,
      String valorNuevo,
      String descripcion
  );

  PageResponse<LogSistemaResponse> listarLogs(
      String accion,
      Rol rol,
      String usuario,
      int page,
      int size
  );

  List<LogSistemaResponse> obtenerLogsParaExportacion(
      String accion,
      Rol rol,
      String usuario
  );
}