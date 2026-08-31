package com.Trabajo_Final_Beltran.service.impl;

import com.Trabajo_Final_Beltran.exception.BusinessException;
import com.Trabajo_Final_Beltran.service.ImageValidationService;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ImageValidationServiceImpl implements ImageValidationService {

  private static final long MAX_SIZE =
      2 * 1024 * 1024;

  @Override
  public void validar(MultipartFile archivo) {

    if (archivo == null || archivo.isEmpty()) {
      return;
    }

    if (archivo.getSize() > MAX_SIZE) {
      throw new BusinessException(
          "La imagen no puede superar los 2 MB"
      );
    }


  String contentType = archivo.getContentType();

if (
  contentType == null
      ||
      (
      !contentType.equals("image/jpeg")
      &&
      !contentType.equals("image/png")
      )
      ) {
    throw new BusinessException(
        "Solo se permiten imágenes JPG, JPEG y PNG"
    );
  }

    try {
      BufferedImage imagen =
          ImageIO.read(archivo.getInputStream());

      if (imagen == null) {
        throw new BusinessException(
            "El archivo seleccionado no es una imagen válida"
        );
      }
      int ancho = imagen.getWidth();
      int alto = imagen.getHeight();

      if (ancho < 300 || alto < 300) {
        throw new BusinessException(
            "La imagen debe tener como mínimo 300 x 300 píxeles"
        );
      }

      if (ancho > 3000 || alto > 3000) {
        throw new BusinessException(
            "La imagen no puede superar los 3000 x 3000 píxeles"
        );
      }

    } catch (IOException e) {
      throw new BusinessException(
          "No se pudo validar la imagen"
      );
    }
}

}
