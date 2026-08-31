package com.Trabajo_Final_Beltran.service;

import com.Trabajo_Final_Beltran.dto.request.SetupRequest;
import com.Trabajo_Final_Beltran.dto.response.SetupResponse;
import com.Trabajo_Final_Beltran.entity.Direccion;
import com.Trabajo_Final_Beltran.entity.Establecimiento;
import com.Trabajo_Final_Beltran.exception.BusinessException;
import com.Trabajo_Final_Beltran.mapper.DireccionMapper;
import com.Trabajo_Final_Beltran.mapper.SetupMapper;
import com.Trabajo_Final_Beltran.repository.DireccionRepository;
import com.Trabajo_Final_Beltran.repository.EstablecimientoRepository;
import com.Trabajo_Final_Beltran.util.NumeroUtils;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SetupService {

    private final EstablecimientoRepository establecimientoRepository;

    private final SetupMapper setupMapper;

    private final DireccionRepository direccionRepository;

    @Transactional
    public SetupResponse crearEstablecimiento(
            SetupRequest request
    ) {

      if (establecimientoRepository.count() > 0) {
        throw new BusinessException(
            "Este establecimiento ya fue configurado."
        );
      }

      normalizarDatos(
          request
      );

      validarHorarios(
          request
      );

      validarDatosUnicos(
           request
          );

      Direccion direccionGuardada =
          crearDireccionEstablecimiento(
              request
          );

        Establecimiento establecimiento =
                setupMapper.toEntity(request);

      establecimiento.setDireccion(
          direccionGuardada
      );

      Establecimiento establecimientoGuardado =
          establecimientoRepository.save(
              establecimiento
          );

      return setupMapper.toResponse(
          establecimientoGuardado
      );
    }

  private Direccion crearDireccionEstablecimiento(
      SetupRequest request
  ) {

    Direccion direccion =
        DireccionMapper.toEntity(
            request.getDireccion()
        );

    direccion.setEsPrincipal(
        true
    );

    direccion.setFechaCreacion(
        LocalDateTime.now()
    );

    return direccionRepository.save(
        direccion
    );
  }

  private void validarDatosUnicos(
      SetupRequest request
  ) {

    if (
        establecimientoRepository.existsByCuit(
            request.getCuit()
        )
    ) {
      throw new BusinessException(
          "El CUIT ya está registrado."
      );
    }

    if (
        establecimientoRepository.existsByEmail(
            request.getEmail()
        )
    ) {
      throw new BusinessException(
          "El email ya está registrado."
      );
    }
  }

    private void normalizarDatos(SetupRequest request) {
        request.setEmail(
            request.getEmail().trim().toLowerCase()
        );
        request.setCuit(
            NumeroUtils.limpiarNumero(request.getCuit())
        );
        request.setTelefono(
            NumeroUtils.limpiarNumero(request.getTelefono())
        );
    }

  private void validarHorarios(
      SetupRequest request
  ) {

    if (
        !request.getHorarioApertura().isBefore(
            request.getHorarioCierre()
        )
    ) {
      throw new BusinessException(
          "El horario de apertura debe ser anterior al horario de cierre."
      );
    }
  }
}