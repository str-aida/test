
package com.Trabajo_Final_Beltran.entity;


import com.Trabajo_Final_Beltran.enums.EstadoPedido;
import com.Trabajo_Final_Beltran.enums.MetodoPago;
import com.Trabajo_Final_Beltran.enums.TipoEntrega;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
@Table(name="pedido")
public class Pedido {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_pedido")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_establecimiento" , nullable = false)
  private Establecimiento establecimiento;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_usuario" , nullable = false)
  private Usuario usuario;
  
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_cupon")
  private Cupon cupon;

  @Column(name = "monto_descuento", precision = 10, scale = 2)
  private BigDecimal montoDescuento;

  @OneToMany(
      mappedBy = "pedido",
      cascade = CascadeType.ALL
  )
  @Builder.Default
  private List<DetallePedido> detalles =
      new ArrayList<>();

  @OneToOne(
      mappedBy = "pedido",
      fetch = FetchType.LAZY
  )
  private Pago pago;

  @Column(name="fecha_hora", nullable = false)
  private LocalDateTime fechaHora;

  @Column(nullable = false , precision = 10, scale = 2)
  private BigDecimal total;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private EstadoPedido estado;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TipoEntrega tipoEntrega;
  
  
  @Enumerated(EnumType.STRING)
  @Column(name = "metodo_pago")
  private MetodoPago metodoPago;
  
  @Version
  @Column(nullable = false)
  private Long version;

  @Column(name="nombre_cliente" , nullable = false , length = 150)
  private String nombreCliente;

  @Column(name="telefono_cliente" ,nullable = false , length = 20)
  private String telefonoCliente;

  @Column(name="direccion_cliente" ,  length = 300)
  private String direccionCliente;

  @Column(
      name = "numero_pedido", unique = true, length = 20)
  private String numeroPedido;
}
