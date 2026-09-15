package com.Trabajo_Final_Beltran.service.impl;

import com.Trabajo_Final_Beltran.service.EmailSenderService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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

            MimeMessage mensaje = mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(
                            mensaje,
                            true,
                            "UTF-8"
                    );

            helper.setTo(destino);
            helper.setSubject(asunto);

            helper.setText(cuerpo, true);

            mailSender.send(mensaje);

            System.out.println(
                    "Email enviado a " + destino
            );

        } catch (Exception e) {

            System.out.println(
                    "Error enviando email: "
                            + e.getMessage()
            );
        }
    }
}