package com.Trabajo_Final_Beltran.entity;

import com.Trabajo_Final_Beltran.enums.EstadoCupon;
import com.Trabajo_Final_Beltran.enums.TipoAsignacionCupon;
import com.Trabajo_Final_Beltran.enums.TipoDescuento;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "cupon")
public class Cupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cupon")
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String codigo;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_descuento", nullable = false)
    private TipoDescuento tipoDescuento;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Column(name = "uso_maximo")
    private Integer usoMaximo;

    @Column(name = "usos_actuales")
    @Builder.Default
    private Integer usosActuales = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private EstadoCupon estado = EstadoCupon.ACTIVO;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_asignacion", length = 30)
    private TipoAsignacionCupon tipoAsignacion;

    @Version
    private Long version;
}