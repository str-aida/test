package com.Trabajo_Final_Beltran.event;

import com.Trabajo_Final_Beltran.entity.Usuario;
import org.springframework.context.ApplicationEvent;

public class PedidoFinalizadoEvent extends ApplicationEvent {

    private final Usuario usuario;

    public PedidoFinalizadoEvent(Object source, Usuario usuario) {
        super(source);
        this.usuario = usuario;
    }

    public Usuario getUsuario() {
        return usuario;
    }
}