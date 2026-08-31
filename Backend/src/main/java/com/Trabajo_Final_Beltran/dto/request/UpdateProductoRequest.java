package com.Trabajo_Final_Beltran.dto.request;

import com.Trabajo_Final_Beltran.enums.EstadoProducto;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProductoRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(
            min = 3,
            max = 150,
            message = "El nombre debe tener entre 3 y 150 caracteres"
    )
    private String nombre;

    @Size(
            max = 500,
            message = "La descripción no puede superar los 500 caracteres"
    )
    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(
            value = "0.01",
            message = "El precio debe ser mayor a 0"
    )
    private BigDecimal precio;

    @NotNull(message = "La categoría es obligatoria")
    private Long categoriaId;

    @NotNull(message = "El estado es obligatorio")
    private EstadoProducto estado;

    @Min(
            value = 0,
            message = "El stock no puede ser negativo"
    )
    private Integer stock;

   private Boolean eliminarImagen;

    @Size(
            max = 50,
            message = "El código no puede superar los 50 caracteres"
    )
    private String codigo;
}