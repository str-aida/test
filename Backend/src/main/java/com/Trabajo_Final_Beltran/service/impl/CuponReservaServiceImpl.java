package com.Trabajo_Final_Beltran.service.impl;

import com.Trabajo_Final_Beltran.entity.Cupon;
import com.Trabajo_Final_Beltran.entity.Usuario;
import com.Trabajo_Final_Beltran.exception.BusinessException;
import com.Trabajo_Final_Beltran.repository.CuponUsuarioRepository;
import com.Trabajo_Final_Beltran.service.CuponReservaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CuponReservaServiceImpl implements CuponReservaService {

    private final CuponUsuarioRepository cuponUsuarioRepository;

    @Override
    @Transactional
    public void reservar(Cupon cupon, Usuario usuario, Long pedidoId) {
        int afectadas = cuponUsuarioRepository.marcarReservado(
                usuario.getId(), cupon.getId(), pedidoId);

        // 0 filas = alguien lo reservó entre la validación y este UPDATE (solo en concurrencia)
        if (afectadas == 0) {
            throw new BusinessException(
                "El cupón ya está reservado o no se encuentra disponible");
        }
    }

    @Override
    @Transactional
    public void liberarReserva(Cupon cupon, Usuario usuario) {
        // idempotente: si ya se liberó, no hace nada
        cuponUsuarioRepository.liberarReserva(usuario.getId(), cupon.getId());
    }
}