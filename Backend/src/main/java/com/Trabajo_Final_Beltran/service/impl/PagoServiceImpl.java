package com.Trabajo_Final_Beltran.service.impl;

import com.Trabajo_Final_Beltran.dto.response.PagoResponse;
import com.Trabajo_Final_Beltran.dto.response.PasarelaPagoResponse;
import com.Trabajo_Final_Beltran.entity.Pago;
import com.Trabajo_Final_Beltran.entity.Pedido;
import com.Trabajo_Final_Beltran.entity.Usuario;
import com.Trabajo_Final_Beltran.enums.EstadoPago;
import com.Trabajo_Final_Beltran.enums.EstadoPedido;
import com.Trabajo_Final_Beltran.enums.MetodoPago;
import com.Trabajo_Final_Beltran.exception.BusinessException;
import com.Trabajo_Final_Beltran.mapper.PagoMapper;
import com.Trabajo_Final_Beltran.repository.PagoRepository;
import com.Trabajo_Final_Beltran.repository.PedidoRepository;
import com.Trabajo_Final_Beltran.security.SecurityUtils;
import com.Trabajo_Final_Beltran.service.LogSistemaService;
import com.Trabajo_Final_Beltran.service.PagoService;
import com.Trabajo_Final_Beltran.service.PasarelaPagosService;
import io.github.resilience4j.bulkhead.BulkheadFullException;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PagoServiceImpl implements PagoService {

    private final PagoRepository pagoRepository;
    private final PedidoRepository pedidoRepository;
    private final PasarelaPagosService pasarelaPagosService;
    private final LogSistemaService logSistemaService;

    @Override
    @Transactional
    public PagoResponse crearPago(Long pedidoId) {
        Usuario usuario = SecurityUtils.obtenerUsuarioAutenticado();

        Pedido pedido = pedidoRepository
                .findByIdAndUsuarioId(pedidoId, usuario.getId())
                .orElseThrow(() -> new BusinessException("Pedido no encontrado"));

        validarPedidoParaPago(pedido);

        if (pagoRepository.existsByPedidoId(pedido.getId())) {
            throw new BusinessException("El pedido ya posee un pago registrado");
        }

        Pago pago = Pago.builder()
                .pedido(pedido)
                .metodo(pedido.getMetodoPago())
                .monto(calcularMontoFinal(pedido))
                .estado(EstadoPago.PENDIENTE)
                .fechaCreacion(LocalDateTime.now())
                .build();

        Pago pagoGuardado;
        try {
            pagoGuardado = pagoRepository.save(pago);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException("El pedido ya posee un pago registrado");
        }

        if (pedido.getMetodoPago() == MetodoPago.TARJETA) {
            iniciarPagoTarjeta(pagoGuardado);
        }

        return PagoMapper.toResponse(pagoGuardado);
    }

    @Override
    @Transactional
    public PagoResponse aprobarPago(Long pagoId, String referenciaExterna) {
        Pago pago = obtenerPagoDelEstablecimiento(pagoId);

        if (pago.getEstado() != EstadoPago.PENDIENTE) {
            throw new BusinessException("El pago ya fue procesado");
        }

        Pedido pedido = pago.getPedido();
        if (pedido.getEstado() != EstadoPedido.PENDIENTE) {
            throw new BusinessException("El pedido ya no admite pagos");
        }

        // ── Efectivo: no se aprueba por acá, se cobra al entregar ──
        if (pago.getMetodo() == MetodoPago.EFECTIVO) {
            throw new BusinessException(
                "El pago en efectivo se confirma automáticamente al entregar el pedido"
            );
        }

        // ── Tarjeta: la referencia debe coincidir con la guardada al crear ──
        if (referenciaExterna == null || referenciaExterna.isBlank()) {
            throw new BusinessException("La referencia externa es obligatoria");
        }
        if (!referenciaExterna.equals(pago.getReferenciaExterna())) {
            throw new BusinessException("La referencia no corresponde a este pago");
        }
        if (!pasarelaPagosService.verificarPago(referenciaExterna)) {
            throw new BusinessException("El pago no pudo ser verificado por la pasarela");
        }

        pago.setEstado(EstadoPago.APROBADO);
        pago.setFechaActualizacion(LocalDateTime.now());

        Pago actualizado = pagoRepository.save(pago);

        logSistemaService.registrarAuditoria(
                "PAGO", actualizado.getId(), null,
                "estado", "PENDIENTE", "APROBADO", "Pago aprobado"
        );

        return PagoMapper.toResponse(actualizado);
    }

    @Override
    @Transactional
    @Bulkhead(name = "reembolsarPago", fallbackMethod = "reembolsarPagoFallback")
    public PagoResponse reembolsarPago(Long pagoId) {
        Pago pago = obtenerPagoDelEstablecimiento(pagoId);

        if (pago.getEstado() != EstadoPago.APROBADO) {
            throw new BusinessException("Solo se pueden reembolsar pagos aprobados");
        }


        boolean reembolsoExitoso = pasarelaPagosService.reembolsarPago(pago);
        if (!reembolsoExitoso) {
            throw new BusinessException("No fue posible realizar el reembolso");
        }

        pago.setEstado(EstadoPago.REEMBOLSADO);
        pago.setFechaActualizacion(LocalDateTime.now());

        Pago actualizado = pagoRepository.save(pago);

        logSistemaService.registrarAuditoria(
                "PAGO", actualizado.getId(), null,
                "estado", "APROBADO", "REEMBOLSADO", "Pago reembolsado"
        );

        return PagoMapper.toResponse(actualizado);
    }



    private void validarPedidoParaPago(Pedido pedido) {
        if (pedido.getEstado() != EstadoPedido.PENDIENTE) {
            throw new BusinessException("Solo se puede pagar un pedido pendiente");
        }
        if (pedido.getMetodoPago() == null) {
            throw new BusinessException("El pedido no tiene un método de pago definido");
        }
    }

    private BigDecimal calcularMontoFinal(Pedido pedido) {
        BigDecimal monto = pedido.getTotal();
        if (pedido.getMontoDescuento() != null) {
            monto = monto.subtract(pedido.getMontoDescuento());
        }
        if (monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("El monto a pagar debe ser mayor a cero");
        }
        return monto;
    }

    private void iniciarPagoTarjeta(Pago pago) {
        try {
            PasarelaPagoResponse respuesta = pasarelaPagosService.crearPago(pago);
            pago.setReferenciaExterna(respuesta.getReferenciaExterna());
            pago.setUrlPago(respuesta.getUrlPago());
            pagoRepository.save(pago);
        } catch (Exception e) {
            // No dejamos un PENDIENTE sin referencia: lo anulamos y avisamos claro.
            pago.setEstado(EstadoPago.ANULADO);
            pago.setFechaActualizacion(LocalDateTime.now());
            pagoRepository.save(pago);
            log.error("Fallo al iniciar pago en pasarela para pago {}", pago.getId(), e);
            throw new BusinessException("No se pudo iniciar el pago, intentá nuevamente");
        }
    }

    private Pago obtenerPagoDelEstablecimiento(Long pagoId) {
        Usuario usuario = SecurityUtils.obtenerUsuarioAutenticado();
        Long establecimientoId = usuario.getEstablecimiento().getId();
        return pagoRepository
                .findByIdAndPedidoEstablecimientoId(pagoId, establecimientoId)
                .orElseThrow(() -> new BusinessException("Pago no encontrado"));
    }

    private PagoResponse reembolsarPagoFallback(Long pagoId, BulkheadFullException ex) {
        throw new BusinessException(
            "Estamos procesando muchos reembolsos en este momento. Intentá de nuevo en unos segundos."
        );
    }
}