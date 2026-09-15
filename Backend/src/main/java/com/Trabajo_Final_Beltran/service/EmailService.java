package com.Trabajo_Final_Beltran.service;

import com.Trabajo_Final_Beltran.entity.Cupon;
import com.Trabajo_Final_Beltran.entity.Establecimiento;


public interface EmailService {
    //Es la pasarela de Email
    void enviarEmail(String destino, String asunto, String cuerpo);
    
    //los diferentes casos de uso
    void enviarEmailRecuperacion(String destino, String token, Establecimiento establecimiento);
    void enviarEmailPedidoListo(String destino, String numeroPedido, String nombre, Establecimiento establecimiento);
    void enviarEmailCupon(String destino, String nombre, Cupon cupon, Establecimiento establecimiento, boolean asignacionManual);
}