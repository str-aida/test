package com.Trabajo_Final_Beltran.service.impl;

import com.Trabajo_Final_Beltran.dto.request.UpdateEstablecimientoRequest;
import com.Trabajo_Final_Beltran.dto.response.EstablecimientoClienteResponse;
import com.Trabajo_Final_Beltran.dto.response.EstablecimientoResponse;
import com.Trabajo_Final_Beltran.entity.Direccion;
import com.Trabajo_Final_Beltran.entity.Establecimiento;
import com.Trabajo_Final_Beltran.entity.Usuario;
import com.Trabajo_Final_Beltran.exception.BusinessException;
import com.Trabajo_Final_Beltran.mapper.EstablecimientoMapper;
import com.Trabajo_Final_Beltran.repository.DireccionRepository;
import com.Trabajo_Final_Beltran.repository.EstablecimientoRepository;
import com.Trabajo_Final_Beltran.repository.UsuarioRepository;
import com.Trabajo_Final_Beltran.security.SecurityUtils;
import com.Trabajo_Final_Beltran.service.EstablecimientoService;
import com.Trabajo_Final_Beltran.util.NumeroUtils;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EstablecimientoServiceImpl implements EstablecimientoService {

  private final UsuarioRepository usuarioRepository;

  private final EstablecimientoRepository establecimientoRepository;

  private final DireccionRepository direccionRepository;

  @Override
  public EstablecimientoResponse obtenerEstablecimiento() {

    Establecimiento establecimiento =
        obtenerEstablecimientoAutenticado();

    return EstablecimientoMapper.toResponse(
        establecimiento
    );
  }

  @Override
  public EstablecimientoClienteResponse obtenerInfoCliente(Long id) {
    Establecimiento establecimiento = establecimientoRepository.findById(id)
        .orElseThrow(() -> new BusinessException("Establecimiento no encontrado."));

    return EstablecimientoMapper.toClienteResponse(establecimiento);
  }

  @Override
  public EstablecimientoClienteResponse obtenerInfoClienteActual() {
    Establecimiento establecimiento = obtenerEstablecimientoAutenticado();
    return EstablecimientoMapper.toClienteResponse(establecimiento);
  }

  @Override
  @Transactional
  public EstablecimientoResponse actualizarEstablecimiento(
      UpdateEstablecimientoRequest request) {

    Establecimiento establecimiento =
        obtenerEstablecimientoAutenticado();

    normalizarDatos(request);

    validarHorarios(request);

    validarEmail(request, establecimiento);

    actualizarDatosEstablecimiento(establecimiento, request);

    Direccion direccion =
        actualizarDireccion(establecimiento, request);

    direccionRepository.save(direccion);

    establecimientoRepository.save(establecimiento);

    return EstablecimientoMapper.toResponse(establecimiento);
  }

  private Establecimiento obtenerEstablecimientoAutenticado() {

    Usuario usuarioAutenticado =
        SecurityUtils.obtenerUsuarioAutenticado();

    Usuario usuario =
        usuarioRepository.findByEmail(usuarioAutenticado.getEmail())
            .orElseThrow(() -> new BusinessException(
                "No se encontró el usuario autenticado."
            ));

    Establecimiento establecimiento =
        usuario.getEstablecimiento();

    if (establecimiento == null) {
      throw new BusinessException(
          "El usuario no tiene un establecimiento asociado."
      );
    }

    return establecimiento;
  }

    private void normalizarDatos(UpdateEstablecimientoRequest request) {
        request.setEmail(
            request.getEmail()
                .trim()
                .toLowerCase()
        );
        request.setTelefono(
            NumeroUtils.limpiarNumero(request.getTelefono())  // CAMBIADO
        );
    }

  private void validarHorarios(UpdateEstablecimientoRequest request) {

    if (!request.getHorarioApertura().isBefore(request.getHorarioCierre())) {
      throw new BusinessException(
          "El horario de apertura debe ser anterior al horario de cierre."
      );
    }
  }

  private void validarEmail(
      UpdateEstablecimientoRequest request,
      Establecimiento establecimiento) {

    Optional<Establecimiento> establecimientoConEmail =
        establecimientoRepository.findByEmail(request.getEmail());

    if (establecimientoConEmail.isPresent()
        && !establecimientoConEmail.get()
        .getId().equals(establecimiento.getId())) {

      throw new BusinessException(
          "El email ya está registrado."
      );
    }
  }

  private void actualizarDatosEstablecimiento(
      Establecimiento establecimiento,
      UpdateEstablecimientoRequest request) {

    establecimiento.setNombre(request.getNombre());
    establecimiento.setRazonSocial(request.getRazonSocial());
    establecimiento.setEmail(request.getEmail());
    establecimiento.setTelefono(request.getTelefono());
    establecimiento.setHorarioApertura(request.getHorarioApertura());
    establecimiento.setHorarioCierre(request.getHorarioCierre());
    establecimiento.setDiasHabiles(request.getDiasHabiles());
    establecimiento.setDescripcion(request.getDescripcion());
    establecimiento.setTipoServicio(request.getTipoServicio());
  }

  private Direccion actualizarDireccion(
      Establecimiento establecimiento,
      UpdateEstablecimientoRequest request) {

    Direccion direccion = establecimiento.getDireccion();

    if (direccion == null) {
      throw new BusinessException(
          "El establecimiento no tiene una dirección asociada."
      );
    }

    direccion.setNombre(request.getDireccion().getNombre());
    direccion.setCalle(request.getDireccion().getCalle());
    direccion.setNumero(request.getDireccion().getNumero());
    direccion.setLocalidad(request.getDireccion().getLocalidad());
    direccion.setPiso(request.getDireccion().getPiso());
    direccion.setDepartamento(request.getDireccion().getDepartamento());
    direccion.setCodigoPostal(request.getDireccion().getCodigoPostal());
    direccion.setReferencia(request.getDireccion().getReferencia());

    return direccion;
  }
}