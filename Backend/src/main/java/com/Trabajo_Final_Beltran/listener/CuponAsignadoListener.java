package com.Trabajo_Final_Beltran.listener;

import com.Trabajo_Final_Beltran.event.CuponAsignadoEvent;
import com.Trabajo_Final_Beltran.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class CuponAsignadoListener {

    private final EmailService emailService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCuponAsignado(CuponAsignadoEvent event) {
        emailService.enviarEmailCupon(
                event.getUsuario().getEmail(),
                event.getUsuario().getNombre(),
                event.getCupon()
        );
    }
}