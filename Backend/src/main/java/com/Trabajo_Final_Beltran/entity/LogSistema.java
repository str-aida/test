/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.Trabajo_Final_Beltran.entity;

import com.Trabajo_Final_Beltran.enums.TipoOperacion;
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
import jakarta.persistence.Table;
import java.time.LocalDateTime;
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
@Table(name="log_sistema")
public class LogSistema {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_log")
  private Long id;

  @Column(name = "tabla_afectada", nullable = false)
  private String tablaAfectada;

  @Column(name = "id_registro")
  private Long idRegistro;

  @Column(length = 150)
  private String referencia;

  @Column(nullable = false, length = 50)
  private String accion;

  @Column(name = "campo_modificado" , length = 100)
  private String campoModificado;

  @Column(name = "valor_anterior", length = 500)
  private String valorAnterior;

  @Column(name = "valor_nuevo", length = 500)
  private String valorNuevo;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "id_usuario",
      nullable = false
  )
  private Usuario usuario;

  @Column(nullable = false)
  private LocalDateTime fecha;

  @Column(length = 255, nullable = false)
  private String descripcion;

  @Enumerated(EnumType.STRING)
  @Column(name = "tipo_operacion", nullable = false)
  private TipoOperacion tipoOperacion;

}
