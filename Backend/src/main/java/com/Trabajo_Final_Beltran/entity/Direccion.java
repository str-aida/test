
package com.Trabajo_Final_Beltran.entity;

import com.Trabajo_Final_Beltran.enums.EstadoDireccion;
import jakarta.persistence.*;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "direccion")
public class Direccion {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_direccion")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "id_usuario"
  )
  private Usuario usuario;

  @Column(length = 50)
  private String nombre;

  @Column(nullable = false, length = 100)
  private String calle;

  @Column(nullable = false, length = 20)
  private String numero;

  @Column(nullable = false, length = 100)
  private String localidad;

  @Column(length = 20)
  private String piso;

  @Column(length = 20)
  private String departamento;

  @Column(name = "codigo_postal", length = 10)
  private String codigoPostal;

  @Column(length = 200)
  private String referencia;

  @Column(
      name = "es_principal",
      nullable = false
  )
  private Boolean esPrincipal;

  @Column(name = "fecha_creacion")
  private LocalDateTime fechaCreacion;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private EstadoDireccion estado;

  @PrePersist
  public void prePersist() {

    if (this.estado == null) {

      this.estado =
          EstadoDireccion.ACTIVA;
    }
  }
}
