package com.Trabajo_Final_Beltran.service.impl;

import com.Trabajo_Final_Beltran.dto.request.UpdateReglaCuponRequest;
import com.Trabajo_Final_Beltran.dto.response.ReglaCuponResponse;
import com.Trabajo_Final_Beltran.entity.ReglaCupon;
import com.Trabajo_Final_Beltran.enums.TipoAsignacionCupon;
import com.Trabajo_Final_Beltran.enums.TipoDescuento;
import com.Trabajo_Final_Beltran.exception.BusinessException;
import com.Trabajo_Final_Beltran.mapper.ReglaCuponMapper;
import com.Trabajo_Final_Beltran.repository.ReglaCuponRepository;
import com.Trabajo_Final_Beltran.service.ReglaCuponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReglaCuponServiceImpl implements ReglaCuponService {

    private final ReglaCuponRepository reglaCuponRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ReglaCuponResponse> listarReglas() {
        return reglaCuponRepository.findAll()
                .stream()
                .map(ReglaCuponMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ReglaCuponResponse obtenerReglaPorTipo(TipoAsignacionCupon tipoAsignacion) {
        ReglaCupon regla = reglaCuponRepository.findByTipoAsignacion(tipoAsignacion)
                .orElseThrow(() -> new BusinessException("No existe una regla configurada para: " + tipoAsignacion));

        return ReglaCuponMapper.toResponse(regla);
    }

    @Override
    @Transactional
    public ReglaCuponResponse actualizarRegla(TipoAsignacionCupon tipoAsignacion, UpdateReglaCuponRequest request) {
        ReglaCupon regla = reglaCuponRepository.findByTipoAsignacion(tipoAsignacion)
                .orElseThrow(() -> new BusinessException("No existe una regla configurada para: " + tipoAsignacion));

        validarValorSegunTipo(request.getTipoDescuento(), request.getValor());
        validarParametrosEspecificos(tipoAsignacion, request);

        ReglaCuponMapper.updateEntity(regla, request);
        ReglaCupon guardada = reglaCuponRepository.save(regla);

        return ReglaCuponMapper.toResponse(guardada);
    }

    private void validarValorSegunTipo(TipoDescuento tipoDescuento, BigDecimal valor) {
        if (tipoDescuento == TipoDescuento.PORCENTAJE && valor.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new BusinessException("Un descuento porcentual no puede superar el 100%");
        }
    }

    private void validarParametrosEspecificos(TipoAsignacionCupon tipoAsignacion, UpdateReglaCuponRequest request) {
        if (tipoAsignacion == TipoAsignacionCupon.CANTIDAD_COMPRAS) {
            if (request.getCantidadComprasRequeridas() == null || request.getCantidadComprasRequeridas() < 1) {
                throw new BusinessException("La regla de compras requiere una cantidad de compras mayor a 0");
            }
        }
    }
}
