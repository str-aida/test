
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
@Table(name = "recuperacion_password")
public class RecuperacionPassword {
    
   @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_recuperacion")
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @Column(name = "fecha_expiracion")
    private LocalDateTime fechaExpiracion;

    @Column(nullable = false)
    private boolean usado;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @PrePersist
    public void prePersist() {

        this.fechaCreacion = LocalDateTime.now();

        if (!this.usado) {
            this.usado = false;
        }
        
    }
    
}
