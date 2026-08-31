package com.Trabajo_Final_Beltran.repository;


import com.Trabajo_Final_Beltran.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria,Long> {
  boolean existsByNombreAndEstablecimientoId(
      String nombre,
      Long establecimientoId
  );
  List<Categoria> findAllByEstablecimientoIdOrderByIdDesc(
      Long establecimientoId
  );

    Optional<Categoria> findByIdAndEstablecimientoId(
        Long categoriaId,
        Long establecimientoId
    );

}
