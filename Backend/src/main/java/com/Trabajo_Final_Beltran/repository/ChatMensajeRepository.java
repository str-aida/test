package com.Trabajo_Final_Beltran.repository;

import com.Trabajo_Final_Beltran.entity.ChatMensaje;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMensajeRepository extends JpaRepository<ChatMensaje, Long> {

    Page<ChatMensaje> findByConversacionIdOrderByFechaEnvioDesc(Long conversacionId, Pageable pageable);

    long countByConversacionIdAndLeidoFalse(Long conversacionId);

    @Modifying
    @Query("""
        UPDATE ChatMensaje m
        SET m.leido = true
        WHERE m.conversacion.id = :conversacionId
          AND m.remitente.id <> :usuarioId
          AND m.leido = false
        """)
    int marcarLeidosDelOtroParticipante(@Param("conversacionId") Long conversacionId, @Param("usuarioId") Long usuarioId);
}