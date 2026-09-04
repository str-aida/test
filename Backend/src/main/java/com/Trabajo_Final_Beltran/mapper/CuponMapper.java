package com.Trabajo_Final_Beltran.mapper;


import com.Trabajo_Final_Beltran.dto.request.CreateCuponRequest;
import com.Trabajo_Final_Beltran.dto.request.UpdateCuponRequest;
import com.Trabajo_Final_Beltran.dto.response.CuponResponse;
import com.Trabajo_Final_Beltran.entity.Cupon;
import com.Trabajo_Final_Beltran.enums.EstadoCupon;

public class CuponMapper {

    private CuponMapper() {
    }

    public static Cupon toEntity(CreateCuponRequest request) {
        return Cupon.builder()
                .codigo(request.getCodigo())
                .tipoDescuento(request.getTipoDescuento())
                .valor(request.getValor())
                .fechaInicio(request.getFechaInicio())
                .fechaFin(request.getFechaFin())
                .usoMaximo(request.getUsoMaximo())
                .tipoAsignacion(request.getTipoAsignacion()) 
                .usosActuales(0)
                .estado(EstadoCupon.ACTIVO)
                .build();
    }

    public static void updateFromRequest(Cupon cupon, UpdateCuponRequest request) {
        cupon.setTipoDescuento(request.getTipoDescuento());
        cupon.setValor(request.getValor());
        cupon.setFechaInicio(request.getFechaInicio());
        cupon.setFechaFin(request.getFechaFin());
        cupon.setUsoMaximo(request.getUsoMaximo());
        cupon.setEstado(request.getEstado());
    }

    public static CuponResponse toResponse(Cupon cupon) {
        return buildResponse(cupon, null);
    }

    public static CuponResponse toResponse(Cupon cupon, long totalAsignaciones) {
        Integer cuposDisponibles = null;
        if (cupon.getUsoMaximo() != null) {
            cuposDisponibles = Math.max(cupon.getUsoMaximo() - (int) totalAsignaciones, 0);
        }
        return buildResponse(cupon, cuposDisponibles);
    }

    private static CuponResponse buildResponse(Cupon cupon, Integer cuposDisponibles) {
        return CuponResponse.builder()
                .id(cupon.getId())
                .codigo(cupon.getCodigo())
                .tipoDescuento(cupon.getTipoDescuento())
                .valor(cupon.getValor())
                .fechaInicio(cupon.getFechaInicio())
                .fechaFin(cupon.getFechaFin())
                .usoMaximo(cupon.getUsoMaximo())
                .usosActuales(cupon.getUsosActuales())
                .estado(cupon.getEstado())
                .cuposDisponibles(cuposDisponibles)
                .build();
    }
}