package com.Trabajo_Final_Beltran.listener;

import com.Trabajo_Final_Beltran.event.CuponAsignadoEvent;
import com.Trabajo_Final_Beltran.service.EmailService;
import com.Trabajo_Final_Beltran.service.NotificacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class CuponAsignadoListener {

    private final EmailService emailService;

    private final NotificacionService notificacionService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCuponAsignado(CuponAsignadoEvent event) {
        emailService.enviarEmailCupon(
                event.getUsuario().getEmail(),
                event.getUsuario().getNombre(),
                event.getCupon()
        );

      notificacionService.notificarCuponAsignado(
          event.getUsuario(),
          event.getCupon()
      );
    }
}