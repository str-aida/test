package com.Trabajo_Final_Beltran.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImageValidationService {

  void validar(MultipartFile archivo);
}