package com.Trabajo_Final_Beltran.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_mensaje")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMensaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mensaje")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_conversacion", nullable = false)
    private ChatConversacion conversacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_remitente", nullable = false)
    private Usuario remitente;

    @Column(name = "es_sistema", nullable = false)
    private boolean esSistema;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenido;

    @Column(name = "fecha_envio", updatable = false)
    private LocalDateTime fechaEnvio;

    @Column(nullable = false)
    private boolean leido;

    @PrePersist
    public void prePersist() {
        this.fechaEnvio = LocalDateTime.now();
    }
}