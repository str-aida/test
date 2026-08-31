package com.Trabajo_Final_Beltran.repository;

import com.Trabajo_Final_Beltran.entity.Pago;
import com.Trabajo_Final_Beltran.enums.EstadoPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PagoRepository
        extends JpaRepository<Pago, Long> {

    Optional<Pago> findByPedidoId(
            Long pedidoId
    );
    
    Optional<Pago> findByIdAndPedidoEstablecimientoId(Long id, Long establecimientoId);

    boolean existsByPedidoId(
            Long pedidoId
    );

    List<Pago> findAllByEstado(
            EstadoPago estado
    );
    
    Optional<Pago> findByReferenciaExterna(
        String referenciaExterna
    );
}