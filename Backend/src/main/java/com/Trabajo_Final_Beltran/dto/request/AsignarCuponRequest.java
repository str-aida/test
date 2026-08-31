package com.Trabajo_Final_Beltran.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AsignarCuponRequest {

    @NotNull(message = "El id del cupón es obligatorio")
    private Long cuponId;

    private Long usuarioId;

    @Email(message = "El email no tiene un formato válido")
    private String email;
}