
package com.Trabajo_Final_Beltran.repository;


import com.Trabajo_Final_Beltran.dto.response.ProductoAnaliticaResponse;
import com.Trabajo_Final_Beltran.entity.DetallePedido;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface DetallePedidoRepository
    extends JpaRepository<DetallePedido, Long> {

  @Query("""
    SELECT new com.Trabajo_Final_Beltran.dto.response.ProductoAnaliticaResponse(
        pr.id,
        dp.nombreProducto,
        SUM(dp.cantidad),
        SUM(dp.subtotal),
        pr.estado
    )
    FROM DetallePedido dp
    JOIN dp.producto pr
    JOIN dp.pedido p
    WHERE p.estado = com.Trabajo_Final_Beltran.enums.EstadoPedido.ENTREGADO
    GROUP BY
        pr.id,
        dp.nombreProducto,
        pr.estado
    ORDER BY SUM(dp.cantidad) DESC
    """)
  List<ProductoAnaliticaResponse> obtenerProductosMasVendidos(
      Pageable pageable
  );

  @Query(
      value = """
        SELECT
            pr.id_producto,
            pr.nombre,
            COALESCE(
                SUM(
                    CASE
                        WHEN p.estado = 'ENTREGADO'
                         AND p.fecha_hora >= :fechaDesde
                        THEN dp.cantidad
                        ELSE 0
                    END
                ),
                0
            ) AS cantidad_vendida,
            COALESCE(
                SUM(
                    CASE
                        WHEN p.estado = 'ENTREGADO'
                         AND p.fecha_hora >= :fechaDesde
                        THEN dp.subtotal
                        ELSE 0
                    END
                ),
                0
            ) AS ingresos_generados,
            pr.estado
        FROM producto pr
        LEFT JOIN detalle_pedido dp
            ON dp.id_producto = pr.id_producto
        LEFT JOIN pedido p
            ON p.id_pedido = dp.id_pedido
        GROUP BY
            pr.id_producto,
            pr.nombre,
            pr.estado
        ORDER BY
            cantidad_vendida ASC,
            ingresos_generados ASC
        LIMIT :limite
        """,
      nativeQuery = true
  )
  List<Object[]> obtenerProductosMenosVendidos(
      @Param("fechaDesde")
      LocalDateTime fechaDesde,

      @Param("limite")
      int limite
  );
}