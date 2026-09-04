package com.Trabajo_Final_Beltran.service;

import com.Trabajo_Final_Beltran.dto.response.NotificacionResponse;
import com.Trabajo_Final_Beltran.entity.Pedido;
import java.util.List;
import com.Trabajo_Final_Beltran.entity.Cupon;
import com.Trabajo_Final_Beltran.entity.Usuario;

public interface NotificacionService {

  void notificarPedidoAceptado(Pedido pedido);

  void notificarPedidoRechazado(Pedido pedido);

  void notificarPedidoEnPreparacion(Pedido pedido);

  void notificarPedidoListo(Pedido pedido);

  void notificarPedidoEntregado(Pedido pedido);

  List<NotificacionResponse> listarMisNotificaciones();

  void marcarComoLeida(Long id);

  long contarNoLeidas();

  void eliminarNotificacionesAntiguas();

  void notificarCuponAsignado(Usuario usuario, Cupon cupon);

}