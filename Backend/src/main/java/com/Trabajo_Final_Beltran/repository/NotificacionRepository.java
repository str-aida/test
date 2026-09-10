package com.Trabajo_Final_Beltran.repository;

import com.Trabajo_Final_Beltran.entity.Notificacion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

  List<Notificacion> findTop10ByUsuarioIdOrderByFechaDesc(
      Long usuarioId
  );

  long countByUsuarioIdAndLeidaFalse(
      Long usuarioId
  );

  @Modifying(flushAutomatically = true, clearAutomatically = true)
  @Transactional
  @Query("""
    UPDATE Notificacion n
    SET n.leida = true
    WHERE n.usuario.id = :usuarioId AND n.leida = false
""")
  int marcarTodasComoLeidas(
      @Param("usuarioId") Long usuarioId
  );

  @Modifying(flushAutomatically = true, clearAutomatically = true)
  @Transactional
  @Query("""
    DELETE FROM Notificacion n
    WHERE n.fecha < :fechaLimite
""")
  int eliminarNotificacionesAnterioresA(
      @Param("fechaLimite") LocalDateTime fechaLimite
  );
}