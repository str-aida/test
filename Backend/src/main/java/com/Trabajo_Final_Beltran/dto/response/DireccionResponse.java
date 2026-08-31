/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.Trabajo_Final_Beltran.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DireccionResponse {

  private Long id;

  private String nombre;

  private String calle;

  private String numero;

  private String localidad;

  private String piso;

  private String departamento;

  private String codigoPostal;

  private String referencia;

  private Boolean esPrincipal;
}