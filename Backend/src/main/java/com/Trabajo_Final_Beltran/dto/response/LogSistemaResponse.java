
package com.Trabajo_Final_Beltran.dto.response;


import com.Trabajo_Final_Beltran.enums.TipoOperacion;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.Trabajo_Final_Beltran.enums.Rol;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LogSistemaResponse {

  private Long id;

  private String tablaAfectada;

  private Long idRegistro;

  private String referencia;

  private String accion;

  private String campoModificado;

  private String valorAnterior;

  private String valorNuevo;

  private String usuario;

  private Rol rol;

  private LocalDateTime fecha;

  private String descripcion;

  private TipoOperacion tipoOperacion;
  
}
