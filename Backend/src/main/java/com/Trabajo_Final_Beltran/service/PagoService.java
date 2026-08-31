package com.Trabajo_Final_Beltran.service;

import com.Trabajo_Final_Beltran.dto.request.CreatePagoRequest;
import com.Trabajo_Final_Beltran.dto.response.PagoResponse;

public interface PagoService {

    PagoResponse crearPago(Long pedidoId);
    
    PagoResponse aprobarPago(
            Long pagoId,
            String referenciaExterna
    );

    PagoResponse reembolsarPago(
            Long pagoId
    );
}