
package com.Trabajo_Final_Beltran.controller;

import com.Trabajo_Final_Beltran.dto.request.CreateProductoRequest;
import com.Trabajo_Final_Beltran.dto.request.UpdateProductoRequest;
import com.Trabajo_Final_Beltran.dto.response.MensajeResponse;
import com.Trabajo_Final_Beltran.dto.response.ProductoResponse;
import com.Trabajo_Final_Beltran.enums.EstadoProducto;
import com.Trabajo_Final_Beltran.service.ProductoService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/productos")
@RequiredArgsConstructor
public class ProductoController {
     private final ProductoService productoService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<MensajeResponse> crearProducto(
        @Valid
        @RequestPart("producto")
        CreateProductoRequest request,

        @RequestPart(value = "imagen", required = false)
        MultipartFile imagen
    ) {

      return ResponseEntity
          .status(HttpStatus.CREATED)
          .body(
              productoService.crearProducto(
                  request,
                  imagen
              )
          );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponse> editarProducto(
            @PathVariable Long id,

        @Valid
        @RequestPart("producto")
        UpdateProductoRequest request,

        @RequestPart(
            value = "imagen",
            required = false
        )
        MultipartFile imagen
    ) {

        return ResponseEntity.ok(
                productoService.editarProducto(
                        id,
                        request,
                        imagen
                )
        );
    }
    
    @PreAuthorize("hasAnyRole('ADMIN','CLIENTE','EMPLEADO')")
    @GetMapping
    public ResponseEntity<List<ProductoResponse>> listarProductos(
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) EstadoProducto estado,
            @RequestParam(required = false) String texto
    ) {
        return ResponseEntity.ok(
                productoService.listarProductos(categoriaId, estado, texto)
        );
    }    

}
