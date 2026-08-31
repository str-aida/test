
package com.Trabajo_Final_Beltran.entity;

import com.Trabajo_Final_Beltran.enums.DiaSemana;
import com.Trabajo_Final_Beltran.enums.EstadoEstablecimiento;
import com.Trabajo_Final_Beltran.enums.TipoServicio;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import jakarta.persistence.PrePersist;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "establecimiento")
public class Establecimiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_establecimiento")
    private Long id;
    
    @JsonIgnore
    @OneToMany(mappedBy = "establecimiento", fetch = FetchType.LAZY)
    private List<Usuario> usuarios;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_direccion")
  private Direccion direccion;
    
    @Column(nullable = false, length = 200)
    private String nombre;
    
    @Column(name = "razon_social", nullable = false,length = 300)
    private String razonSocial;
    
    @Column (nullable = false, unique = true, length = 20)
    private String cuit;
    
    @Column (nullable = false, length = 150)
    private String email;
    
    @Column (nullable = false, length = 20)
    private String telefono;

    @Column (name = "horario_apertura", nullable = false)
    private LocalTime horarioApertura;
    
    @Column (name = "horario_Cierre", nullable = false)
    private LocalTime horarioCierre;

  @ElementCollection
  @CollectionTable(
      name = "establecimiento_dias_habiles",
      joinColumns = @JoinColumn(name = "id_establecimiento")
  )
  @Enumerated(EnumType.STRING)
  @Column(name = "dia", nullable = false)
  private Set<DiaSemana> diasHabiles;
    
    @Column (length = 300)
    private String descripcion;
    
    @Column (name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_servicio", nullable = false)
    private TipoServicio tipoServicio;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoEstablecimiento estado;
    
    @PrePersist
public void prePersist() {

    this.fechaCreacion = LocalDateTime.now();

    if (this.estado == null) {
        this.estado = EstadoEstablecimiento.ACTIVO;
    }
}

}
