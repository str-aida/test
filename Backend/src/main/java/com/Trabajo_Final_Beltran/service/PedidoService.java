package com.Trabajo_Final_Beltran.service;

import com.Trabajo_Final_Beltran.dto.request.AplicarCuponRequest;
import com.Trabajo_Final_Beltran.dto.request.CreatePedidoRequest;
import com.Trabajo_Final_Beltran.dto.response.PedidoDetalleResponse;
import com.Trabajo_Final_Beltran.dto.response.PedidoResponse;
import com.Trabajo_Final_Beltran.dto.response.ValidacionCuponResponse;
import com.Trabajo_Final_Beltran.enums.EstadoPago;
import com.Trabajo_Final_Beltran.enums.EstadoPedido;
import com.Trabajo_Final_Beltran.enums.MetodoPago;
import com.Trabajo_Final_Beltran.enums.TipoEntrega;
import java.time.LocalDate;
import com.Trabajo_Final_Beltran.dto.response.PageResponse;

public interface PedidoService {
  PageResponse<PedidoResponse> listarPedidos(
      EstadoPedido estado,
      TipoEntrega tipoEntrega,
      EstadoPago estadoPago,
      MetodoPago metodoPago,
      String nombreCliente,
      String numeroPedido,
      LocalDate fechaDesde,
      LocalDate fechaHasta,
      int page,
      int size
  );

  PageResponse<PedidoResponse> listarPedidosEnCurso(
      int page,
      int size
  );

  PedidoDetalleResponse obtenerPedidoPorId(
      Long id
  );

  PedidoDetalleResponse crearPedido(
      CreatePedidoRequest request
  );

  PedidoDetalleResponse aceptarPedido(
      Long id
  );

  PedidoDetalleResponse rechazarPedido(
      Long id
  );

  PedidoDetalleResponse pasarAEnPreparacion(
      Long id
  );

  PedidoDetalleResponse marcarComoListo(
      Long id
  );

  PedidoDetalleResponse marcarComoEntregado(
      Long id
  );

  ValidacionCuponResponse aplicarCupon(
      Long pedidoId,
      AplicarCuponRequest request
  );

}