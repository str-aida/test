package com.Trabajo_Final_Beltran.service.impl;


import com.Trabajo_Final_Beltran.config.StorageProperties;
import com.Trabajo_Final_Beltran.service.ImageStorageService;
import com.Trabajo_Final_Beltran.service.ImageValidationService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.Trabajo_Final_Beltran.exception.StorageException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(
    name = "storage.provider",
    havingValue = "LOCAL"
)
public class LocalFileStorageServiceImpl implements ImageStorageService {

  private final ImageValidationService imageValidationService;

  private final StorageProperties storageProperties;

  @PostConstruct
  private void verificarConfiguracion() {
    log.info("Ruta configurada para almacenamiento de imágenes: {}",
        storageProperties.getLocal().getPath());
  }

  @Override
  public String guardar(
      MultipartFile archivo,
      Long establecimientoId
  ) {
    imageValidationService.validar(archivo);
    try {

      Path directorio =
          Paths.get(
              storageProperties.getLocal().getPath(),
              "establecimiento-" + establecimientoId,
              "productos"
          );

      Files.createDirectories(directorio);

      String extension =
          obtenerExtension(archivo.getOriginalFilename());

      String nombreArchivo =
          UUID.randomUUID() + extension;

      Path archivoDestino =
          directorio.resolve(nombreArchivo);

      archivo.transferTo(archivoDestino);

      return "/uploads/establecimiento-"
          + establecimientoId
          + "/productos/"
          + nombreArchivo;

    } catch (IOException e) {

      log.error(
          "Error al guardar la imagen del producto",
          e
      );

      throw new StorageException(
          "No se pudo guardar la imagen",
          e
      );
    }
  }

  @Override
  public void eliminar(String imagenUrl) {

    if (imagenUrl == null || imagenUrl.isBlank()) {
      return;
    }

    try {

      String rutaRelativa =
          imagenUrl.replaceFirst("^/uploads/?", "");

      Path archivo =
          Paths.get(
              storageProperties.getLocal().getPath(),
              rutaRelativa
          );

      Files.deleteIfExists(archivo);

    } catch (IOException e) {

      log.error(
          "Error al eliminar la imagen: {}",
          imagenUrl,
          e
      );

      throw new StorageException(
          "No se pudo eliminar la imagen",
          e
      );
    }
  }

  private String obtenerExtension(
      String nombreArchivo
  ) {

    if (
        nombreArchivo == null
            || !nombreArchivo.contains(".")
    ) {
      return "";
    }

    return nombreArchivo.substring(
        nombreArchivo.lastIndexOf(".")
    ).toLowerCase();
  }
}