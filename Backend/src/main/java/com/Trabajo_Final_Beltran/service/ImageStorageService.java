package com.Trabajo_Final_Beltran.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImageStorageService {

  String guardar(MultipartFile archivo,
      Long establecimientoId);

  void eliminar(String imagenUrl);
}