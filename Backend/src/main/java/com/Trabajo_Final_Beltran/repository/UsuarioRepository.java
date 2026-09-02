package com.Trabajo_Final_Beltran.repository;

import com.Trabajo_Final_Beltran.entity.Usuario;
import com.Trabajo_Final_Beltran.enums.Estado;
import com.Trabajo_Final_Beltran.enums.Rol;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository
        extends JpaRepository<Usuario, Long>, JpaSpecificationExecutor<Usuario> {

    @EntityGraph(attributePaths = "establecimiento")
    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByDni(String dni);

    boolean existsByRol(Rol rol);

    long countByRol(Rol rol);

    List<Usuario> findByEstado(Estado estado);

    Optional<Usuario> findByEmailIgnoreCase(String email);

    Optional<Usuario> findByEmailIgnoreCaseAndEstado(String email, Estado estado);

    @Query("""
        SELECT u FROM Usuario u
        WHERE LOWER(CONCAT(u.nombre, ' ', u.apellido)) = LOWER(:nombreCompleto)
        """)
    List<Usuario> findByNombreCompletoIgnoreCase(@Param("nombreCompleto") String nombreCompleto);

    @Query("""
        SELECT u FROM Usuario u
        WHERE LOWER(u.nombre)   LIKE LOWER(CONCAT('%', :q, '%'))
           OR LOWER(u.apellido) LIKE LOWER(CONCAT('%', :q, '%'))
           OR LOWER(u.email)    LIKE LOWER(CONCAT('%', :q, '%'))
        ORDER BY u.nombre ASC, u.apellido ASC
        """)
    List<Usuario> buscarPorTexto(@Param("q") String q, Pageable pageable);
    
    List<Usuario> findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCaseOrEmailContainingIgnoreCase(
        String nombre, String apellido, String email, Pageable pageable);
}