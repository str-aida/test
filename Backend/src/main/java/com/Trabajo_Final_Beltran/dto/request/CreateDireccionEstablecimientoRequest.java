package com.Trabajo_Final_Beltran.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
public class CreateDireccionEstablecimientoRequest {

  @Size(
      max = 50,
      message = "El nombre no puede superar los 50 caracteres"
  )
  private String nombre;

  @NotBlank(
      message = "La calle es obligatoria"
  )
  @Size(
      max = 100,
      message = "La calle no puede superar los 100 caracteres"
  )
  private String calle;

  @NotBlank(
      message = "El número es obligatorio"
  )
  @Size(
      max = 20,
      message = "El número no puede superar los 20 caracteres"
  )
  private String numero;

  @NotBlank(
      message = "La localidad es obligatoria"
  )
  @Size(
      max = 100,
      message = "La localidad no puede superar los 100 caracteres"
  )
  private String localidad;

  @Size(
      max = 20,
      message = "El piso no puede superar los 20 caracteres"
  )
  private String piso;

  @Size(
      max = 20,
      message = "El departamento no puede superar los 20 caracteres"
  )
  private String departamento;

  @Size(
      max = 10,
      message = "El código postal no puede superar los 10 caracteres"
  )
  private String codigoPostal;

  @Size(
      max = 200,
      message = "La referencia no puede superar los 200 caracteres"
  )
  private String referencia;

}