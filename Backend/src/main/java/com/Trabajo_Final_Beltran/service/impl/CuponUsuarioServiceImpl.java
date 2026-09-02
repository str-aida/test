package com.Trabajo_Final_Beltran.service.impl;

import com.Trabajo_Final_Beltran.dto.response.CuponUsuarioResponse;
import com.Trabajo_Final_Beltran.entity.Cupon;
import com.Trabajo_Final_Beltran.entity.CuponUsuario;
import com.Trabajo_Final_Beltran.entity.Usuario;
import com.Trabajo_Final_Beltran.enums.EstadoCupon;
import com.Trabajo_Final_Beltran.event.CuponAsignadoEvent;
import com.Trabajo_Final_Beltran.exception.BusinessException;
import com.Trabajo_Final_Beltran.mapper.CuponUsuarioMapper;
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
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public void asignarCupon(Usuario usuario, Cupon cupon) {

        if (cupon == null || cupon.getId() == null) {
            return; // cupón inválido/no persistido, se ignora
        }

        try {
            validarCuponAsignable(cupon);
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new BusinessException(
                "El cupón se agotó mientras se procesaba. Reintentá la operación."
            );
        }

        boolean tieneSinUsar = cuponUsuarioRepository
                .existsByUsuarioIdAndCuponIdAndUsadoFalse(usuario.getId(), cupon.getId());
        if (tieneSinUsar) {
            throw new BusinessException(
                "El usuario ya tiene este cupón disponible y sin utilizar"
            );
        }

        try {
            CuponUsuario cuponUsuario = CuponUsuario.builder()
                    .usuario(usuario)
                    .cupon(cupon)
                    .usado(false)
                    .build();
            cuponUsuarioRepository.save(cuponUsuario);

            eventPublisher.publishEvent(new CuponAsignadoEvent(this, usuario, cupon));
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new BusinessException(
                "El cupón se agotó mientras se procesaba. Reintentá la operación."
            );
        }
    }

    /**
     * Valida que un cupón cumple todas las condiciones para ser asignable:
     * estado ACTIVO, dentro de la vigencia y sin superar el uso máximo.
     * Todos los campos opcionales son null-safe.
     */
    private void validarCuponAsignable(Cupon cupon) {

        // 1. Estado activo
        if (cupon.getEstado() != EstadoCupon.ACTIVO) {
            throw new BusinessException("El cupón no está activo y no puede ser asignado");
        }

        // 2. Usos disponibles (null-safe: null en usoMaximo = sin límite)
        if (cupon.getUsoMaximo() != null) {
            int usosActuales = cupon.getUsosActuales() != null ? cupon.getUsosActuales() : 0;
            if (usosActuales >= cupon.getUsoMaximo()) {
                throw new BusinessException("El cupón alcanzó su límite máximo de usos");
            }
        }

        // 3. Vigencia de fechas (null-safe: null = sin restricción de fecha)
        LocalDate hoy = LocalDate.now();

        if (cupon.getFechaInicio() != null && hoy.isBefore(cupon.getFechaInicio())) {
            throw new BusinessException("El cupón aún no está vigente");
        }

        if (cupon.getFechaFin() != null && hoy.isAfter(cupon.getFechaFin())) {
            throw new BusinessException("El cupón ya no está vigente (venció)");
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