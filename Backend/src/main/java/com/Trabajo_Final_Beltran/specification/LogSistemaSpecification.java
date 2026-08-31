package com.Trabajo_Final_Beltran.specification;

import com.Trabajo_Final_Beltran.entity.LogSistema;
import com.Trabajo_Final_Beltran.enums.Rol;
import org.springframework.data.jpa.domain.Specification;

public class LogSistemaSpecification {

  private LogSistemaSpecification() {
  }

  public static Specification<LogSistema> accion(String accion) {

    String like =
        "%" + accion.toLowerCase() + "%";

    return (root, query, cb) ->
        cb.like(
            cb.lower(root.get("accion")),
            like
        );
  }

  public static Specification<LogSistema> rol(
      Rol rol
  ) {
    return (root, query, cb) ->
        cb.equal(
            root.get("usuario").get("rol"),
            rol
        );
  }

  public static Specification<LogSistema> usuario(String usuario) {

    String like =
        "%" + usuario.toLowerCase() + "%";

    return (root, query, cb) ->
        cb.like(
            cb.lower(
                cb.concat(
                    cb.concat(
                        root.join("usuario").get("nombre"),
                        " "
                    ),
                    root.join("usuario").get("apellido")
                )
            ),
            like
        );
  }
}