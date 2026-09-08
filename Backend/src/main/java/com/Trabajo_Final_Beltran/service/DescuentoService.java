
package com.Trabajo_Final_Beltran.service;

import com.Trabajo_Final_Beltran.dto.request.CreateDescuentoRequest;
import com.Trabajo_Final_Beltran.dto.request.UpdateDescuentoRequest;
import com.Trabajo_Final_Beltran.dto.response.DescuentoResponse;
import com.Trabajo_Final_Beltran.dto.response.MensajeResponse;

import java.util.List;

public interface DescuentoService {

    DescuentoResponse crearDescuento(CreateDescuentoRequest request);

    DescuentoResponse editarDescuento(Long id, UpdateDescuentoRequest request);

    List<DescuentoResponse> listarDescuentos();

    DescuentoResponse obtenerPorId(Long id);

    MensajeResponse activar(Long id);

    MensajeResponse desactivar(Long id);
}