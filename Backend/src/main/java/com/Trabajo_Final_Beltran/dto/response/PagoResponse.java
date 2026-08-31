
package com.Trabajo_Final_Beltran.dto.response;

import com.Trabajo_Final_Beltran.enums.EstadoPago;
import com.Trabajo_Final_Beltran.enums.MetodoPago;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagoResponse {
    private Long id;

    private Long pedidoId;

    private BigDecimal monto;

    private MetodoPago metodoPago;

    private EstadoPago estado;
    
    private LocalDateTime fechaCreacion;
    
    private String referenciaExterna;
    
    private String urlPago;
    
    private String idTransaccionExterna;

}
