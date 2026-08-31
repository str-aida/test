
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
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="detalle_pedido")
public class DetallePedido {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_detalle")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_pedido" , nullable = false)
  private Pedido pedido;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_producto" , nullable = false)
  private Producto producto;

  @Column(nullable = false)
  private Integer cantidad;

  @Column(name="precio_unitario" , nullable = false , precision = 10,
  scale = 2)
  private BigDecimal precioUnitario;

 @Column(nullable = false , precision = 10, scale = 2)
  private BigDecimal subtotal;

 @Column(name="nombre_producto" , nullable = false , length = 150)
  private String nombreProducto;

}
