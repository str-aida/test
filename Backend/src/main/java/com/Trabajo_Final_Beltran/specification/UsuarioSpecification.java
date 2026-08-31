package com.Trabajo_Final_Beltran.specification;

import com.Trabajo_Final_Beltran.entity.Usuario;
import com.Trabajo_Final_Beltran.enums.Rol;
import org.springframework.data.jpa.domain.Specification;

public class UsuarioSpecification {

    private UsuarioSpecification() {
    }

    public static Specification<Usuario> conRol(Rol rol) {
        return (root, query, cb) ->
                cb.equal(root.get("rol"), rol);
    }

    public static Specification<Usuario> conTexto(String texto) {
        String like = "%" + texto.toLowerCase() + "%";
        return (root, query, cb) ->
                cb.or(
                        cb.like(cb.lower(root.get("nombre")), like),
                        cb.like(cb.lower(root.get("apellido")), like),
                        cb.like(cb.lower(root.get("email")), like)
                );
    }

    public static Specification<Usuario> conRolAdminOEmpleado() {
        return (root, query, cb) ->
                root.get("rol").in(Rol.ADMIN, Rol.EMPLEADO);
    }
}