
package com.Trabajo_Final_Beltran.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import lombok.*;


@Entity
@Table(
    name = "descuento_producto",
    uniqueConstraints = @UniqueConstraint(columnNames = {"id_descuento", "id_producto"})
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DescuentoProducto {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_descuento_producto")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_descuento", nullable = false)
    private Descuento descuento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal porcentaje;
}