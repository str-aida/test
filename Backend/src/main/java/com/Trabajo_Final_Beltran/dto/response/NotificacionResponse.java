package com.Trabajo_Final_Beltran.dto.response;

import com.Trabajo_Final_Beltran.enums.TipoNotificacion;
import java.time.LocalDateTime;
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
public class NotificacionResponse {

  private Long id;

  private String titulo;

  private String mensaje;

  private Boolean leida;

  private LocalDateTime fecha;

  private TipoNotificacion tipo;

}
