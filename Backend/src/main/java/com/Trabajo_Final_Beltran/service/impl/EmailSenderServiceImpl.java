package com.Trabajo_Final_Beltran.service.impl;

import com.Trabajo_Final_Beltran.service.EmailSenderService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailSenderServiceImpl
        implements EmailSenderService {

    private final JavaMailSender mailSender;

    @Override
    @Async("emailExecutor")
    public void enviarEmail(
            String destino,
            String asunto,
            String cuerpo
    ) {

        try {

            SimpleMailMessage mensaje =
                    new SimpleMailMessage();

            mensaje.setTo(destino);
            mensaje.setSubject(asunto);
            mensaje.setText(cuerpo);

            mailSender.send(mensaje);

            System.out.println(
                    "Email enviado a "
                            + destino
            );

        } catch (Exception e) {

            System.out.println(
                    "Error enviando email: "
                            + e.getMessage()
            );
        }
    }
}