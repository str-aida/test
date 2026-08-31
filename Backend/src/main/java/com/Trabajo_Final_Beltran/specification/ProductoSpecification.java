package com.Trabajo_Final_Beltran.specification;

import com.Trabajo_Final_Beltran.entity.Producto;
import com.Trabajo_Final_Beltran.enums.EstadoProducto;
import org.springframework.data.jpa.domain.Specification;

public class ProductoSpecification {

    private ProductoSpecification() {
    }

    public static Specification<Producto> establecimientoId(
            Long establecimientoId
    ) {
        return (root, query, cb) ->
                cb.equal(
                        root.get("establecimiento").get("id"),
                        establecimientoId
                );
    }

    public static Specification<Producto> categoriaId(
            Long categoriaId
    ) {
        return (root, query, cb) ->
                cb.equal(
                        root.get("categoria").get("id"),
                        categoriaId
                );
    }

    public static Specification<Producto> estado(
            EstadoProducto estado
    ) {
        return (root, query, cb) ->
                cb.equal(
                        root.get("estado"),
                        estado
                );
    }

    public static Specification<Producto> texto(
            String texto
    ) {
        return (root, query, cb) ->
                cb.or(
                        cb.like(
                                cb.lower(root.get("nombre")),
                                "%" + texto.toLowerCase() + "%"
                        ),
                        cb.like(
                                cb.lower(root.get("descripcion")),
                                "%" + texto.toLowerCase() + "%"
                        )
                );
    }
}