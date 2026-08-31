package com.Trabajo_Final_Beltran.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "cupon_usuario")
public class CuponUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cupon_usuario") 
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cupon", nullable = false)
    private Cupon cupon;

    @Column(nullable = false)
    @Builder.Default
    private Boolean usado = false;

    @Column(name = "fecha_asignacion", updatable = false)
    private LocalDateTime fechaAsignacion;

    @Column(name = "fecha_uso")
    private LocalDateTime fechaUso;
    
    @Column(name = "reservado", nullable = false)
    @Builder.Default
    private Boolean reservado = false;

    @Column(name = "pedido_reserva_id")
    private Long pedidoReservaId;

    @PrePersist
    public void prePersist() {
        this.fechaAsignacion = LocalDateTime.now();
        if (this.usado == null) {
            this.usado = false;
        }
    }
}