package com.Trabajo_Final_Beltran.service.strategy.cupon;

import com.Trabajo_Final_Beltran.entity.Cupon;
import com.Trabajo_Final_Beltran.entity.ReglaCupon;
import com.Trabajo_Final_Beltran.entity.Usuario;
import com.Trabajo_Final_Beltran.enums.EstadoCupon;
import com.Trabajo_Final_Beltran.enums.TipoAsignacionCupon;
import com.Trabajo_Final_Beltran.repository.CuponRepository;
import com.Trabajo_Final_Beltran.repository.ReglaCuponRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class BienvenidaStrategy implements AsignacionCuponStrategy {

    private final CuponRepository cuponRepository;
    private final ReglaCuponRepository reglaCuponRepository;

    @Override
    public List<Cupon> obtenerCupones(Usuario usuario) {
        Optional<ReglaCupon> reglaOpt = reglaCuponRepository.findByTipoAsignacion(TipoAsignacionCupon.BIENVENIDA);
        if (reglaOpt.isEmpty() || Boolean.FALSE.equals(reglaOpt.get().getActivo())) {
            log.info("Regla de bienvenida desactivada o inexistente. Usuario {}", usuario.getId());
            return List.of();
        }

        ReglaCupon regla = reglaOpt.get();
        String codigo = "BIENVENIDA-" + usuario.getId();

        Cupon cupon = cuponRepository.findByCodigo(codigo)
                .orElseGet(() -> crearCuponBienvenida(codigo, regla));

        return List.of(cupon);
    }

    private Cupon crearCuponBienvenida(String codigo, ReglaCupon regla) {
        int diasValidez = regla.getDiasValidez() != null ? regla.getDiasValidez() : 30;

        Cupon cupon = Cupon.builder()
                .codigo(codigo)
                .tipoDescuento(regla.getTipoDescuento())
                .valor(regla.getValor())
                .fechaInicio(LocalDate.now())
                .fechaFin(LocalDate.now().plusDays(diasValidez))
                .usoMaximo(1)
                .estado(EstadoCupon.ACTIVO)
                .tipoAsignacion(TipoAsignacionCupon.BIENVENIDA)
                .build();

        try {
            Cupon guardado = cuponRepository.save(cupon);
            log.info(">>> Cupón de bienvenida creado: {} para usuario con id (idCupon={})", codigo, guardado.getId());
            return guardado;
        } catch (DataIntegrityViolationException e) {
            log.warn(">>> Conflicto concurrente al crear cupón de bienvenida {}, recuperando existente", codigo);
            return cuponRepository.findByCodigo(codigo)
                    .orElseThrow(() -> new IllegalStateException(
                            "No se pudo recuperar el cupón concurrente " + codigo, e));
        }
    }
}
