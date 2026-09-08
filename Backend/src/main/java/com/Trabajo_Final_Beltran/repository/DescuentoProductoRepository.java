/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.Trabajo_Final_Beltran.repository;

import com.Trabajo_Final_Beltran.entity.DescuentoProducto;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DescuentoProductoRepository extends JpaRepository<DescuentoProducto, Long> {

    @Query("""
        SELECT dp FROM DescuentoProducto dp
        JOIN FETCH dp.producto
        JOIN dp.descuento d
        WHERE d.establecimiento.id = :establecimientoId
          AND d.estado = 'ACTIVO'
          AND CURRENT_DATE BETWEEN d.fechaInicio AND d.fechaFin
        """)
    List<DescuentoProducto> findVigentesPorEstablecimiento(@Param("establecimientoId") Long establecimientoId);

    @Query("""
        SELECT dp FROM DescuentoProducto dp
        JOIN dp.descuento d
        WHERE dp.producto.id = :productoId
          AND d.estado = 'ACTIVO'
          AND d.id <> :descuentoIdExcluir
          AND d.fechaInicio <= :fechaFin
          AND d.fechaFin >= :fechaInicio
        """)
    List<DescuentoProducto> findSolapadosPorProducto(
        @Param("productoId") Long productoId,
        @Param("fechaInicio") LocalDate fechaInicio,
        @Param("fechaFin") LocalDate fechaFin,
        @Param("descuentoIdExcluir") Long descuentoIdExcluir
    );

    void deleteByDescuentoId(Long descuentoId);
}