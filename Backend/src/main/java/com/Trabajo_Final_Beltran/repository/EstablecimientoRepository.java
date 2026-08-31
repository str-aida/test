package com.Trabajo_Final_Beltran.repository;

import com.Trabajo_Final_Beltran.entity.Establecimiento;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstablecimientoRepository
        extends JpaRepository<Establecimiento, Long> {

    @EntityGraph(attributePaths = "direccion")
    Optional<Establecimiento> findByCuit(String cuit);

    Optional<Establecimiento> findFirstBy();
    
    boolean existsByCuit(String cuit);

    boolean existsByEmail(String email);

  @EntityGraph(attributePaths = "direccion")
  Optional<Establecimiento> findById(Long id);

  Optional<Establecimiento> findByEmail(String email);

}