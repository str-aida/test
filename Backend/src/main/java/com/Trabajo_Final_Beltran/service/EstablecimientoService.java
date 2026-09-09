package com.Trabajo_Final_Beltran.service;

import com.Trabajo_Final_Beltran.dto.request.UpdateEstablecimientoRequest;
import com.Trabajo_Final_Beltran.dto.response.EstablecimientoClienteResponse;
import com.Trabajo_Final_Beltran.dto.response.EstablecimientoResponse;
import org.springframework.web.multipart.MultipartFile;

public interface EstablecimientoService {

  EstablecimientoResponse obtenerEstablecimiento();

  EstablecimientoResponse actualizarEstablecimiento(
      UpdateEstablecimientoRequest request);

  EstablecimientoClienteResponse obtenerInfoCliente(Long id);

  EstablecimientoClienteResponse obtenerInfoClienteActual();

  EstablecimientoResponse actualizarLogo(MultipartFile logo);

}
