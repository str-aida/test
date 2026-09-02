
package com.Trabajo_Final_Beltran.service.impl;


import com.Trabajo_Final_Beltran.dto.request.CreateProductoRequest;
import com.Trabajo_Final_Beltran.dto.request.UpdateProductoRequest;
import com.Trabajo_Final_Beltran.dto.response.MensajeResponse;
import com.Trabajo_Final_Beltran.dto.response.ProductoResponse;
import com.Trabajo_Final_Beltran.entity.Categoria;
import com.Trabajo_Final_Beltran.entity.Establecimiento;
import com.Trabajo_Final_Beltran.entity.Producto;
import com.Trabajo_Final_Beltran.entity.Usuario;
import com.Trabajo_Final_Beltran.enums.EstadoProducto;
import com.Trabajo_Final_Beltran.enums.Rol;
import com.Trabajo_Final_Beltran.enums.TipoOperacion;
import com.Trabajo_Final_Beltran.exception.BusinessException;
import com.Trabajo_Final_Beltran.mapper.ProductoMapper;
import com.Trabajo_Final_Beltran.repository.CategoriaRepository;
import com.Trabajo_Final_Beltran.repository.ProductoRepository;
import com.Trabajo_Final_Beltran.security.SecurityUtils;
import com.Trabajo_Final_Beltran.service.LogSistemaService;
import com.Trabajo_Final_Beltran.service.ProductoCacheService;
import com.Trabajo_Final_Beltran.service.ProductoService;
import com.Trabajo_Final_Beltran.specification.ProductoSpecification;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.Trabajo_Final_Beltran.service.ImageStorageService;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import lombok.extern.slf4j.Slf4j;
import java.math.RoundingMode;
import com.Trabajo_Final_Beltran.enums.EstadoCategoria;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;

    private final CategoriaRepository categoriaRepository;

    private final LogSistemaService logSistemaService;
    
    private final ProductoCacheService productoCacheService;

    private final ImageStorageService imageStorageService;



    @Override
    @Transactional
    @CacheEvict(value = "productos", allEntries = true)
    public MensajeResponse crearProducto(
        CreateProductoRequest request,
        MultipartFile imagen
    ){


      Usuario usuario =
          SecurityUtils.obtenerUsuarioAutenticado();

        Establecimiento establecimiento =
                usuario.getEstablecimiento();

      Categoria categoria =
          obtenerCategoriaActiva(
              request.getCategoriaId(),
              establecimiento.getId()
          );

        validarCodigoUnico(request);

      validarNombreUnico(
          request,
          establecimiento.getId()
      );

      String imagenUrl = null;

      if (imagen != null && !imagen.isEmpty()) {
        imagenUrl = imageStorageService.guardar(
            imagen,
            establecimiento.getId()
        );
      }

      if (imagenUrl != null) {


        String imagenGuardada = imagenUrl;


        TransactionSynchronizationManager.registerSynchronization(
            new TransactionSynchronization() {

              @Override
              public void afterCompletion(
                  int status
              ) {

                if (
                    status ==
                        TransactionSynchronization.STATUS_ROLLED_BACK
                ) {

                  imageStorageService.eliminar(
                      imagenGuardada
                  );
                }
              }
            }
        );
      }


        Producto producto =
                ProductoMapper.toEntity(request,imagenUrl);

        producto.setCategoria(categoria);

        producto.setEstablecimiento(establecimiento);

        producto.setEstado(EstadoProducto.ACTIVO);


      Producto productoGuardado =
          productoRepository.save(producto);


      logSistemaService.registrarLog(
          "PRODUCTO",
          productoGuardado.getId(),
          productoGuardado.getNombre(),
          "Crear producto",
          "Se creó el producto " + productoGuardado.getNombre(),
          TipoOperacion.INSERT
      );


      return MensajeResponse.builder()
          .mensaje("Producto creado correctamente")
          .build();
    }

    private void validarCodigoUnico(
            CreateProductoRequest request
    ) {

        if (
                request.getCodigo() != null
                &&
                !request.getCodigo().isBlank()
                &&
                productoRepository.existsByCodigo(
                        request.getCodigo()
                )
        ) {

            throw new BusinessException(
                    "El código ya está registrado"
            );
        }
    }

  private void validarNombreUnico(
      CreateProductoRequest request,
      Long establecimientoId
  ) {

    if (
        productoRepository
            .existsByNombreIgnoreCaseAndEstablecimientoId(
                request.getNombre(),
                establecimientoId
            )
    ) {

      throw new BusinessException(
          "Ya existe un producto con ese nombre"
      );
    }
  }
    
    private void validarCodigoUnicoEdicion(
        UpdateProductoRequest request,
        Long productoId
) {

    if (
            request.getCodigo() != null
            &&
            !request.getCodigo().isBlank()
            &&
            productoRepository.existsByCodigoAndIdNot(
                    request.getCodigo(),
                    productoId
            )
    ) {

        throw new BusinessException(
                "El código ya está registrado"
        );
    }
}

  private void validarNombreUnicoEdicion(
      UpdateProductoRequest request,
      Long establecimientoId,
      Long productoId
  ) {

    if (
        productoRepository
            .existsByNombreIgnoreCaseAndEstablecimientoIdAndIdNot(
                request.getNombre(),
                establecimientoId,
                productoId
            )
    ) {

      throw new BusinessException(
          "Ya existe un producto con ese nombre"
      );
    }
  }

    
    @Override
    @Transactional
    @CacheEvict(value = "productos", allEntries = true)
    public ProductoResponse editarProducto(
        Long productoId,
        UpdateProductoRequest request,
        MultipartFile imagen
) {

      Usuario usuario =
          SecurityUtils.obtenerUsuarioAutenticado();

    Establecimiento establecimiento =
            usuario.getEstablecimiento();

    Producto producto =
            productoRepository
                    .findByIdAndEstablecimientoId(
                            productoId,
                            establecimiento.getId()
                    )
                    .orElseThrow(() ->
                            new BusinessException(
                                    "Producto no encontrado"
                            )
                    );

      Categoria categoria =
          obtenerCategoriaActiva(
              request.getCategoriaId(),
              establecimiento.getId()
          );

    validarCodigoUnicoEdicion(
            request,
            productoId
    );

      validarNombreUnicoEdicion(
          request,
          establecimiento.getId(),
          productoId
      );

      String imagenAnterior = producto.getImagenUrl();


      if (
          imagen != null
              &&
              !imagen.isEmpty()
              &&
              Boolean.TRUE.equals(request.getEliminarImagen())
      ) {
        throw new BusinessException(
            "No se puede enviar una imagen nueva y solicitar su eliminación al mismo tiempo"
        );
      }

      String imagenNueva = null;

      String imagenAEliminar = null;

      if (imagen != null && !imagen.isEmpty()) {
        imagenNueva = imageStorageService.guardar(
            imagen,
            establecimiento.getId()
        );
      }

      if (
          imagenAnterior != null
              &&
              (
                  imagenNueva != null
                      ||
                      Boolean.TRUE.equals(request.getEliminarImagen())
              )
      ) {
        imagenAEliminar = imagenAnterior;
      }

      if (imagenNueva != null) {

        String imagenNuevaGuardada = imagenNueva;

        TransactionSynchronizationManager.registerSynchronization(
            new TransactionSynchronization() {

              @Override
              public void afterCompletion(
                  int status
              ) {

                if (
                    status ==
                        TransactionSynchronization.STATUS_ROLLED_BACK
                ) {

                  imageStorageService.eliminar(
                      imagenNuevaGuardada
                  );
                }
              }
            }
        );
      }

      if (imagenAEliminar != null) {

        String imagenAnteriorGuardada = imagenAEliminar;

        TransactionSynchronizationManager.registerSynchronization(
            new TransactionSynchronization() {

              @Override
              public void afterCompletion(
                  int status
              ) {

                if (
                    status ==
                        TransactionSynchronization.STATUS_COMMITTED
                ) {

                  imageStorageService.eliminar(
                      imagenAnteriorGuardada
                  );
                }
              }
            }
        );
      }

      String nombreAnterior = producto.getNombre();

      String descripcionAnterior = producto.getDescripcion();

      String stockAnterior = producto.getStock().toString();

      String precioAnterior =
          producto.getPrecio()
              .setScale(2, RoundingMode.HALF_UP)
              .toString();

      String codigoAnterior = producto.getCodigo();

      String estadoAnterior = producto.getEstado().name();

      String categoriaAnterior = producto.getCategoria().getNombre();

    ProductoMapper.updateProductoFromRequest(
            producto,
            request
    );
      if (imagenNueva != null) {
        producto.setImagenUrl(imagenNueva);
      }

      if (
          imagenNueva == null
              &&
              Boolean.TRUE.equals(request.getEliminarImagen())
      ) {
        producto.setImagenUrl(null);
      }

    producto.setCategoria(categoria);

      Producto productoActualizado =
          productoRepository.save(producto);


      logSistemaService.registrarAuditoria(
          "PRODUCTO",
          productoActualizado.getId(),
          productoActualizado.getNombre(),
          "nombre",
          nombreAnterior,
          productoActualizado.getNombre(),
          "Se modificó el nombre del producto"
      );

      logSistemaService.registrarAuditoria(
          "PRODUCTO",
          productoActualizado.getId(),
          productoActualizado.getNombre(),
          "descripcion",
          descripcionAnterior,
          productoActualizado.getDescripcion(),
          "Se modificó la descripción del producto"
      );

      logSistemaService.registrarAuditoria(
          "PRODUCTO",
          productoActualizado.getId(),
          productoActualizado.getNombre(),
          "precio",
          precioAnterior,
          productoActualizado.getPrecio()
              .setScale(2, RoundingMode.HALF_UP)
              .toString(),
          "Se modificó el precio del producto"
      );

      logSistemaService.registrarAuditoria(
          "PRODUCTO",
          productoActualizado.getId(),
          productoActualizado.getNombre(),
          "stock",
          stockAnterior,
          productoActualizado.getStock().toString(),
          "Se modificó el stock del producto"
      );

      logSistemaService.registrarAuditoria(
          "PRODUCTO",
          productoActualizado.getId(),
          productoActualizado.getNombre(),
          "codigo",
          codigoAnterior,
          productoActualizado.getCodigo(),
          "Se modificó el código del producto"
      );

      logSistemaService.registrarAuditoria(
          "PRODUCTO",
          productoActualizado.getId(),
          productoActualizado.getNombre(),
          "estado",
          estadoAnterior,
          productoActualizado.getEstado().name(),
          "Se modificó el estado del producto"
      );

      logSistemaService.registrarAuditoria(
          "PRODUCTO",
          productoActualizado.getId(),
          productoActualizado.getNombre(),
          "categoria",
          categoriaAnterior,
          productoActualizado.getCategoria().getNombre(),
          "Se modificó la categoría del producto"
      );

      return ProductoMapper.toResponse(productoActualizado);}

  
    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponse> listarProductos(
            Long categoriaId,
            EstadoProducto estado,
            String texto
    ) {

        Usuario usuario =
                SecurityUtils.obtenerUsuarioAutenticado();

        Long establecimientoId =
                usuario.getEstablecimiento().getId();

        boolean esCliente = usuario.getRol() == Rol.CLIENTE;
        EstadoProducto estadoFiltro = esCliente ? EstadoProducto.ACTIVO : estado;

        return productoCacheService.buscarProductosFiltrados(
                establecimientoId,
                categoriaId,
                estadoFiltro,
                texto,
                esCliente
        );
    }

  private Categoria obtenerCategoriaActiva(
      Long categoriaId,
      Long establecimientoId
  ) {

    Categoria categoria =
        categoriaRepository
            .findByIdAndEstablecimientoId(
                categoriaId,
                establecimientoId
            )
            .orElseThrow(() ->
                new BusinessException(
                    "La categoría no existe o no pertenece al establecimiento"
                )
            );

    if (
        categoria.getEstado()
            != EstadoCategoria.ACTIVO
    ) {
      throw new BusinessException(
          "No se puede asignar un producto a una categoría inactiva"
      );
    }

    return categoria;
  }
}