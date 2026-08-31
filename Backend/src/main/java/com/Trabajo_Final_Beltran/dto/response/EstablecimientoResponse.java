package com.Trabajo_Final_Beltran.dto.response;

import com.Trabajo_Final_Beltran.enums.DiaSemana;
import com.Trabajo_Final_Beltran.enums.EstadoEstablecimiento;
import com.Trabajo_Final_Beltran.enums.TipoServicio;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstablecimientoResponse {

  private Long id;

  private String nombre;

  private String razonSocial;

  private String cuit;

  private String email;

  private String telefono;

  private DireccionResponse direccion;

  private LocalTime horarioApertura;

  private LocalTime horarioCierre;

  private Set<DiaSemana> diasHabiles;

  private String descripcion;

  private TipoServicio tipoServicio;

  private EstadoEstablecimiento estado;

  private LocalDateTime fechaCreacion;
}