package com.Trabajo_Final_Beltran.dto.response;

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
public class SetupResponse {

    private String mensaje;

    private Long idEstablecimiento;
}