/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.Trabajo_Final_Beltran.repository;

import com.Trabajo_Final_Beltran.entity.LogSistema;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;

@Repository
public interface LogSistemaRepository extends JpaRepository<LogSistema,Long>,
    JpaSpecificationExecutor<LogSistema> {


  List<LogSistema> findAllByOrderByFechaDescIdDesc();

  @EntityGraph(attributePaths = "usuario")
  Page<LogSistema> findAll(
      Specification<LogSistema> spec,
      Pageable pageable
  );
}
