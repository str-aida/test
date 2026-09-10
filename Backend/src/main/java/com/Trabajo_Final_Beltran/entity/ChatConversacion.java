package com.Trabajo_Final_Beltran.entity;

import com.Trabajo_Final_Beltran.enums.CerradoPor;
import com.Trabajo_Final_Beltran.enums.EstadoConversacion;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "chat_conversacion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatConversacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_conversacion")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_establecimiento", nullable = false)
    private Establecimiento establecimiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pedido", nullable = false)
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Usuario cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_agente")
    private Usuario agente;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoConversacion estado;

    @Enumerated(EnumType.STRING)
    @Column(name = "cerrado_por")
    private CerradoPor cerradoPor;

    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @Version
    private Long version;

    @OneToMany(mappedBy = "conversacion", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ChatMensaje> mensajes = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
        if (this.estado == null) {
            this.estado = EstadoConversacion.ABIERTA;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }
}