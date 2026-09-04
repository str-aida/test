package com.Trabajo_Final_Beltran.repository;

import com.Trabajo_Final_Beltran.entity.Cupon;
import com.Trabajo_Final_Beltran.enums.EstadoCupon;
import com.Trabajo_Final_Beltran.enums.TipoAsignacionCupon;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CuponRepository extends JpaRepository<Cupon, Long> {

    Optional<Cupon> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);

    Optional<Cupon> findByCodigoAndEstado(String codigo, EstadoCupon estado);

    boolean existsByCodigoAndFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(
            String codigo,
            LocalDate fechaInicio,
            LocalDate fechaFin
    );
    
    List<Cupon> findByTipoAsignacion(String tipoAsignacion);
    
    List<Cupon> findByTipoAsignacionAndEstado(
        TipoAsignacionCupon tipoAsignacion,
        EstadoCupon estado
);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Cupon c WHERE c.id = :id")
    Optional<Cupon> findByIdForUpdate(@Param("id") Long id);
    
}