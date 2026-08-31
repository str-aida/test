package com.Trabajo_Final_Beltran.repository;

import com.Trabajo_Final_Beltran.entity.RecuperacionPassword;
import com.Trabajo_Final_Beltran.entity.Usuario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecuperacionPasswordRepository
        extends JpaRepository<RecuperacionPassword, Long> {

    Optional<RecuperacionPassword> findByToken(String token);
    List<RecuperacionPassword> findByUsuarioAndUsadoFalse(Usuario usuario);
}