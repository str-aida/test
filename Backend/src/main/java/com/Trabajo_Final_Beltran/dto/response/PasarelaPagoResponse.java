package com.Trabajo_Final_Beltran.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PasarelaPagoResponse {

    private String referenciaExterna;

    private String urlPago;

    private String estado;
}