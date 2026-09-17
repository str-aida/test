package com.Trabajo_Final_Beltran.repository;

import com.Trabajo_Final_Beltran.entity.ReglaCupon;
import com.Trabajo_Final_Beltran.enums.TipoAsignacionCupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReglaCuponRepository extends JpaRepository<ReglaCupon, Long> {

    Optional<ReglaCupon> findByTipoAsignacion(TipoAsignacionCupon tipoAsignacion);

    boolean existsByTipoAsignacion(TipoAsignacionCupon tipoAsignacion);
}
