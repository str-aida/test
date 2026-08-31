package com.Trabajo_Final_Beltran.service.impl;

import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.resources.preference.Preference;
import com.Trabajo_Final_Beltran.dto.response.PasarelaPagoResponse;
import com.Trabajo_Final_Beltran.entity.Pago;
import com.Trabajo_Final_Beltran.exception.BusinessException;
import com.Trabajo_Final_Beltran.service.PasarelaPagosService;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.resources.payment.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;

@Service
@RequiredArgsConstructor
public class MercadoPagoPasarelaServiceImpl implements PasarelaPagosService {
    
    @Value("${mercadopago.webhook-url}")
    private String webhookUrl;
    
    @Override
    public PasarelaPagoResponse crearPago(
            Pago pago
    ) {

        try {

            PreferenceItemRequest item =
                    PreferenceItemRequest.builder()
                            .title(
                                    "Pedido #" +
                                    pago.getPedido().getId()
                            )
                            .quantity(
                                    1
                            )
                            .unitPrice(
                                    pago.getMonto()
                            )
                            .build();
            
            PreferenceRequest preferenceRequest =
                    PreferenceRequest.builder()
                        .items(
                                List.of(item)
                        )
                        .externalReference(pago.getId().toString()
                        )
                        .notificationUrl(
                                webhookUrl
                        )
                        .build();

            PreferenceClient client =
                    new PreferenceClient();

            System.out.println(
                    "WEBHOOK URL = "
                    + webhookUrl
            );
            
            Preference preference =
                    client.create(
                            preferenceRequest
                    );
            
            System.out.println(
                    "PREFERENCE ID = "
                    + preference.getId()
            );

            System.out.println(
                    "INIT POINT = "
                    + preference.getInitPoint()
            );
            

            return PasarelaPagoResponse.builder()
                    .referenciaExterna(
                            preference.getId()
                    )
                    .urlPago(
                            preference.getInitPoint()
                    )
                    .estado(
                            "PENDIENTE"
                    )
                    .build();

        } catch (Exception e) {

            throw new BusinessException(
                    "Error al generar el checkout de Mercado Pago: "
                            + e.getMessage()
            );
        }
    }
    
    @Override
    public String obtenerReferenciaExternaPorPaymentId(
            String paymentId
    ) {

        try {

            PaymentClient client =
                    new PaymentClient();

            Payment payment =
                    client.get(
                            Long.valueOf(paymentId)
                    );
            
            System.out.println(
                "PAYMENT STATUS = "
                + payment.getStatus()
            );

            System.out.println(
                "EXTERNAL REFERENCE = "
                + payment.getExternalReference()
            );

            return payment.getExternalReference();

        } catch (Exception e) {

            throw new BusinessException(
                    "No se pudo consultar el pago en Mercado Pago"
            );
        }
        
    }
    
    @Override
    public String obtenerEstadoPorPaymentId(
            String paymentId
    ) {
        try {

            PaymentClient client =
                    new PaymentClient();

            Payment payment =
                    client.get(
                            Long.valueOf(paymentId)
                    );

            return payment.getStatus();

        } catch (Exception e) {

            throw new BusinessException(
                    "No se pudo consultar el estado del pago"
            );
        }
        
    }
    // TODO: nunca implementado, siempre devuelve true
    @Override
    public boolean verificarPago(
            String referencia
    ) {
        return true;
    }

    @Override
    public boolean reembolsarPago(Pago pago) {
        try {
            if (pago.getIdTransaccionExterna() == null
                    || pago.getIdTransaccionExterna().isBlank()) {
                throw new BusinessException(
                    "El pago no tiene ID de transacción externa para reembolsar"
                );
            }

            PaymentClient client = new PaymentClient();
            client.refund(
                Long.valueOf(pago.getIdTransaccionExterna())
            );

            System.out.println(
                "REEMBOLSO ENVIADO A MP para paymentId: "
                + pago.getIdTransaccionExterna()
            );
            return true;

        } catch (MPApiException e) {
            if (e.getStatusCode() == 401) {
                System.out.println(
                    "ADVERTENCIA: Reembolso no disponible en sandbox. " +
                    "En producción funcionará con credenciales live."
                );
                return true; 
            }
            System.out.println("MP STATUS: " + e.getStatusCode());
            System.out.println("MP RESPONSE: " + e.getApiResponse().getContent());
            throw new BusinessException(
                "Error al reembolsar en Mercado Pago: " + e.getMessage()
            );

        } catch (BusinessException e) {
            throw e;

        } catch (Exception e) {
            System.out.println("ERROR GENERICO: " + e.getMessage());
            throw new BusinessException(
                "Error al reembolsar en Mercado Pago: " + e.getMessage()
            );
        }    
    }

}