package com.Trabajo_Final_Beltran.scheduler;

import com.Trabajo_Final_Beltran.service.NotificacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificacionScheduler {

  private final NotificacionService notificacionService;

  @Scheduled(cron = "0 30 6 * * *")
  public void eliminarNotificacionesAntiguas() {

    notificacionService
        .eliminarNotificacionesAntiguas();

    System.out.println(
        "NotificacionScheduler ejecutado"
    );
  }
}