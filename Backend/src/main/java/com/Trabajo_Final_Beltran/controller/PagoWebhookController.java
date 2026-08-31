package com.Trabajo_Final_Beltran.controller;

import com.Trabajo_Final_Beltran.service.PagoWebhookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
public class PagoWebhookController {

    private final PagoWebhookService pagoWebhookService;

    @PostMapping("/webhook")
    public ResponseEntity<Void> recibirWebhook(
            @RequestParam(required = false) String topic,
            @RequestParam(required = false) String id,
            @RequestBody(required = false) String payload
    ) {
        System.out.println("=== WEBHOOK RECIBIDO ===");
        System.out.println("TOPIC  -> " + topic);
        System.out.println("ID     -> " + id);
        System.out.println("BODY   -> " + payload);

        try {

            if (topic != null && id != null) {
                pagoWebhookService.procesarNotificacion(topic, id);
                return ResponseEntity.ok().build();
            }

            if (payload != null && payload.contains("\"payment\"")) {
                com.fasterxml.jackson.databind.ObjectMapper mapper =
                        new com.fasterxml.jackson.databind.ObjectMapper();
                com.fasterxml.jackson.databind.JsonNode node =
                        mapper.readTree(payload);
                String type = node.path("type").asText();
                String paymentId = node.path("data").path("id").asText();
                if ("payment".equals(type) && !paymentId.isBlank()) {
                    pagoWebhookService.procesarNotificacion("payment", paymentId);
                }
            }

        } catch (ObjectOptimisticLockingFailureException e) {
            System.out.println("Webhook duplicado ignorado para id: " + id);

        } catch (Exception e) {
            System.out.println("Error procesando webhook: " + e.getMessage());
        }

        return ResponseEntity.ok().build();
    }
}