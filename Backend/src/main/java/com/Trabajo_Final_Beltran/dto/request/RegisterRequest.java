package com.Trabajo_Final_Beltran.dto.request;

import com.Trabajo_Final_Beltran.enums.Rol;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 100, message = "El apellido no puede superar los 100 caracteres")
    private String apellido;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no tiene un formato válido")
    @Size(max = 150, message = "El email no puede superar los 150 caracteres")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, max = 20, message = "La contraseña debe tener entre 8 y 255 caracteres")
    @Pattern(
        regexp = "^(?=.*[A-Z])(?=.*\\d).+$",
        message = "La contraseña debe contener al menos una letra mayúscula y un número"
    )
    private String password;

    @Pattern(
        regexp = "^[0-9+\\-\\s]+$",
        message = "El teléfono solo puede contener números."
    )
    private String telefono;

    @NotBlank(message = "El DNI es obligatorio")
    @Size(min = 7, max = 9, message = "El DNI debe tener un mínimo de 7 dígitos")
    @Pattern(regexp = "\\d+", message = "El DNI solo puede contener números")
    private String dni;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento debe ser anterior a hoy")
    private LocalDate fechaNacimiento;
    
    @Valid
    private CreateDireccionRequest direccion;

    private Rol rol;
    
}