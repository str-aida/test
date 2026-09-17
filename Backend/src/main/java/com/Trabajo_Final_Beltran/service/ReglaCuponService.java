package com.Trabajo_Final_Beltran.service;

import com.Trabajo_Final_Beltran.dto.request.UpdateReglaCuponRequest;
import com.Trabajo_Final_Beltran.dto.response.ReglaCuponResponse;
import com.Trabajo_Final_Beltran.enums.TipoAsignacionCupon;

import java.util.List;

public interface ReglaCuponService {

    List<ReglaCuponResponse> listarReglas();

    ReglaCuponResponse obtenerReglaPorTipo(TipoAsignacionCupon tipoAsignacion);

    ReglaCuponResponse actualizarRegla(TipoAsignacionCupon tipoAsignacion, UpdateReglaCuponRequest request);
}
