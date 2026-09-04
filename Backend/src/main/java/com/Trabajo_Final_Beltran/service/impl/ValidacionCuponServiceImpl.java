package com.Trabajo_Final_Beltran.service.impl;

import com.Trabajo_Final_Beltran.entity.Cupon;
import com.Trabajo_Final_Beltran.entity.CuponUsuario;
import com.Trabajo_Final_Beltran.entity.Usuario;
import com.Trabajo_Final_Beltran.enums.EstadoCupon;
import com.Trabajo_Final_Beltran.exception.BusinessException;
import com.Trabajo_Final_Beltran.repository.CuponRepository;
import com.Trabajo_Final_Beltran.repository.CuponUsuarioRepository;
import com.Trabajo_Final_Beltran.service.ValidacionCuponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ValidacionCuponServiceImpl implements ValidacionCuponService {

    private final CuponRepository cuponRepository;
    private final CuponUsuarioRepository cuponUsuarioRepository;

    @Override
    public Cupon validarCupon(String codigo, Usuario usuario) {

        Cupon cupon = cuponRepository.findByCodigo(codigo)
                .orElseThrow(() ->
                        new BusinessException("El cupón no existe")
                );

        validarEstadoActivo(cupon);
        validarVigencia(cupon);
        validarUsosDisponibles(cupon);
        validarAsignadoYNoUsado(cupon, usuario);

        return cupon;
    }

    private void validarEstadoActivo(Cupon cupon) {
        if (cupon.getEstado() != EstadoCupon.ACTIVO) {
            boolean agotadoPorCupos = cupon.getUsoMaximo() != null
                    && cuponUsuarioRepository.countByCuponId(cupon.getId()) >= cupon.getUsoMaximo();

            if (!agotadoPorCupos) {
                throw new BusinessException("El cupón no está activo");
            }
        }
    }

    private void validarVigencia(Cupon cupon) {
        LocalDate hoy = LocalDate.now();

        if (hoy.isBefore(cupon.getFechaInicio())) {
            throw new BusinessException("El cupón todavía no está vigente");
        }

        if (hoy.isAfter(cupon.getFechaFin())) {
            throw new BusinessException("El cupón ya expiró");
        }
    }

    private void validarUsosDisponibles(Cupon cupon) {
        if (cupon.getUsoMaximo() != null
                && cupon.getUsosActuales() >= cupon.getUsoMaximo()) {
            throw new BusinessException("El cupón alcanzó su límite de usos");
        }
    }

    private void validarAsignadoYNoUsado(Cupon cupon, Usuario usuario) {

        CuponUsuario cuponUsuario = cuponUsuarioRepository
                .findByUsuarioIdAndCuponIdAndUsado(usuario.getId(), cupon.getId(), false)
                .orElseThrow(() ->
                        new BusinessException("No tenés este cupón disponible o ya fue utilizado"));

        if (Boolean.TRUE.equals(cuponUsuario.getUsado())) {
            throw new BusinessException("Ya utilizaste este cupón anteriormente");
        }

        if (Boolean.TRUE.equals(cuponUsuario.getReservado())) {
            throw new BusinessException(
                "Este cupón ya está reservado en otro pedido pendiente");
        }
    }
}