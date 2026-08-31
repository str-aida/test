
package com.Trabajo_Final_Beltran.dto.response;

import com.Trabajo_Final_Beltran.enums.Estado;
import com.Trabajo_Final_Beltran.enums.Rol;
import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class UsuarioPerfilResponse {
        private Long id;

    private String nombre;

    private String apellido;

    private String email;

    private String telefono;

    private String dni;

    private LocalDate fechaNacimiento;
    
    private Rol rol;
    
    private Estado estado;
    
    private DireccionResponse direccion;
}
