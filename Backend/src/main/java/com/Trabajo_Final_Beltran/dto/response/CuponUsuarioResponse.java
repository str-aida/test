package com.Trabajo_Final_Beltran.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CuponUsuarioResponse {

    private Long id;
    private CuponResponse cupon; // anidado: el cliente ve el detalle completo del cupón
    private Boolean usado;
    private LocalDateTime fechaAsignacion;
    private LocalDateTime fechaUso;
}