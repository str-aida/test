package com.Trabajo_Final_Beltran.listener;

import com.Trabajo_Final_Beltran.event.ChatEstadoEvent;
import com.Trabajo_Final_Beltran.event.ChatLecturaEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ChatEstadoListener {

    private final SimpMessagingTemplate messagingTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onChatEstado(ChatEstadoEvent evento) {
        messagingTemplate.convertAndSend(
                "/topic/chat/" + evento.conversacionId() + "/estado",
                evento.respuesta());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onChatLectura(ChatLecturaEvent evento) {
        messagingTemplate.convertAndSend(
                "/topic/chat/" + evento.conversacionId() + "/leidos",
                evento.lectura());
    }
}