/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.Trabajo_Final_Beltran.service.impl;

import com.Trabajo_Final_Beltran.dto.request.CreateDireccionRequest;
import com.Trabajo_Final_Beltran.dto.request.UpdateDireccionRequest;
import com.Trabajo_Final_Beltran.dto.response.DireccionResponse;
import com.Trabajo_Final_Beltran.entity.Direccion;
import com.Trabajo_Final_Beltran.entity.Usuario;
import com.Trabajo_Final_Beltran.enums.EstadoDireccion;
import com.Trabajo_Final_Beltran.exception.BusinessException;
import com.Trabajo_Final_Beltran.mapper.DireccionMapper;
import com.Trabajo_Final_Beltran.repository.DireccionRepository;
import com.Trabajo_Final_Beltran.security.SecurityUtils;
import com.Trabajo_Final_Beltran.service.DireccionService;
import com.Trabajo_Final_Beltran.util.TextNormalizerUtil;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DireccionServiceImpl
    implements DireccionService {

  private final DireccionRepository
      direccionRepository;

  private Direccion obtenerDireccionDelUsuario(
      Long direccionId
  ) {

    Usuario usuario =
        SecurityUtils
            .obtenerUsuarioAutenticado();

    return direccionRepository
        .findByIdAndUsuarioIdAndEstado(
            direccionId,
            usuario.getId(),
            EstadoDireccion.ACTIVA
        )
        .orElseThrow(() ->
            new BusinessException(
                "Dirección no encontrada"
            )
        );
  }

  @Override
  public DireccionResponse crearDireccion(
      CreateDireccionRequest request
  ) {

    Usuario usuario =
        SecurityUtils
            .obtenerUsuarioAutenticado();

    boolean primeraDireccion =
        !direccionRepository
            .existsByUsuarioId(
                usuario.getId()
            );

    Direccion direccion =
        DireccionMapper
            .toEntity(request);
    
    normalizarDireccion(direccion);

    direccion.setUsuario(
        usuario
    );

    direccion.setFechaCreacion(
        LocalDateTime.now()
    );

    if (primeraDireccion) {

      direccion.setEsPrincipal(
          true
      );

    } else {

      boolean marcarComoPrincipal =
          request.getEsPrincipal() != null
              &&
              request.getEsPrincipal();

      if (marcarComoPrincipal) {

        direccionRepository
            .findByUsuarioIdAndEsPrincipalTrue(
                usuario.getId()
            )
            .ifPresent(
                direccionActual -> {

                  direccionActual
                      .setEsPrincipal(
                          false
                      );

                  direccionRepository
                      .save(
                          direccionActual
                      );
                }
            );

        direccion.setEsPrincipal(
            true
        );

      } else {

        direccion.setEsPrincipal(
            false
        );
      }
    }

    Direccion direccionGuardada =
        direccionRepository
            .save(
                direccion
            );

    return DireccionMapper
        .toResponse(
            direccionGuardada
        );
  }

  @Override
  public List<DireccionResponse>
  listarDirecciones() {

    Usuario usuario =
        SecurityUtils
            .obtenerUsuarioAutenticado();

    return direccionRepository
        .findAllByUsuarioIdAndEstado(
            usuario.getId(),
            EstadoDireccion.ACTIVA
        )
        .stream()
        .map(
            DireccionMapper
                ::toResponse
        )
        .toList();
  }

  @Override
  public DireccionResponse editarDireccion(
      Long id,
      UpdateDireccionRequest request
  ) {

    Direccion direccion =
        obtenerDireccionDelUsuario(
            id
        );
    
    normalizarDireccion(direccion);

    direccion.setNombre(
        request.getNombre()
    );

    direccion.setCalle(
        request.getCalle()
    );

    direccion.setNumero(
        request.getNumero()
    );

    direccion.setLocalidad(
        request.getLocalidad()
    );

    direccion.setPiso(
        request.getPiso()
    );

    direccion.setDepartamento(
        request.getDepartamento()
    );

    direccion.setCodigoPostal(
        request.getCodigoPostal()
    );

    direccion.setReferencia(
        request.getReferencia()
    );

    Direccion direccionActualizada =
        direccionRepository
            .save(
                direccion
            );

    return DireccionMapper
        .toResponse(
            direccionActualizada
        );
  }

  @Override
  public DireccionResponse marcarComoPrincipal(
      Long id
  ) {

    Usuario usuario =
        SecurityUtils
            .obtenerUsuarioAutenticado();

    Direccion direccionNuevaPrincipal =
        obtenerDireccionDelUsuario(
            id
        );

    direccionRepository
        .findByUsuarioIdAndEsPrincipalTrue(
            usuario.getId()
        )
        .ifPresent(
            direccionActual -> {

              direccionActual
                  .setEsPrincipal(
                      false
                  );

              direccionRepository
                  .save(
                      direccionActual
                  );
            }
        );

    direccionNuevaPrincipal
        .setEsPrincipal(
            true
        );

    Direccion direccionGuardada =
        direccionRepository
            .save(
                direccionNuevaPrincipal
            );

    return DireccionMapper
        .toResponse(
            direccionGuardada
        );
  }

  @Override
  public void eliminarDireccion(
      Long id
  ) {

    Direccion direccion =
        obtenerDireccionDelUsuario(
            id
        );

    direccion.setEstado(
        EstadoDireccion.INACTIVA
    );

    direccionRepository
        .save(
            direccion
        );
  }
  
    private void normalizarDireccion(Direccion direccion) {
      direccion.setNombre(
          TextNormalizerUtil.normalizarTexto(direccion.getNombre())
      );

      direccion.setCalle(
          TextNormalizerUtil.normalizarTexto(direccion.getCalle())
      );

      direccion.setLocalidad(
          TextNormalizerUtil.normalizarTexto(direccion.getLocalidad())
      );

      direccion.setNumero(
          TextNormalizerUtil.normalizarNumero(direccion.getNumero())
      );
  }
  
}