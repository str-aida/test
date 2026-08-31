package com.Trabajo_Final_Beltran.mapper;

import com.Trabajo_Final_Beltran.dto.request.SetupRequest;
import com.Trabajo_Final_Beltran.dto.response.SetupResponse;
import com.Trabajo_Final_Beltran.entity.Establecimiento;
import com.Trabajo_Final_Beltran.enums.EstadoEstablecimiento;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class SetupMapper {

    public Establecimiento toEntity(SetupRequest request) {

        return Establecimiento.builder()
                .nombre(request.getNombre())
                .razonSocial(request.getRazonSocial())
                .cuit(request.getCuit())
                .email(request.getEmail())
                .telefono(request.getTelefono())
                .horarioApertura(request.getHorarioApertura())
                .horarioCierre(request.getHorarioCierre())
                .diasHabiles(request.getDiasHabiles())
                .descripcion(request.getDescripcion())
                .tipoServicio(request.getTipoServicio())
                .estado(EstadoEstablecimiento.ACTIVO)
                .fechaCreacion(LocalDateTime.now())
                .build();
    }

    public SetupResponse toResponse(Establecimiento establecimiento) {

        return SetupResponse.builder()
                .mensaje("Establecimiento creado correctamente")
                .idEstablecimiento(establecimiento.getId())
                .build();
    }
}