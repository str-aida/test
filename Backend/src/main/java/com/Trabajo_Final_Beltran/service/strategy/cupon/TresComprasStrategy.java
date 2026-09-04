package com.Trabajo_Final_Beltran.service.strategy.cupon;

import com.Trabajo_Final_Beltran.entity.Cupon;
import com.Trabajo_Final_Beltran.entity.Usuario;
import com.Trabajo_Final_Beltran.enums.EstadoCupon;
import com.Trabajo_Final_Beltran.enums.EstadoPedido;
import com.Trabajo_Final_Beltran.enums.TipoAsignacionCupon;
import com.Trabajo_Final_Beltran.enums.TipoDescuento;
import com.Trabajo_Final_Beltran.repository.CuponRepository;
import com.Trabajo_Final_Beltran.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;

@Slf4j
@Component
@RequiredArgsConstructor
public class TresComprasStrategy implements AsignacionCuponStrategy {

    private static final BigDecimal PORCENTAJE_DESCUENTO = BigDecimal.valueOf(10);
    private static final int DIAS_VALIDEZ = 30;

    @Value("${app.cupones.intervalo-compras:3}")
    private int intervaloCompras;

    private final PedidoRepository pedidoRepository;
    private final CuponRepository cuponRepository;

    @Override
    public List<Cupon> obtenerCupones(Usuario usuario) {
        long pedidosEntregados = pedidoRepository
                .countByUsuarioIdAndEstado(usuario.getId(), EstadoPedido.ENTREGADO);

        if (pedidosEntregados == 0 || pedidosEntregados % intervaloCompras != 0) {
            return List.of();
        }

        String codigo = "FIDELIDAD-" + usuario.getId() + "-" + pedidosEntregados;

        Cupon cupon = cuponRepository.findByCodigo(codigo)
                .orElseGet(() -> crearCupon(codigo));

        return List.of(cupon);
    }

    private Cupon crearCupon(String codigo) {
        Cupon cupon = Cupon.builder()
                .codigo(codigo)
                .tipoDescuento(TipoDescuento.PORCENTAJE)
                .valor(PORCENTAJE_DESCUENTO)
                .fechaInicio(LocalDate.now())
                .fechaFin(LocalDate.now().plusDays(DIAS_VALIDEZ))
                .usoMaximo(1)
                .estado(EstadoCupon.ACTIVO)
                .tipoAsignacion(TipoAsignacionCupon.CANTIDAD_COMPRAS)
                .build();

        try {
            Cupon guardado = cuponRepository.save(cupon);
            log.info(">>> Cupón creado y guardado: {} (id={})", codigo, guardado.getId());
            return guardado;
        } catch (DataIntegrityViolationException e) {
            log.warn(">>> Conflicto concurrente al crear {}, recuperando existente", codigo);
            return cuponRepository.findByCodigo(codigo)
                    .orElseThrow(() -> new IllegalStateException(
                            "No se pudo recuperar el cupón concurrente " + codigo, e));
        }
    }
}