package com.Trabajo_Final_Beltran.service.impl;

import com.Trabajo_Final_Beltran.dto.response.CuponUsuarioResponse;
import com.Trabajo_Final_Beltran.entity.Cupon;
import com.Trabajo_Final_Beltran.entity.CuponUsuario;
import com.Trabajo_Final_Beltran.entity.Usuario;
import com.Trabajo_Final_Beltran.enums.EstadoCupon;
import com.Trabajo_Final_Beltran.event.CuponAsignadoEvent;
import com.Trabajo_Final_Beltran.exception.BusinessException;
import com.Trabajo_Final_Beltran.mapper.CuponUsuarioMapper;
import com.Trabajo_Final_Beltran.repository.CuponRepository;
import com.Trabajo_Final_Beltran.repository.CuponUsuarioRepository;
import com.Trabajo_Final_Beltran.service.CuponUsuarioService;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CuponUsuarioServiceImpl implements CuponUsuarioService {

    private final CuponUsuarioRepository cuponUsuarioRepository;
    private final CuponRepository cuponRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public void asignarCupon(Usuario usuario, Cupon cupon) {

        if (cupon == null || cupon.getId() == null) {
            return; // cupón inválido/no persistido, se ignora
        }

        try {

            Cupon cuponBloqueado = cuponRepository.findByIdForUpdate(cupon.getId())
                    .orElseThrow(() -> new BusinessException("El cupón no existe"));

            validarCuponAsignable(cuponBloqueado);

            boolean tieneSinUsar = cuponUsuarioRepository
                    .existsByUsuarioIdAndCuponIdAndUsadoFalse(usuario.getId(), cuponBloqueado.getId());
            if (tieneSinUsar) {
                throw new BusinessException(
                    "El usuario ya tiene este cupón disponible y sin utilizar"
                );
            }

            CuponUsuario cuponUsuario = CuponUsuario.builder()
                    .usuario(usuario)
                    .cupon(cuponBloqueado)
                    .usado(false)
                    .build();
            cuponUsuarioRepository.save(cuponUsuario);

            if (cuponBloqueado.getUsoMaximo() != null) {
                cuponUsuarioRepository.flush();
                long totalAsignaciones = cuponUsuarioRepository.countByCuponId(cuponBloqueado.getId());
                if (totalAsignaciones >= cuponBloqueado.getUsoMaximo()) {
                    cuponBloqueado.setEstado(EstadoCupon.INACTIVO);
                    cuponRepository.save(cuponBloqueado);
                }
            }

            eventPublisher.publishEvent(new CuponAsignadoEvent(this, usuario, cuponBloqueado));

        } catch (ObjectOptimisticLockingFailureException e) {
            throw new BusinessException(
                "El cupón se agotó mientras se procesaba. Reintentá la operación."
            );
        }
    }


    private void validarCuponAsignable(Cupon cupon) {

        if (cupon.getEstado() != EstadoCupon.ACTIVO) {
            throw new BusinessException("El cupón no está activo");
        }

        LocalDate hoy = LocalDate.now();

        if (cupon.getFechaInicio() != null && hoy.isBefore(cupon.getFechaInicio())) {
            throw new BusinessException("El cupón aún no está vigente");
        }

        if (cupon.getFechaFin() != null && hoy.isAfter(cupon.getFechaFin())) {
            throw new BusinessException("El cupón ya no está vigente (venció)");
        }

        if (cupon.getUsoMaximo() != null) {
            long totalAsignaciones = cuponUsuarioRepository.countByCuponId(cupon.getId());
            if (totalAsignaciones >= cupon.getUsoMaximo()) {
                throw new BusinessException(
                    "El cupón ya no tiene cupos disponibles (llegó a su límite de usuarios)"
                );
            }
        }
    }

    @Override
    public List<CuponUsuarioResponse> obtenerCuponesActivos(Long usuarioId) {
        return cuponUsuarioRepository.findAllByUsuarioIdAndUsado(usuarioId, false)
                .stream()
                .map(CuponUsuarioMapper::toResponse)
                .toList();
    }
}