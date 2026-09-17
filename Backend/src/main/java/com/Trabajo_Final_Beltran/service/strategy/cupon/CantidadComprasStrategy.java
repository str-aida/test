package com.Trabajo_Final_Beltran.service.strategy.cupon;

import com.Trabajo_Final_Beltran.entity.Cupon;
import com.Trabajo_Final_Beltran.entity.ReglaCupon;
import com.Trabajo_Final_Beltran.entity.Usuario;
import com.Trabajo_Final_Beltran.enums.EstadoCupon;
import com.Trabajo_Final_Beltran.enums.EstadoPedido;
import com.Trabajo_Final_Beltran.enums.TipoAsignacionCupon;
import com.Trabajo_Final_Beltran.repository.CuponRepository;
import com.Trabajo_Final_Beltran.repository.PedidoRepository;
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
public class CantidadComprasStrategy implements AsignacionCuponStrategy {

    private final PedidoRepository pedidoRepository;
    private final CuponRepository cuponRepository;
    private final ReglaCuponRepository reglaCuponRepository;

    @Override
    public List<Cupon> obtenerCupones(Usuario usuario) {
        Optional<ReglaCupon> reglaOpt = reglaCuponRepository.findByTipoAsignacion(TipoAsignacionCupon.CANTIDAD_COMPRAS);
        if (reglaOpt.isEmpty() || Boolean.FALSE.equals(reglaOpt.get().getActivo())) {
            log.info("Regla de cantidad de compras desactivada o inexistente. Usuario {}", usuario.getId());
            return List.of();
        }

        ReglaCupon regla = reglaOpt.get();
        int intervalo = (regla.getCantidadComprasRequeridas() != null && regla.getCantidadComprasRequeridas() > 0)
                ? regla.getCantidadComprasRequeridas()
                : 3;

        long pedidosEntregados = pedidoRepository
                .countByUsuarioIdAndEstado(usuario.getId(), EstadoPedido.ENTREGADO);

        if (pedidosEntregados == 0 || pedidosEntregados % intervalo != 0) {
            return List.of();
        }

        String codigo = "FIDELIDAD-" + usuario.getId() + "-" + pedidosEntregados;

        Cupon cupon = cuponRepository.findByCodigo(codigo)
                .orElseGet(() -> crearCupon(codigo, regla));

        return List.of(cupon);
    }

    private Cupon crearCupon(String codigo, ReglaCupon regla) {
        int diasValidez = regla.getDiasValidez() != null ? regla.getDiasValidez() : 30;

        Cupon cupon = Cupon.builder()
                .codigo(codigo)
                .tipoDescuento(regla.getTipoDescuento())
                .valor(regla.getValor())
                .fechaInicio(LocalDate.now())
                .fechaFin(LocalDate.now().plusDays(diasValidez))
                .usoMaximo(1)
                .estado(EstadoCupon.ACTIVO)
                .tipoAsignacion(TipoAsignacionCupon.CANTIDAD_COMPRAS)
                .build();

        try {
            Cupon guardado = cuponRepository.save(cupon);
            log.info(">>> Cupón de fidelidad por compras creado: {} (id={})", codigo, guardado.getId());
            return guardado;
        } catch (DataIntegrityViolationException e) {
            log.warn(">>> Conflicto concurrente al crear {}, recuperando existente", codigo);
            return cuponRepository.findByCodigo(codigo)
                    .orElseThrow(() -> new IllegalStateException(
                            "No se pudo recuperar el cupón concurrente " + codigo, e));
        }
    }
}
