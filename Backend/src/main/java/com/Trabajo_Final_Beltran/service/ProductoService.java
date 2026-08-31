
package com.Trabajo_Final_Beltran.service;

import com.Trabajo_Final_Beltran.dto.request.CreateProductoRequest;
import com.Trabajo_Final_Beltran.dto.request.UpdateProductoRequest;
import com.Trabajo_Final_Beltran.dto.response.MensajeResponse;
import com.Trabajo_Final_Beltran.dto.response.ProductoResponse;
import com.Trabajo_Final_Beltran.enums.EstadoProducto;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface ProductoService {

   MensajeResponse crearProducto(
       CreateProductoRequest request,
       MultipartFile imagen
  );

    ProductoResponse editarProducto(
            Long productoId,
            UpdateProductoRequest request,
            MultipartFile imagen
    );


  List<ProductoResponse> listarProductos(
        Long categoriaId,
        EstadoProducto estado,
        String texto
    );
}