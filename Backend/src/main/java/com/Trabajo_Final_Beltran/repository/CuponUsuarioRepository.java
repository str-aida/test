package com.Trabajo_Final_Beltran.repository;

import com.Trabajo_Final_Beltran.entity.CuponUsuario;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface CuponUsuarioRepository extends JpaRepository<CuponUsuario, Long> {

    @EntityGraph(attributePaths = {"cupon"})
    List<CuponUsuario> findAllByUsuarioId(Long usuarioId);

    @EntityGraph(attributePaths = {"cupon"})
    List<CuponUsuario> findAllByUsuarioIdAndUsado(Long usuarioId, Boolean usado);

    Optional<CuponUsuario> findByUsuarioIdAndCuponId(Long usuarioId, Long cuponId);

    Optional<CuponUsuario> findByUsuarioIdAndCuponIdAndUsado(
        Long usuarioId,
        Long cuponId,
        Boolean usado
    );
    
    boolean existsByUsuarioIdAndCuponId(Long usuarioId, Long cuponId);

    boolean existsByUsuarioIdAndCuponIdAndUsadoFalse(Long usuarioId, Long cuponId);

    /**
     * Cantidad total de asignaciones de un cupón (usadas + pendientes + reservadas).
     * Se usa para validar el cupo disponible al asignar.
     */
    long countByCuponId(Long cuponId);

    /**
     * Devuelve el conteo de asignaciones agrupado por cupón para una lista de IDs.
     * Evita el problema N+1 al listar cupones: una sola consulta en lugar de una por cupón.
     * Cada elemento del resultado es un Object[] con [cuponId (Long), count (Long)].
     */
    @Query("""
        SELECT cu.cupon.id, COUNT(cu)
          FROM CuponUsuario cu
         WHERE cu.cupon.id IN :cuponIds
         GROUP BY cu.cupon.id
    """)
    List<Object[]> contarAsignacionesPorCupones(@Param("cuponIds") List<Long> cuponIds);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
        UPDATE CuponUsuario cu
           SET cu.reservado = true,
               cu.pedidoReservaId = :pedidoId
         WHERE cu.usuario.id = :usuarioId
           AND cu.cupon.id = :cuponId
           AND cu.reservado = false
           AND cu.usado = false
    """)
    int marcarReservado(@Param("usuarioId") Long usuarioId,
                        @Param("cuponId") Long cuponId,
                        @Param("pedidoId") Long pedidoId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
        UPDATE CuponUsuario cu
           SET cu.reservado = false,
               cu.pedidoReservaId = null
         WHERE cu.usuario.id = :usuarioId
           AND cu.cupon.id = :cuponId
    """)
    int liberarReserva(@Param("usuarioId") Long usuarioId,
                       @Param("cuponId") Long cuponId);

}