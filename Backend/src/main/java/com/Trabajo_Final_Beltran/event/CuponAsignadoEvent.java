package com.Trabajo_Final_Beltran.event;

import com.Trabajo_Final_Beltran.entity.Cupon;
import com.Trabajo_Final_Beltran.entity.Usuario;
import org.springframework.context.ApplicationEvent;

public class CuponAsignadoEvent extends ApplicationEvent {

    private final Usuario usuario;
    private final Cupon cupon;

    public CuponAsignadoEvent(Object source, Usuario usuario, Cupon cupon) {
        super(source);
        this.usuario = usuario;
        this.cupon = cupon;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Cupon getCupon() {
        return cupon;
    }
}