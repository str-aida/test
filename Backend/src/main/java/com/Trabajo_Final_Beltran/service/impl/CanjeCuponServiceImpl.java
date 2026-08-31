package com.Trabajo_Final_Beltran.service.impl;

import com.Trabajo_Final_Beltran.entity.Cupon;
import com.Trabajo_Final_Beltran.entity.CuponUsuario;
import com.Trabajo_Final_Beltran.entity.Usuario;
import com.Trabajo_Final_Beltran.exception.BusinessException;
import com.Trabajo_Final_Beltran.repository.CuponRepository;
import com.Trabajo_Final_Beltran.repository.CuponUsuarioRepository;
import com.Trabajo_Final_Beltran.service.CanjeCuponService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CanjeCuponServiceImpl implements CanjeCuponService {

    private final CuponUsuarioRepository cuponUsuarioRepository;
    private final CuponRepository cuponRepository;

    @Override
    @Transactional
    public void canjear(Cupon cupon, Usuario usuario) {

        CuponUsuario cuponUsuario = cuponUsuarioRepository
                .findByUsuarioIdAndCuponId(usuario.getId(), cupon.getId())
                .orElseThrow(() ->
                        new BusinessException(
                            "No se encontró la asignación del cupón para este usuario"
                        )
                );

        if (Boolean.TRUE.equals(cuponUsuario.getUsado())) {
            throw new BusinessException("El cupón ya fue canjeado");
        }

        cuponUsuario.setUsado(true);
        cuponUsuario.setReservado(false);
        cuponUsuario.setFechaUso(LocalDateTime.now());
        cuponUsuarioRepository.save(cuponUsuario);
        cupon.setUsosActuales(cupon.getUsosActuales() + 1);
        cuponRepository.save(cupon);
    }
    
    @Override
    @Transactional
    public void deshacerCanje(Cupon cupon, Usuario usuario) {
        CuponUsuario cuponUsuario = cuponUsuarioRepository
                .findByUsuarioIdAndCuponId(usuario.getId(), cupon.getId())
                .orElseThrow(() -> new BusinessException(
                        "No se encontró la asignación del cupón para este usuario"));


        if (!Boolean.TRUE.equals(cuponUsuario.getUsado())) {
            return;
        }

        // El cupón vuelve a DISPONIBLE: ni usado, ni reservado.
        cuponUsuario.setUsado(false);
        cuponUsuario.setFechaUso(null);
        cuponUsuario.setReservado(false);
        cuponUsuario.setPedidoReservaId(null);
        cuponUsuarioRepository.save(cuponUsuario);

        int usos = cupon.getUsosActuales() == null ? 0 : cupon.getUsosActuales();
        cupon.setUsosActuales(Math.max(0, usos - 1));
        cuponRepository.save(cupon);
    }
}