package com.Trabajo_Final_Beltran.entity;

import com.Trabajo_Final_Beltran.enums.TipoAsignacionCupon;
import com.Trabajo_Final_Beltran.enums.TipoDescuento;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "regla_cupon")
public class ReglaCupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_regla")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_asignacion", nullable = false, unique = true, length = 30)
    private TipoAsignacionCupon tipoAsignacion;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_descuento", nullable = false, length = 20)
    private TipoDescuento tipoDescuento;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Column(name = "dias_validez", nullable = false)
    @Builder.Default
    private Integer diasValidez = 30;

    @Column(name = "cantidad_compras_requeridas")
    private Integer cantidadComprasRequeridas;

    @Column(length = 255)
    private String descripcion;
}
