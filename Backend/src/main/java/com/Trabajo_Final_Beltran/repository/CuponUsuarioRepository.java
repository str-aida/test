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


    long countByCuponId(Long cuponId);

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