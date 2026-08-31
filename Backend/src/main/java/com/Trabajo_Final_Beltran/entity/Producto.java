
package com.Trabajo_Final_Beltran.entity;

import lombok.*;
import com.Trabajo_Final_Beltran.enums.EstadoProducto;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "producto", uniqueConstraints = {@UniqueConstraint(columnNames = "codigo")
        }
)
public class Producto {
    
        @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "id_establecimiento",
            nullable = false
    )
    private Establecimiento establecimiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "id_categoria",
            nullable = false
    )
    private Categoria categoria;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(length = 500)
    private String descripcion;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoProducto estado;

    @Column(nullable = false)
    private Integer stock;

    @Column(name = "imagen_url", length = 300)
    private String imagenUrl;

    @Column(length = 50, unique = true)
    private String codigo;
    
    @Version
    @Column(nullable = false)
    private Long version;

}
