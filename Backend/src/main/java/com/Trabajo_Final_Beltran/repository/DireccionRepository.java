/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.Trabajo_Final_Beltran.repository;

import com.Trabajo_Final_Beltran.entity.Direccion;
import com.Trabajo_Final_Beltran.enums.EstadoDireccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DireccionRepository
    extends JpaRepository<Direccion, Long> {

  List<Direccion>
  findAllByUsuarioIdAndEstado(
      Long usuarioId,
      EstadoDireccion estado
  );

  Optional<Direccion>
  findByIdAndUsuarioIdAndEstado(
      Long direccionId,
      Long usuarioId,
      EstadoDireccion estado
  );

  Optional<Direccion>
  findByUsuarioIdAndEsPrincipalTrue(
      Long usuarioId
  );

  boolean existsByUsuarioId(
      Long usuarioId
  );
}

