package com.Trabajo_Final_Beltran.mapper;


import com.Trabajo_Final_Beltran.dto.response.DetallePedidoResponse;
import com.Trabajo_Final_Beltran.dto.response.PedidoDetalleResponse;
import com.Trabajo_Final_Beltran.dto.response.PedidoResponse;
import com.Trabajo_Final_Beltran.entity.DetallePedido;
import com.Trabajo_Final_Beltran.entity.Pago;
import com.Trabajo_Final_Beltran.entity.Pedido;
import com.Trabajo_Final_Beltran.enums.EstadoPago;
import com.Trabajo_Final_Beltran.enums.MetodoPago;
import java.math.BigDecimal;
import lombok.Builder;

@Builder
public class PedidoMapper {

  public static PedidoResponse toResponse(
      Pedido pedido
  ) {
    
    Pago pago = pedido.getPago();

    MetodoPago metodoPago = null;
    EstadoPago estadoPago = null;

    metodoPago = pago != null ? pago.getMetodo() : pedido.getMetodoPago();

    estadoPago = pago != null ? pago.getEstado() : null;

    return PedidoResponse.builder()
        .id(pedido.getId())
        .nombreCliente(pedido.getNombreCliente())
        .telefonoCliente(pedido.getTelefonoCliente())
        .fechaHora(pedido.getFechaHora())
        .total(pedido.getTotal())
        .estado(pedido.getEstado())
        .tipoEntrega(pedido.getTipoEntrega())
        .metodoPago(metodoPago)
        .estadoPago(estadoPago)
        .numeroPedido(pedido.getNumeroPedido())
        .build();
  }

  public static DetallePedidoResponse toDetallePedidoResponse
      (DetallePedido detalle
      ){

    return DetallePedidoResponse.builder()
            .productoId(detalle.getProducto().getId())
            .nombreProducto(detalle.getNombreProducto())
            .cantidad(detalle.getCantidad())
            .precioUnitario(detalle.getPrecioUnitario())
            .subtotal(detalle.getSubtotal())
            .build();


  }
public static PedidoDetalleResponse toDetalleResponse(
    Pedido pedido
) {
  Pago pago = pedido.getPago();
  MetodoPago metodoPago = null;
  EstadoPago estadoPago = null;
  String urlPago = null;

  metodoPago = pago != null ? pago.getMetodo() : pedido.getMetodoPago();

  estadoPago = pago != null ? pago.getEstado() : null;

  
  String codigoCuponAplicado = null;
  BigDecimal montoDescuento = pedido.getMontoDescuento();
  BigDecimal totalConDescuento = pedido.getTotal();

  if (pedido.getCupon() != null) {
    codigoCuponAplicado = pedido.getCupon().getCodigo();
  }

  if (montoDescuento != null) {
    totalConDescuento = pedido.getTotal().subtract(montoDescuento);
  }

  return PedidoDetalleResponse.builder()
      .id(pedido.getId())
      .fechaHora(pedido.getFechaHora())
      .total(pedido.getTotal())
      .estado(pedido.getEstado())
      .tipoEntrega(pedido.getTipoEntrega())
      .metodoPago(metodoPago)
      .estadoPago(estadoPago)
      .urlPago(urlPago)
      .nombreCliente(pedido.getNombreCliente())
      .telefonoCliente(pedido.getTelefonoCliente())
      .direccionCliente(pedido.getDireccionCliente())
      .numeroPedido(pedido.getNumeroPedido())
      .codigoCuponAplicado(codigoCuponAplicado)    
      .montoDescuento(montoDescuento)                 
      .totalConDescuento(totalConDescuento)            
      .detalles(
          pedido.getDetalles()
              .stream()
              .map(PedidoMapper::toDetallePedidoResponse)
              .toList()
      )
      .build();
}
}
