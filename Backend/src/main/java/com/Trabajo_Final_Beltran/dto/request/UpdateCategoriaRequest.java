package com.Trabajo_Final_Beltran.dto.request;


import com.Trabajo_Final_Beltran.enums.EstadoCategoria;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class UpdateCategoriaRequest {


  @NotBlank(message = "El nombre es obligatorio")
  @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
  private String nombre;

  @Size(max = 200, message = "La descripción no puede superar los 200 caracteres")
  private String descripcion;

  @NotNull(message = "El estado es obligatorio")
  private EstadoCategoria estado;


}
