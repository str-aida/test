package com.Trabajo_Final_Beltran.service;

public interface EmailSenderService {

    void enviarEmail(
            String destino,
            String asunto,
            String cuerpo
    );
}