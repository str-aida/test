package com.Trabajo_Final_Beltran.repository;

import com.Trabajo_Final_Beltran.entity.Notificacion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
    DELETE FROM Notificacion n
    WHERE n.fecha < :fechaLimite
""")
  int eliminarNotificacionesAnterioresA(
      @Param("fechaLimite") LocalDateTime fechaLimite
  );
}