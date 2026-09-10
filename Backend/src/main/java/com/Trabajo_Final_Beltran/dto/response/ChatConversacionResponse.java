package com.Trabajo_Final_Beltran.dto.response;

import com.Trabajo_Final_Beltran.enums.CerradoPor;
import com.Trabajo_Final_Beltran.enums.EstadoConversacion;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatConversacionResponse {
    private Long id;
    private Long pedidoId;
    private String numeroPedido;
    private Long clienteId;
    private String nombreCliente;
    private Long agenteId;
    private String nombreAgente;
    private EstadoConversacion estado;
    private CerradoPor cerradoPor;
    private LocalDateTime fechaCreacion;
    private long mensajesNoLeidos;
}