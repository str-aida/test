/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.Trabajo_Final_Beltran.repository;

import com.Trabajo_Final_Beltran.entity.Descuento;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;


public interface DescuentoRepository extends JpaRepository<Descuento, Long> {
    Optional<Descuento> findByIdAndEstablecimientoId(Long id, Long establecimientoId);
    List<Descuento> findAllByEstablecimientoId(Long establecimientoId);
}