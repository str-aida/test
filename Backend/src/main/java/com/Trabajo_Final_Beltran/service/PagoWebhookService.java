package com.Trabajo_Final_Beltran.service;

public interface PagoWebhookService {

    void procesarNotificacion(
            String topic,
            String id
    );
}