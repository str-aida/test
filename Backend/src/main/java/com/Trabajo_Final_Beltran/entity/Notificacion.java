package com.Trabajo_Final_Beltran.entity;

import com.Trabajo_Final_Beltran.enums.TipoNotificacion;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.Trabajo_Final_Beltran.enums.TipoReferencia;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "notificacion")
public class Notificacion {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_notificacion")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "id_usuario",
      nullable = false
  )
  private Usuario usuario;

  @Enumerated(EnumType.STRING)
  @Column(name = "tipo_referencia")
  private TipoReferencia tipoReferencia;

  @Column(name = "referencia_id")
  private Long referenciaId;

  @Column(nullable = false, length = 100)
  private String titulo;

  @Column(nullable = false, length = 255)
  private String mensaje;

  @Column(nullable = false)
  private Boolean leida;

  @Column(nullable = false)
  private LocalDateTime fecha;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TipoNotificacion tipo;

}