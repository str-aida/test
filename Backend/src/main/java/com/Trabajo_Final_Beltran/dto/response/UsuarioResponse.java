
package com.Trabajo_Final_Beltran.dto.response;


import com.Trabajo_Final_Beltran.enums.Rol;
import java.time.LocalDate;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponse {

    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private Rol rol;
    private String dni;
    private LocalDate fechaNacimiento;
}