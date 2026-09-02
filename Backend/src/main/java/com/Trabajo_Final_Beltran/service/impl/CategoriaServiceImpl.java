package com.Trabajo_Final_Beltran.service.impl;

import com.Trabajo_Final_Beltran.dto.request.CreateCategoriaRequest;
import com.Trabajo_Final_Beltran.dto.request.UpdateCategoriaRequest;
import com.Trabajo_Final_Beltran.dto.response.CategoriaResponse;
import com.Trabajo_Final_Beltran.dto.response.MensajeResponse;
import com.Trabajo_Final_Beltran.entity.Categoria;
import com.Trabajo_Final_Beltran.entity.Establecimiento;
import com.Trabajo_Final_Beltran.enums.EstadoCategoria;
import com.Trabajo_Final_Beltran.enums.TipoOperacion;
import com.Trabajo_Final_Beltran.repository.CategoriaRepository;
import com.Trabajo_Final_Beltran.repository.EstablecimientoRepository;
import com.Trabajo_Final_Beltran.service.CategoriaService;
import com.Trabajo_Final_Beltran.service.LogSistemaService;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.Trabajo_Final_Beltran.mapper.CategoriaMapper;
import com.Trabajo_Final_Beltran.exception.BusinessException;
import java.util.List;
import com.Trabajo_Final_Beltran.entity.Usuario;
import com.Trabajo_Final_Beltran.security.SecurityUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;




@Service
@RequiredArgsConstructor
public class CategoriaServiceImpl implements CategoriaService {

  private final CategoriaRepository categoriaRepository;
  private final EstablecimientoRepository establecimientoRepository;
  private final LogSistemaService logSistemaService;
  private final CacheManager cacheManager;



  private void validarCategoriaDelEstablecimiento(Categoria categoria) {

    Usuario usuario =
        SecurityUtils
            .obtenerUsuarioAutenticado();

    Long establecimientoUsuario =
        usuario.getEstablecimiento().getId();

    Long establecimientoCategoria =
        categoria.getEstablecimiento().getId();

    if (!establecimientoCategoria.equals(
        establecimientoUsuario
    )) {

      throw new BusinessException(
          "No tienes permiso sobre esta categoría"
      );
    }
  }


  @Override
  @CacheEvict(value = "productos", allEntries = true)
  public MensajeResponse crearCategoria(CreateCategoriaRequest request) {

    Usuario usuario =
        SecurityUtils
            .obtenerUsuarioAutenticado();

    Long establecimientoId =
        usuario.getEstablecimiento().getId();

    String nombreNormalizado =
        normalizarNombreCategoria(
            request.getNombre()
        );

    boolean existeCategoria =
        categoriaRepository
            .existsByNombreAndEstablecimientoId(
                nombreNormalizado,
                establecimientoId
            );

    if (existeCategoria) {

      throw new BusinessException(
          "La categoría ya existe"
      );
    }

    Establecimiento establecimiento =
        establecimientoRepository
            .findById(establecimientoId)
            .orElseThrow(() ->
                new BusinessException(
                    "Establecimiento no encontrado"
                )
            );

    request.setNombre(nombreNormalizado);

    Categoria categoria =
        CategoriaMapper.toEntity(request);

    categoria.setEstablecimiento(
        establecimiento
    );
    categoria.setEstado(EstadoCategoria.ACTIVO);

    Categoria categoriaGuardada =
        categoriaRepository.save(categoria);

    logSistemaService.registrarLog(
        "CATEGORIA",
        categoriaGuardada.getId(),
        categoriaGuardada.getNombre(),
        "Crear categoría",
        "Se creó la categoría "
            + categoriaGuardada.getNombre(),
        TipoOperacion.INSERT
    );

    evictarCacheProductosPostCommit();

    return MensajeResponse.builder()
        .mensaje("Categoría creada correctamente")
        .build();
  }

  @Override
  public List<CategoriaResponse> listarCategorias() {

    Usuario usuario =
        SecurityUtils
            .obtenerUsuarioAutenticado();

    Long establecimientoId =
        usuario.getEstablecimiento().getId();

    List<Categoria> categorias =
        categoriaRepository
            .findAllByEstablecimientoIdOrderByIdDesc(
                establecimientoId
            );

    return categorias.stream()
        .map(CategoriaMapper::toResponse)
        .toList();
  }

  @Override
  @CacheEvict(value = "productos", allEntries = true)
  public CategoriaResponse editarCategoria(Long id, UpdateCategoriaRequest request) {

    Categoria categoria =
        categoriaRepository.findById(id)
            .orElseThrow(() ->
                new BusinessException(
                    "Categoría no encontrada"
                )
            );
    validarCategoriaDelEstablecimiento(
        categoria
    );

    String nombreAnterior =
        categoria.getNombre();

    String descripcionAnterior =
        categoria.getDescripcion();

    String estadoAnterior =
        categoria.getEstado()
            .name();

    String nombreNormalizado =
        normalizarNombreCategoria(
            request.getNombre()
        );

    categoria.setNombre(
        nombreNormalizado
    );

    categoria.setDescripcion(
        request.getDescripcion()
    );

    categoria.setEstado(
        request.getEstado()
    );
    Categoria categoriaActualizada =
        categoriaRepository.save(categoria);

      logSistemaService.registrarAuditoria(
          "CATEGORIA",
          categoriaActualizada.getId(),
          categoriaActualizada.getNombre(),
          "nombre",
          nombreAnterior,
          categoriaActualizada.getNombre(),
          "Se modificó el nombre de la categoría"
      );


        logSistemaService.registrarAuditoria(
            "CATEGORIA",
            categoriaActualizada.getId(),
            categoriaActualizada.getNombre(),
            "descripcion",
            descripcionAnterior,
            categoriaActualizada.getDescripcion(),
            "Se modificó la descripción de la categoría"
        );


        logSistemaService.registrarAuditoria(
            "CATEGORIA",
            categoriaActualizada.getId(),
            categoriaActualizada.getNombre(),
            "estado",
            estadoAnterior,
            categoriaActualizada.getEstado()
                .name(),
            "Se modificó el estado de la categoría"
        );

    evictarCacheProductosPostCommit();

    return CategoriaMapper.toResponse(
        categoriaActualizada
    );
  }

  @Override
  @CacheEvict(value = "productos", allEntries = true)
  public void eliminarCategoria(Long id) {
    Categoria categoria =
        categoriaRepository.findById(id)
            .orElseThrow(() ->
                new BusinessException(
                    "Categoría no encontrada"
                )
            );
    validarCategoriaDelEstablecimiento(categoria);


    EstadoCategoria estadoAnterior =
        categoria.getEstado();

    categoria.setEstado(
        EstadoCategoria.INACTIVO
    );

    categoriaRepository.save(
        categoria
    );

    logSistemaService.registrarAuditoria(
        "CATEGORIA",
        categoria.getId(),
        categoria.getNombre(),
        "estado",
        estadoAnterior.name(),
        categoria.getEstado().name(),
        "Categoría desactivada"
    );

    evictarCacheProductosPostCommit();
  }


  private String normalizarNombreCategoria(String nombre) {

    String nombreNormalizado =
        nombre.trim().toLowerCase();

    return Character.toUpperCase(
        nombreNormalizado.charAt(0)
    ) + nombreNormalizado.substring(1);
  }

  private void evictarCacheProductosPostCommit() {
    Runnable evictTask = () -> {
      Cache cache = cacheManager.getCache("productos");
      if (cache != null) {
        cache.clear();
      }
    };

    if (TransactionSynchronizationManager.isActualTransactionActive()) {
      TransactionSynchronizationManager.registerSynchronization(
          new TransactionSynchronization() {
            @Override
            public void afterCommit() {
              evictTask.run();
            }
          }
      );
    } else {
      evictTask.run();
    }
  }

}
