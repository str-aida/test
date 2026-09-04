package com.Trabajo_Final_Beltran.service.impl;

import com.Trabajo_Final_Beltran.dto.response.NotificacionResponse;
import com.Trabajo_Final_Beltran.entity.Notificacion;
import com.Trabajo_Final_Beltran.entity.Pedido;
import com.Trabajo_Final_Beltran.entity.Usuario;
import com.Trabajo_Final_Beltran.enums.TipoEntrega;
import com.Trabajo_Final_Beltran.enums.TipoNotificacion;
import com.Trabajo_Final_Beltran.enums.TipoReferencia;
import com.Trabajo_Final_Beltran.exception.BusinessException;
import com.Trabajo_Final_Beltran.mapper.NotificacionMapper;
import com.Trabajo_Final_Beltran.repository.NotificacionRepository;
import com.Trabajo_Final_Beltran.security.SecurityUtils;
import com.Trabajo_Final_Beltran.service.EmailService;
import com.Trabajo_Final_Beltran.service.NotificacionService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import com.Trabajo_Final_Beltran.entity.Cupon;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificacionServiceImpl implements NotificacionService {

  private final NotificacionRepository notificacionRepository;

  private final EmailService emailService;



  @Override
  public void notificarPedidoAceptado(Pedido pedido) {

    crearNotificacion(
        pedido.getUsuario(),
        "Pedido aceptado",
        "Tu pedido " + pedido.getNumeroPedido() + " fue aceptado.",
        TipoNotificacion.PEDIDO,
        TipoReferencia.PEDIDO,
        pedido.getId()
    );


  }

  @Override
  public void notificarPedidoRechazado(Pedido pedido) {

    crearNotificacion(
        pedido.getUsuario(),
        "Pedido rechazado",
        "Tu pedido " + pedido.getNumeroPedido()
            + " fue rechazado.",
        TipoNotificacion.PEDIDO,
        TipoReferencia.PEDIDO,
        pedido.getId()
    );

  }

  @Override
  public void notificarPedidoEnPreparacion(Pedido pedido) {

    crearNotificacion(
        pedido.getUsuario(),
        "Pedido en preparación",
        "Tu pedido " + pedido.getNumeroPedido()
            + " está en preparación.",
        TipoNotificacion.PEDIDO,
        TipoReferencia.PEDIDO,
        pedido.getId()
    );

  }

  @Override
  public void notificarPedidoListo(Pedido pedido) {

    crearNotificacion(
        pedido.getUsuario(),
        "Pedido listo",
        "Tu pedido " + pedido.getNumeroPedido()
            + " ya está listo.",
        TipoNotificacion.PEDIDO,
        TipoReferencia.PEDIDO,
        pedido.getId()
    );

    if (
        pedido.getTipoEntrega() == TipoEntrega.RETIRO
    ) {

        emailService.enviarEmailPedidoListo(
            pedido.getUsuario().getEmail(),
            pedido.getNumeroPedido(),
            pedido.getNombreCliente()
        );



    } else {

      // avisar al DeliveryService
    }

  }

  @Override
  public void notificarPedidoEntregado(
      Pedido pedido
  ) {

    crearNotificacion(
        pedido.getUsuario(),
        "Pedido entregado",
        "Tu pedido " + pedido.getNumeroPedido()
            + " fue entregado.",
        TipoNotificacion.PEDIDO,
        TipoReferencia.PEDIDO,
        pedido.getId()
    );
  }

  @Override
  public List<NotificacionResponse> listarMisNotificaciones() {

    Usuario usuario =
        SecurityUtils.obtenerUsuarioAutenticado();

    List<Notificacion> notificaciones =
        notificacionRepository
        .findTop10ByUsuarioIdOrderByFechaDesc(
            usuario.getId()
        );

    return notificaciones.stream()
        .map(NotificacionMapper::toResponse)
        .toList();
  }

  @Override
  public void marcarComoLeida(Long id) {

    Usuario usuario =
        SecurityUtils.obtenerUsuarioAutenticado();

    Notificacion notificacion =
        notificacionRepository
            .findById(id)
            .orElseThrow(() ->
                new BusinessException(
                    "Notificación no encontrada"
                )
            );

    if (
        !notificacion.getUsuario().getId().equals(
            usuario.getId()
        )
    ) {
      throw new BusinessException(
          "No tiene permisos para acceder a esta notificación"
      );
    }

    notificacion.setLeida(true);

    notificacionRepository.save(
        notificacion
    );

  }

  @Override
  public long contarNoLeidas() {

    Usuario usuario =
        SecurityUtils.obtenerUsuarioAutenticado();

    return notificacionRepository
        .countByUsuarioIdAndLeidaFalse(
            usuario.getId()
        );
  }

  private void crearNotificacion(
      Usuario usuario,
      String titulo,
      String mensaje,
      TipoNotificacion tipo,
      TipoReferencia tipoReferencia,
      Long referenciaId
  ) {

    Notificacion notificacion =
        Notificacion.builder()
            .usuario(usuario)
            .titulo(titulo)
            .mensaje(mensaje)
            .leida(false)
            .fecha(LocalDateTime.now())
            .tipo(tipo)
            .tipoReferencia(tipoReferencia)
            .referenciaId(referenciaId)
            .build();

    notificacionRepository.save(
        notificacion
    );
  }

  @Override
  public void eliminarNotificacionesAntiguas() {

    LocalDateTime fechaLimite =
        LocalDateTime.now().minusDays(15);

    notificacionRepository
        .eliminarNotificacionesAnterioresA(
            fechaLimite
        );
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void notificarCuponAsignado(
      Usuario usuario,
      Cupon cupon
  ) {

    crearNotificacion(
        usuario,
        "¡Tenés un nuevo cupón!",
        "Se te asignó el cupón " + cupon.getCodigo() + ".",
        TipoNotificacion.PROMOCION,
        TipoReferencia.CUPON,
        cupon.getId()
    );
  }
}
