package com.Trabajo_Final_Beltran.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ApiError> handleBusinessException(
      BusinessException ex
  ) {

    ApiError apiError =
        ApiError.builder()
            .message(ex.getMessage())
            .build();

    return ResponseEntity
        .badRequest()
        .body(apiError);
  }

  @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
  public ResponseEntity<ApiError> handleOptimisticLockException(
      ObjectOptimisticLockingFailureException ex
  ) {

    ApiError apiError =
        ApiError.builder()
            .message("El registro fue modificado por otro usuario. Intente nuevamente.")
            .build();

    return ResponseEntity
        .status(HttpStatus.CONFLICT)
        .body(apiError);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiError> handleValidationException(
      MethodArgumentNotValidException ex
  ) {

    String mensaje =
        ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .findFirst()
            .map(FieldError::getDefaultMessage)
            .orElse("Error de validación");

    ApiError apiError =
        ApiError.builder()
            .message(mensaje)
            .build();

    return ResponseEntity
        .badRequest()
        .body(apiError);
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<ApiError> handleBadCredentials(
      BadCredentialsException ex
  ) {
    ApiError apiError =
        ApiError.builder()
            .message("Email o contraseña incorrectos")
            .build();
    return ResponseEntity
        .status(HttpStatus.UNAUTHORIZED)
        .body(apiError);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiError> handleException(
      Exception ex
  ) {
    log.error("Error interno del servidor", ex);

    ApiError apiError =
        ApiError.builder()
            .message("Error interno del servidor")
            .build();
    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(apiError);
  }

  @ExceptionHandler(PdfException.class)
  public ResponseEntity<ApiError> handlePdfException(
      PdfException ex
  ) {

    log.error("Error al generar el PDF de auditoría", ex);

    ApiError apiError =
        ApiError.builder()
            .message("No se pudo generar el reporte de auditoría")
            .build();

    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(apiError);
  }

  @ExceptionHandler(StorageException.class)
  public ResponseEntity<ApiError> handleStorageException(
      StorageException ex
  ) {

    log.error("Error en el almacenamiento de archivos", ex);

    ApiError apiError =
        ApiError.builder()
            .message("No se pudo procesar la imagen")
            .build();

    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(apiError);
  }
}