package com.Trabajo_Final_Beltran.entity;

import com.Trabajo_Final_Beltran.enums.EstadoPago;
import com.Trabajo_Final_Beltran.enums.MetodoPago;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pago")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "id_pedido",
            nullable = false,
            unique = true
    )
    private Pedido pedido;

    @Column(
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal monto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MetodoPago metodo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPago estado;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @Column(
            name = "referencia_externa",
            length = 100
    )
    private String referenciaExterna;
    
    private String urlPago;
    
    @Column(
        name = "id_transaccion_externa",
        length = 150
)
    private String idTransaccionExterna;
    
    @Version
    @Column(nullable = false)
    private Long version;
}