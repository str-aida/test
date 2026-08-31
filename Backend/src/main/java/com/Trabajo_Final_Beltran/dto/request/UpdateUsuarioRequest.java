/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.Trabajo_Final_Beltran.dto.request;

import com.Trabajo_Final_Beltran.enums.Estado;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUsuarioRequest {
    private String nombre;
    private String apellido;
    private String telefono;
    private String email;
    @NotNull(message = "El estado es obligatorio")
    private Estado estado;
}
