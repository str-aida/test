package com.Trabajo_Final_Beltran.specification;

import com.Trabajo_Final_Beltran.entity.Pedido;
import com.Trabajo_Final_Beltran.enums.EstadoPago;
import com.Trabajo_Final_Beltran.enums.EstadoPedido;
import com.Trabajo_Final_Beltran.enums.MetodoPago;
import com.Trabajo_Final_Beltran.enums.TipoEntrega;

import com.Trabajo_Final_Beltran.exception.BusinessException;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class PedidoSpecification {
  private PedidoSpecification() {
  }
  public static Specification<Pedido> establecimientoId(
      Long establecimientoId
  ) {

    return (root, query, cb) ->
        cb.equal(
            root.get("establecimiento")
                .get("id"),
            establecimientoId
        );
  }

  public static Specification<Pedido>
  estado(
      EstadoPedido estado
  ) {

    return (root, query, cb) ->
        cb.equal(
            root.get("estado"),
            estado
        );
  }

  public static Specification<Pedido>
  tipoEntrega(
      TipoEntrega tipoEntrega
  ) {

    return (root, query, cb) ->
        cb.equal(
            root.get("tipoEntrega"),
            tipoEntrega
        );
  }

  public static Specification<Pedido>
  nombreCliente(
      String nombreCliente
  ) {

    return (root, query, cb) ->
        cb.like(
            cb.lower(
                root.get("nombreCliente")
            ),
            "%" +
                nombreCliente.toLowerCase()
                + "%"
        );
  }

  public static Specification<Pedido>
  metodoPago(
      MetodoPago metodoPago
  ) {

    return (root, query, cb) ->
        cb.equal(
            root.join("pago")
                .get("metodo"),
            metodoPago
        );
  }

  public static Specification<Pedido>
  estadoPago(
      EstadoPago estadoPago
  ) {

    return (root, query, cb) ->
        cb.equal(
            root.join("pago")
                .get("estado"),
            estadoPago
        );
  }

  public static Specification<Pedido>
  numeroPedido(
      String numeroPedido
  ) {

    String numeroNormalizado;

    try {

      numeroNormalizado =
          numeroPedido.startsWith("PED-")
              ? numeroPedido
              : String.format(
                  "PED-%08d",
                  Long.parseLong(
                      numeroPedido
                  )
              );

    } catch (
        NumberFormatException ex
    ) {

      throw new BusinessException(
          "Número de pedido inválido"
      );
    }

    return (root, query, cb) ->
        cb.equal(
            root.get(
                "numeroPedido"
            ),
            numeroNormalizado
        );
  }
  public static Specification<Pedido>
  fechaEntre(
      LocalDateTime fechaInicio,
      LocalDateTime fechaFin
  ) {

    return (root, query, cb) ->
        cb.between(
            root.get("fechaHora"),
            fechaInicio,
            fechaFin
        );
  }

  public static Specification<Pedido> pedidosEnCurso() {

    return (root, query, cb) ->
        root.get("estado").in(
            EstadoPedido.PENDIENTE,
            EstadoPedido.ACEPTADO,
            EstadoPedido.EN_PREPARACION,
            EstadoPedido.LISTO
        );
  }

}