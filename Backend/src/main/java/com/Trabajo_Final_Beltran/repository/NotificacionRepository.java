package com.Trabajo_Final_Beltran.repository;

import com.Trabajo_Final_Beltran.entity.Notificacion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

  List<Notificacion> findAllByUsuarioIdOrderByFechaDesc(
      Long usuarioId
  );

  long countByUsuarioIdAndLeidaFalse(
      Long usuarioId
  );

}