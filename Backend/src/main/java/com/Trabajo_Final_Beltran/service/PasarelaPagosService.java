package com.Trabajo_Final_Beltran.service;

import com.Trabajo_Final_Beltran.dto.response.PasarelaPagoResponse;
import com.Trabajo_Final_Beltran.entity.Pago;


public interface PasarelaPagosService {

    PasarelaPagoResponse crearPago(
            Pago pago
    );

    boolean verificarPago(
            String referencia
    );

    boolean reembolsarPago(
            Pago pago
    );
    
    String obtenerReferenciaExternaPorPaymentId(
        String paymentId
    );
    
    String obtenerEstadoPorPaymentId(
        String paymentId
    );
    
}