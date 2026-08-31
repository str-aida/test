package com.Trabajo_Final_Beltran.dto.response;



import com.Trabajo_Final_Beltran.enums.EstadoPago;
import com.Trabajo_Final_Beltran.enums.EstadoPedido;
import com.Trabajo_Final_Beltran.enums.MetodoPago;
import com.Trabajo_Final_Beltran.enums.TipoEntrega;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PedidoDetalleResponse {

  private Long id;

  private String numeroPedido;

  private EstadoPedido estado;

  private EstadoPago estadoPago;

  private MetodoPago metodoPago;

  private String urlPago;

  private LocalDateTime fechaHora;

  private BigDecimal total;

  private TipoEntrega tipoEntrega;

  private String nombreCliente;

  private String telefonoCliente;

  private String direccionCliente;

  private List<DetallePedidoResponse> detalles;
  
  private String codigoCuponAplicado; 
  
  private BigDecimal montoDescuento;  
  
  private BigDecimal totalConDescuento;

}
