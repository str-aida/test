package com.Trabajo_Final_Beltran.service.impl;

import com.Trabajo_Final_Beltran.dto.request.CreateCuponRequest;
import com.Trabajo_Final_Beltran.dto.request.UpdateCuponRequest;
import com.Trabajo_Final_Beltran.dto.response.CuponResponse;
import com.Trabajo_Final_Beltran.entity.Cupon;
import com.Trabajo_Final_Beltran.enums.EstadoCupon;
import com.Trabajo_Final_Beltran.enums.TipoDescuento;
import com.Trabajo_Final_Beltran.exception.BusinessException;
import com.Trabajo_Final_Beltran.mapper.CuponMapper;
import com.Trabajo_Final_Beltran.repository.CuponRepository;
import com.Trabajo_Final_Beltran.service.CuponService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CuponServiceImpl implements CuponService {

    private final CuponRepository cuponRepository;

    @Override
    @Transactional
    public CuponResponse crearCupon(CreateCuponRequest request) {

        validarCodigoUnico(request.getCodigo());
        validarFechas(request.getFechaInicio(), request.getFechaFin());
        validarValorSegunTipo(request.getTipoDescuento(), request.getValor());

        Cupon cupon = CuponMapper.toEntity(request);

        Cupon cuponGuardado = cuponRepository.save(cupon);

        return CuponMapper.toResponse(cuponGuardado);
    }

    @Override
    @Transactional
    public CuponResponse editarCupon(Long id, UpdateCuponRequest request) {

        Cupon cupon = cuponRepository.findById(id)
                .orElseThrow(() ->
                        new BusinessException("Cupón no encontrado")
                );

        validarFechas(request.getFechaInicio(), request.getFechaFin());
        validarValorSegunTipo(request.getTipoDescuento(), request.getValor());

        if (request.getUsoMaximo() != null
                && cupon.getUsosActuales() > request.getUsoMaximo()) {
            throw new BusinessException(
                "El uso máximo no puede ser menor a los usos ya registrados ("
                + cupon.getUsosActuales() + ")"
            );
        }

        CuponMapper.updateFromRequest(cupon, request);

        Cupon cuponActualizado = cuponRepository.save(cupon);

        return CuponMapper.toResponse(cuponActualizado);
    }

    @Override
    public CuponResponse obtenerCuponPorId(Long id) {

        Cupon cupon = cuponRepository.findById(id)
                .orElseThrow(() ->
                        new BusinessException("Cupón no encontrado")
                );

        return CuponMapper.toResponse(cupon);
    }

    @Override
    public List<CuponResponse> listarCupones() {

        return cuponRepository.findAll()
                .stream()
                .map(CuponMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void desactivarCupon(Long id) {

        Cupon cupon = cuponRepository.findById(id)
                .orElseThrow(() ->
                        new BusinessException("Cupón no encontrado")
                );

        if (cupon.getEstado() == EstadoCupon.INACTIVO) {
            throw new BusinessException("El cupón ya está desactivado");
        }

        cupon.setEstado(EstadoCupon.INACTIVO);

        cuponRepository.save(cupon);
    }

    private void validarCodigoUnico(String codigo) {
        if (cuponRepository.existsByCodigo(codigo)) {
            throw new BusinessException(
                "Ya existe un cupón con el código " + codigo
            );
        }
    }

    private void validarFechas(
            java.time.LocalDate fechaInicio,
            java.time.LocalDate fechaFin
    ) {
        if (fechaFin.isBefore(fechaInicio)) {
            throw new BusinessException(
                "La fecha de fin no puede ser anterior a la fecha de inicio"
            );
        }
    }

    private void validarValorSegunTipo(
            TipoDescuento tipoDescuento,
            BigDecimal valor
    ) {
        if (tipoDescuento == TipoDescuento.PORCENTAJE
                && valor.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new BusinessException(
                "Un cupón de porcentaje no puede superar el 100%"
            );
        }
    }
}