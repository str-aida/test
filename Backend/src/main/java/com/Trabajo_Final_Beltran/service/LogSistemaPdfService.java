package com.Trabajo_Final_Beltran.service;

import com.Trabajo_Final_Beltran.dto.response.LogSistemaResponse;
import java.util.List;

public interface LogSistemaPdfService {

  byte[] generarPdf(
      List<LogSistemaResponse> logs
  );

}