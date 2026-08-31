package com.Trabajo_Final_Beltran.repository;

import com.Trabajo_Final_Beltran.dto.response.ClienteAnaliticaResponse;
import com.Trabajo_Final_Beltran.dto.response.EstadoPedidoResponse;
import com.Trabajo_Final_Beltran.dto.response.VentaMetodoPagoResponse;
import com.Trabajo_Final_Beltran.dto.response.VentaTipoEntregaResponse;
import com.Trabajo_Final_Beltran.entity.Pedido;
import com.Trabajo_Final_Beltran.enums.EstadoPedido;
import io.lettuce.core.dynamic.annotation.Param;
import java.time.LocalDateTime;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long>,
    JpaSpecificationExecutor<Pedido> {

    
    @EntityGraph(attributePaths = {"pago", "detalles", "detalles.producto", "usuario","cupon"})
    Optional<Pedido> findByIdAndEstablecimientoId(
        Long pedidoId,
        Long establecimientoId
    );

    @EntityGraph(attributePaths = {"pago", "detalles", "detalles.producto"})
    List<Pedido> findAllByUsuarioIdOrderByFechaHoraDesc(
        Long usuarioId
    );

    @EntityGraph(attributePaths = {"pago", "detalles", "detalles.producto", "usuario","cupon"})
    Optional<Pedido> findByIdAndUsuarioId(
        Long pedidoId,
        Long usuarioId
    );
    
    long countByUsuarioIdAndEstado (
        Long usuarioId,
        EstadoPedido estado
    );


  @EntityGraph(attributePaths = {"pago"})
  Page<Pedido> findAll(
      Specification<Pedido> spec,
      Pageable pageable
  );

  @EntityGraph(attributePaths = {"pago"})
  Page<Pedido> findAllByUsuarioId(
      Long usuarioId,
      Pageable pageable
  );

    //consultas para analytic y metrica

  long countByEstado(
      EstadoPedido estado
  );

  //Porque si todavía no hay pedidos entregados, SUM devuelve null entonces usamos:
  //COLESCE

  @Query("""
    SELECT COALESCE(SUM(p.total), 0)
    FROM Pedido p
    WHERE p.estado = com.Trabajo_Final_Beltran.enums.EstadoPedido.ENTREGADO
    """)
  BigDecimal obtenerVentasTotales();

  @Query("""
    SELECT COALESCE(AVG(p.total), 0)
    FROM Pedido p
    WHERE p.estado = com.Trabajo_Final_Beltran.enums.EstadoPedido.ENTREGADO
    """)
  BigDecimal obtenerTicketPromedio();

  @Query("""
    SELECT new com.Trabajo_Final_Beltran.dto.response.ClienteAnaliticaResponse(
        u.id,
        CONCAT(u.nombre, ' ', u.apellido),
        COUNT(p),
        SUM(p.total),
        MAX(p.fechaHora)
    )
    FROM Pedido p
    JOIN p.usuario u
    WHERE p.estado = com.Trabajo_Final_Beltran.enums.EstadoPedido.ENTREGADO
    GROUP BY u.id, u.nombre, u.apellido
    ORDER BY SUM(p.total) DESC
    """)
  List<ClienteAnaliticaResponse> obtenerMejoresClientes(
      Pageable pageable
  );

  @Query("""
    SELECT new com.Trabajo_Final_Beltran.dto.response.EstadoPedidoResponse(
        p.estado,
        COUNT(p)
    )
    FROM Pedido p
    GROUP BY p.estado
    ORDER BY p.estado
    """)
  List<EstadoPedidoResponse> obtenerPedidosPorEstado();

  @Query(
      value = """
        SELECT
            DATE(p.fecha_hora) AS periodo,
            SUM(p.total) AS ventas
        FROM pedido p
        WHERE p.estado = 'ENTREGADO'
          AND p.fecha_hora >= :fechaDesde
        GROUP BY DATE(p.fecha_hora)
        ORDER BY DATE(p.fecha_hora)
        """,
      nativeQuery = true
  )
  List<Object[]> obtenerVentasPorPeriodo(
      @Param("fechaDesde")
      LocalDateTime fechaDesde
  );

  @Query(
      value = """
        SELECT
            u.id_usuario,
            CONCAT(u.nombre, ' ', u.apellido) AS nombre_completo,
            u.email,
            ultimo.fecha_hora AS ultima_compra,
            ultimo.total AS monto_ultima_compra,
            SUM(p.total) AS total_gastado,
            COUNT(p.id_pedido) AS cantidad_pedidos
        FROM usuario u
        JOIN pedido p
            ON p.id_usuario = u.id_usuario
        JOIN pedido ultimo
            ON ultimo.id_pedido = (
                SELECT p2.id_pedido
                FROM pedido p2
                WHERE p2.id_usuario = u.id_usuario
                  AND p2.estado = 'ENTREGADO'
                ORDER BY p2.fecha_hora DESC, p2.id_pedido DESC
                LIMIT 1
            )
        WHERE u.rol = 'CLIENTE'
          AND p.estado = 'ENTREGADO'
        GROUP BY
            u.id_usuario,
            u.nombre,
            u.apellido,
            u.email,
            ultimo.fecha_hora,
            ultimo.total
        HAVING ultimo.fecha_hora < :fechaLimite
        ORDER BY ultimo.fecha_hora ASC
        """,
      nativeQuery = true
  )
  List<Object[]> obtenerClientesInactivos(
      @Param("fechaLimite")
      LocalDateTime fechaLimite
  );

  @Query("""
    SELECT new com.Trabajo_Final_Beltran.dto.response.VentaMetodoPagoResponse(
        p.pago.metodo,
        COUNT(p),
        SUM(p.total)
    )
    FROM Pedido p
    WHERE p.estado = com.Trabajo_Final_Beltran.enums.EstadoPedido.ENTREGADO
    GROUP BY p.pago.metodo
    ORDER BY SUM(p.total) DESC
    """)
  List<VentaMetodoPagoResponse> obtenerVentasPorMetodoPago();

  @Query("""
    SELECT new com.Trabajo_Final_Beltran.dto.response.VentaTipoEntregaResponse(
        p.tipoEntrega,
        COUNT(p),
        SUM(p.total)
    )
    FROM Pedido p
    WHERE p.estado = com.Trabajo_Final_Beltran.enums.EstadoPedido.ENTREGADO
    GROUP BY p.tipoEntrega
    ORDER BY SUM(p.total) DESC
""")
  List<VentaTipoEntregaResponse> obtenerVentasPorTipoEntrega();
}