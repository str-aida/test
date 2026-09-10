package com.Trabajo_Final_Beltran.repository;

import com.Trabajo_Final_Beltran.entity.ChatConversacion;
import com.Trabajo_Final_Beltran.enums.EstadoConversacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatConversacionRepository extends JpaRepository<ChatConversacion, Long> {

    @EntityGraph(attributePaths = { "establecimiento", "cliente", "pedido", "agente" })
    Optional<ChatConversacion> findById(Long id);

    Optional<ChatConversacion> findByPedidoIdAndEstadoNot(Long pedidoId, EstadoConversacion estado);

    Page<ChatConversacion> findByEstablecimientoIdAndEstado(
            Long establecimientoId, EstadoConversacion estado, Pageable pageable);

    Page<ChatConversacion> findByClienteId(Long clienteId, Pageable pageable);

    @Modifying
    @Query("""
            UPDATE ChatConversacion c
            SET c.agente.id = :agenteId, c.estado = 'EN_ATENCION'
            WHERE c.id = :id AND c.estado = 'ABIERTA'
            """)
    int asignarSiEstaAbierta(@Param("id") Long id, @Param("agenteId") Long agenteId);
}