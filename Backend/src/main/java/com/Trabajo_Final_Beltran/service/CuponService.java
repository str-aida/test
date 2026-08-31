package com.Trabajo_Final_Beltran.service;

import com.Trabajo_Final_Beltran.dto.request.CreateCuponRequest;
import com.Trabajo_Final_Beltran.dto.request.UpdateCuponRequest;
import com.Trabajo_Final_Beltran.dto.response.CuponResponse;

import java.util.List;

public interface CuponService {

    CuponResponse crearCupon(CreateCuponRequest request);

    CuponResponse editarCupon(Long id, UpdateCuponRequest request);

    CuponResponse obtenerCuponPorId(Long id);

    List<CuponResponse> listarCupones();

    void desactivarCupon(Long id);
}