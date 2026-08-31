/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.Trabajo_Final_Beltran.mapper;

import com.Trabajo_Final_Beltran.dto.response.NotificacionResponse;
import com.Trabajo_Final_Beltran.entity.Notificacion;

public class NotificacionMapper {

  public static NotificacionResponse toResponse(
      Notificacion notificacion
  ) {

    return NotificacionResponse.builder()
        .id(
            notificacion.getId()
        )
        .titulo(
            notificacion.getTitulo()
        )
        .mensaje(
            notificacion.getMensaje()
        )
        .leida(
            notificacion.getLeida()
        )
        .fecha(
            notificacion.getFecha()
        )
        .tipo(
            notificacion.getTipo()
        )
        .build();
  }

}