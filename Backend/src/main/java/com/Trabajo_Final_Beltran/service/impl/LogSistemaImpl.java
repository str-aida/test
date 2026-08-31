package com.Trabajo_Final_Beltran.service.impl;


import com.Trabajo_Final_Beltran.dto.response.LogSistemaResponse;
import com.Trabajo_Final_Beltran.dto.response.PageResponse;
import com.Trabajo_Final_Beltran.entity.LogSistema;
import com.Trabajo_Final_Beltran.entity.Usuario;
import com.Trabajo_Final_Beltran.enums.TipoOperacion;
import com.Trabajo_Final_Beltran.mapper.LogSistemaMapper;
import com.Trabajo_Final_Beltran.repository.LogSistemaRepository;
import com.Trabajo_Final_Beltran.security.SecurityUtils;
import com.Trabajo_Final_Beltran.service.LogSistemaService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Objects;
import com.Trabajo_Final_Beltran.enums.Rol;
import com.Trabajo_Final_Beltran.specification.LogSistemaSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.beans.factory.annotation.Value;

@Service
@RequiredArgsConstructor
public class LogSistemaImpl implements LogSistemaService {

  private final LogSistemaRepository logSistemaRepository;

  @Value("${gestia.logs.exportacion.max-registros}")
  private int maxRegistrosExportacion;

  @Override
  public void registrarLog(
      String tablaAfectada,
      Long idRegistro,
      String referencia,
      String accion,
      String descripcion,
      TipoOperacion tipoOperacion
  ) {

    LogSistema log =
        LogSistema.builder()
            .tablaAfectada(tablaAfectada)
            .idRegistro(idRegistro)
            .referencia(referencia)
            .accion(accion)
            .descripcion(descripcion)
            .tipoOperacion(tipoOperacion)
            .build();

    persistirLog(log);

  }

  @Override
  public void registrarAuditoria(
      String tablaAfectada,
      Long idRegistro,
      String referencia,
      String campoModificado,
      String valorAnterior,
      String valorNuevo,
      String descripcion) {

    if (
        Objects.equals(
            valorAnterior,
            valorNuevo
        )
    ) {
      return;
    }


    LogSistema log =
        LogSistema.builder()
            .tablaAfectada(tablaAfectada)
            .idRegistro(idRegistro)
            .referencia(referencia)
            .accion("Modificación")
            .campoModificado(campoModificado)
            .valorAnterior(valorAnterior)
            .valorNuevo(valorNuevo)
            .descripcion(descripcion)
            .tipoOperacion(
                TipoOperacion.UPDATE
            )
            .build();

    persistirLog(log);
  }

  @Override
  public PageResponse<LogSistemaResponse> listarLogs(
      String accion,
      Rol rol,
      String usuario,
      int page,
      int size
  ) {

    Specification<LogSistema> spec =
        crearSpecification(
            accion,
            rol,
            usuario
        );

    Pageable pageable =
        PageRequest.of(
            page,
            size,
            Sort.by(
                Sort.Direction.DESC,
                "fecha"
            ).and(
                Sort.by(
                    Sort.Direction.DESC,
                    "id"
                )
            )
        );

    Page<LogSistema> logs =
        logSistemaRepository.findAll(
            spec,
            pageable
        );

    return PageResponse.<LogSistemaResponse>builder()
        .content(
            logs.getContent()
                .stream()
                .map(LogSistemaMapper::toResponse)
                .toList()
        )
        .pagina(logs.getNumber())
        .size(logs.getSize())
        .totalElementos(logs.getTotalElements())
        .totalPaginas(logs.getTotalPages())
        .primera(logs.isFirst())
        .ultima(logs.isLast())
        .build();
  }
  private Specification<LogSistema> crearSpecification(
      String accion,
      Rol rol,
      String usuario
  ) {

    Specification<LogSistema> spec =
        (root, query, cb)
            -> cb.conjunction();

    if (accion != null && !accion.isBlank()) {
      spec = spec.and(
          LogSistemaSpecification.accion(accion)
      );
    }

    if (usuario != null && !usuario.isBlank()) {
      spec = spec.and(
          LogSistemaSpecification.usuario(usuario)
      );
    }

    if (rol != null) {
      spec = spec.and(
          LogSistemaSpecification.rol(rol)
      );
    }

    return spec;
  }

  private void persistirLog(
      LogSistema log
  ) {

    Usuario usuario =
        SecurityUtils.obtenerUsuarioAutenticado();

    log.setUsuario(usuario);

    log.setFecha(
        LocalDateTime.now()
    );

    logSistemaRepository.save(log);
  }

  @Override
  public List<LogSistemaResponse> obtenerLogsParaExportacion(
      String accion,
      Rol rol,
      String usuario
  ) {

    Specification<LogSistema> spec =
        crearSpecification(
            accion,
            rol,
            usuario
        );

    Pageable pageable =
        PageRequest.of(
            0,
            maxRegistrosExportacion,
            Sort.by(
                Sort.Direction.DESC,
                "fecha"
            ).and(
                Sort.by(
                    Sort.Direction.DESC,
                    "id"
                )
            )
        );

    Page<LogSistema> logs =
        logSistemaRepository.findAll(
            spec,
            pageable
        );

    return logs.getContent()
        .stream()
        .map(LogSistemaMapper::toResponse)
        .toList();
  }

}
