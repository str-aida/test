package com.Trabajo_Final_Beltran.event;

import com.Trabajo_Final_Beltran.entity.Cupon;
import com.Trabajo_Final_Beltran.entity.Usuario;
import org.springframework.context.ApplicationEvent;

public class CuponAsignadoEvent extends ApplicationEvent {

    private final Usuario usuario;
    private final Cupon cupon;
    private final boolean asignacionManual;

    public CuponAsignadoEvent(Object source, Usuario usuario, Cupon cupon, boolean asignacionManual) {
        super(source);
        this.usuario = usuario;
        this.cupon = cupon;
        this.asignacionManual = asignacionManual;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Cupon getCupon() {
        return cupon;
    }

    public boolean isAsignacionManual() {
        return asignacionManual;
    }
}