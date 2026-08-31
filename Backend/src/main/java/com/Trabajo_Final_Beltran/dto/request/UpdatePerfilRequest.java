
package com.Trabajo_Final_Beltran.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import lombok.*;

@Getter
@Setter
public class UpdatePerfilRequest {
    @NotBlank
    private String nombre;

    @NotBlank
    private String apellido;

    @NotBlank
    @Pattern(
        regexp = "^[0-9+\\-\\s]+$",
        message = "El teléfono solo puede contener números, +, espacios o guiones"
    )
    private String telefono;
    @Past
    private LocalDate fechaNacimiento;
    
    @Valid
    private UpdateDireccionRequest direccion;
    
    
    
}
