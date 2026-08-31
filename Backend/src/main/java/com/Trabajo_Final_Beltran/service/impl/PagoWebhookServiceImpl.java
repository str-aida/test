
package com.Trabajo_Final_Beltran.service.impl;

import com.Trabajo_Final_Beltran.repository.PagoRepository;
import com.Trabajo_Final_Beltran.service.PagoWebhookService;
import com.Trabajo_Final_Beltran.service.PasarelaPagosService;
import jakarta.transaction.Transactional;
import lombok.*;
import org.springframework.stereotype.Service;

import com.Trabajo_Final_Beltran.entity.Pago;
import com.Trabajo_Final_Beltran.entity.Pedido;
import com.Trabajo_Final_Beltran.enums.EstadoPago;
import com.Trabajo_Final_Beltran.enums.EstadoPedido;
import com.Trabajo_Final_Beltran.exception.BusinessException;
import com.Trabajo_Final_Beltran.repository.PedidoRepository;
import com.Trabajo_Final_Beltran.service.CanjeCuponService;
import java.time.LocalDateTime;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadFullException;

@Service
@RequiredArgsConstructor
public class PagoWebhookServiceImpl implements PagoWebhookService {

    private final PagoRepository pagoRepository;
    private final PasarelaPagosService pasarelaPagosService;
    private final PedidoRepository pedidoRepository;
    private final CanjeCuponService canjeCuponService;
    
    @Override
    @Transactional
    @Bulkhead(name = "webhookMercadoPago", fallbackMethod = "procesarNotificacionFallback")
    public void procesarNotificacion(
            String topic,
            String id
    ) {

        System.out.println(
                "TOPIC = " + topic
        );

        System.out.println(
                "PAYMENT ID = " + id
        );

        if (
                !"payment".equals(topic)
        ) {
            return;
        }
        
        if (id == null || id.isBlank()) {
            return;
        }
        

        String referenciaExterna =
                pasarelaPagosService
                        .obtenerReferenciaExternaPorPaymentId(
                                id
                        );
        
        String estadoMercadoPago =
        pasarelaPagosService
                .obtenerEstadoPorPaymentId(
                        id
                );

        System.out.println(
                "ESTADO MP = "
                + estadoMercadoPago
        );
        
        if (
            referenciaExterna == null
            || referenciaExterna.isBlank()
        ) {
            throw new BusinessException(
                "No se recibió external_reference"
        );
}
        
        
        System.out.println(
                "REFERENCIA EXTERNA = "
                + referenciaExterna
        );

        Long id_pago =
        Long.valueOf(
                referenciaExterna
        );

        Pago pago =
            pagoRepository
                    .findById(
                            id_pago
                    )
                    .orElseThrow(() ->
                            new BusinessException(
                                    "Pago no encontrado"
                            )
                    );
        
        
        if (
                pago.getEstado()
                == EstadoPago.APROBADO
        )
        {
            return;
        }

        pago.setIdTransaccionExterna(
                id
        );
        
        
        if (!"approved".equalsIgnoreCase(
                estadoMercadoPago
        )) {

            System.out.println(
                    "Pago aún no aprobado"
            );

            return;
        }
        
        pago.setEstado(
                EstadoPago.APROBADO
        );

        pago.setFechaActualizacion(
                LocalDateTime.now()
        );
      
        pagoRepository.save(
                pago
        );
        Pedido pedido = pago.getPedido();
        if (pedido.getCupon() != null) {
            canjeCuponService.canjear(
                    pedido.getCupon(),
                    pedido.getUsuario()
            );
        
    }
        
    }
    private void procesarNotificacionFallback(
            String topic,
            String id,
            BulkheadFullException ex
    ) {
        System.out.println(
            "Bulkhead lleno - notificación de MP descartada temporalmente. "
            + "TOPIC: " + topic + " ID: " + id
        );
        
        
    
}
}
