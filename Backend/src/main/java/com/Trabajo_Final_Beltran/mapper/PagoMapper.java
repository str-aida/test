package com.Trabajo_Final_Beltran.mapper;

import com.Trabajo_Final_Beltran.dto.response.PagoResponse;
import com.Trabajo_Final_Beltran.entity.Pago;

public class PagoMapper {

    public static PagoResponse toResponse(Pago pago) {
        return PagoResponse.builder()
                .id(pago.getId())
                .pedidoId(pago.getPedido().getId())
                .monto(pago.getMonto())
                .metodoPago(pago.getMetodo())
                .estado(pago.getEstado())
                .fechaCreacion(pago.getFechaCreacion())
                .referenciaExterna(pago.getReferenciaExterna())
                .urlPago(pago.getUrlPago())
                .idTransaccionExterna(pago.getIdTransaccionExterna())
                .build();
    }
}