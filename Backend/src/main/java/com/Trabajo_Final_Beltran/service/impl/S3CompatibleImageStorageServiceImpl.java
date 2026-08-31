package com.Trabajo_Final_Beltran.service.impl;

import com.Trabajo_Final_Beltran.config.StorageProperties;
import com.Trabajo_Final_Beltran.exception.StorageException;
import com.Trabajo_Final_Beltran.service.ImageStorageService;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import java.util.UUID;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.core.exception.SdkException;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(
    name = "storage.provider",
    havingValue = "S3"
)
public class S3CompatibleImageStorageServiceImpl implements ImageStorageService {

  private final S3Client s3Client;

  private final StorageProperties storageProperties;

  @Override
  public String guardar(
      MultipartFile archivo,
      Long establecimientoId
  ) {

    String extension =
        obtenerExtension(
            archivo.getOriginalFilename()
        );

    String nombreArchivo =
        UUID.randomUUID() + extension;

    String key =
        construirKey(
            establecimientoId,
            nombreArchivo
        );

    PutObjectRequest request =
        PutObjectRequest.builder()
            .bucket(
                storageProperties
                    .getS3()
                    .getBucket()
            )
            .key(key)
            .contentType(
                archivo.getContentType()
            )
            .build();

    try {

      s3Client.putObject(
          request,
          RequestBody.fromInputStream(
              archivo.getInputStream(),
              archivo.getSize()
          )
      );

    } catch (IOException | SdkException e) {

      throw new StorageException(
          "No se pudo guardar la imagen",
          e
      );
    }

    return construirPublicUrl(key);
  }

  @Override
  public void eliminar(String imagenUrl) {

    if (imagenUrl == null || imagenUrl.isBlank()) {
      return;
    }

    String key =
        extraerKey(imagenUrl);

    DeleteObjectRequest request =
        DeleteObjectRequest.builder()
            .bucket(
                storageProperties
                    .getS3()
                    .getBucket()
            )
            .key(key)
            .build();

    try {

      s3Client.deleteObject(request);

    } catch (SdkException e) {

      throw new StorageException(
          "No se pudo eliminar la imagen",
          e
      );
    }
  }

  private String construirKey(
      Long establecimientoId,
      String nombreArchivo
  ) {

    return "establecimiento-"
        + establecimientoId
        + "/productos/"
        + nombreArchivo;
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

  private String construirPublicUrl(
      String key
  ) {

    String publicUrl =
        storageProperties
            .getS3()
            .getPublicUrl();

    return publicUrl.replaceAll("/$", "")
        + "/"
        + key;
  }

  private String extraerKey(
      String imagenUrl
  ) {

    String publicUrl =
        storageProperties
            .getS3()
            .getPublicUrl()
            .replaceAll("/$", "");

    return imagenUrl.replace(
        publicUrl + "/",
        ""
    );
  }
}
