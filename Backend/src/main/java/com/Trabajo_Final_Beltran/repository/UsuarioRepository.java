package com.Trabajo_Final_Beltran.repository;

import com.Trabajo_Final_Beltran.entity.Usuario;
import com.Trabajo_Final_Beltran.enums.Estado;
import com.Trabajo_Final_Beltran.enums.Rol;
import java.util.List;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
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
  
    public List<Usuario> findByEstado(Estado estado);
    
    Optional<Usuario> findByEmailIgnoreCase(String email);
    
    Optional<Usuario> findByEmailIgnoreCaseAndEstado(String email, Estado estado);
    
    
}