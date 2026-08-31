package com.Trabajo_Final_Beltran.repository;

import com.Trabajo_Final_Beltran.entity.Producto;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository
        extends JpaRepository<Producto, Long>,
                JpaSpecificationExecutor<Producto> {

    boolean existsByCodigo(String codigo);

    boolean existsByCodigoAndIdNot(
            String codigo,
            Long id
    );

    Optional<Producto> findByIdAndEstablecimientoId(
            Long id,
            Long establecimientoId
    );

    @EntityGraph(attributePaths = {"categoria"})
    List<Producto> findAll(Specification<Producto> spec);

  boolean existsByNombreIgnoreCaseAndEstablecimientoId(
      String nombre,
      Long establecimientoId
  );

  boolean existsByNombreIgnoreCaseAndEstablecimientoIdAndIdNot(
      String nombre,
      Long establecimientoId,
      Long id
  );
}