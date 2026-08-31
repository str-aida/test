package com.Trabajo_Final_Beltran.event.listener;

import com.Trabajo_Final_Beltran.event.PedidoFinalizadoEvent;
import com.Trabajo_Final_Beltran.service.AsignacionCuponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class PedidoFinalizadoListener {

    private final AsignacionCuponService asignacionCuponService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPedidoFinalizado(PedidoFinalizadoEvent event) {
        try {
            asignacionCuponService.asignarCupones(event.getUsuario());
        } catch (Exception e) {
            // Falló la fidelización: el pedido YA está entregado y commiteado.
            // No reventamos la respuesta, pero esto tiene que saltar a monitoring.
            log.error("[REQUIERE ACCION] No se asignaron cupones al usuario {} — pedido entregado sin beneficio",
                    event.getUsuario().getId(), e);
            // producción: métrica + alerta (Micrometer, Sentry, Slack...)
        }
    }
}