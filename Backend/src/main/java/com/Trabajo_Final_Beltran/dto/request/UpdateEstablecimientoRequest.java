package com.Trabajo_Final_Beltran.dto.request;


import com.Trabajo_Final_Beltran.enums.DiaSemana;
import com.Trabajo_Final_Beltran.enums.TipoServicio;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class UpdateEstablecimientoRequest {

  @NotBlank(message = "El nombre es obligatorio")
  @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
  private String nombre;

  @NotBlank(message = "La razón social es obligatoria")
  @Size(max = 150, message = "La razón social no puede superar los 150 caracteres")
  private String razonSocial;

  @NotBlank(message = "El email es obligatorio")
  @Email(message = "El email no es válido")
  @Size(max = 100, message = "El email no puede superar los 100 caracteres")
  private String email;

  @NotBlank(message = "El teléfono es obligatorio")
  @Size(max = 20, message = "El teléfono no puede superar los 20 caracteres")
  private String telefono;

  @Valid
  @NotNull(message = "La dirección es obligatoria")
  private UpdateDireccionRequest direccion;

  @NotNull(message = "El horario de apertura es obligatorio")
  private LocalTime horarioApertura;

  @NotNull(message = "El horario de cierre es obligatorio")
  private LocalTime horarioCierre;

  @NotEmpty(message = "Debe seleccionar al menos un día hábil")
  private Set<DiaSemana> diasHabiles;

  @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
  private String descripcion;

  @NotNull(message = "El tipo de servicio es obligatorio")
  private TipoServicio tipoServicio;
}