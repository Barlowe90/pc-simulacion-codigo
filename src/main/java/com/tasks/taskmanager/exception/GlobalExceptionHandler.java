package com.tasks.taskmanager.exception;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Manejador centralizado de excepciones para la API REST. Garantiza que ninguna excepción escapa
 * como traza de pila al cliente.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  /** 404 – Tarea no encontrada. */
  @ExceptionHandler(TaskNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleTaskNotFound(TaskNotFoundException ex) {
    ErrorResponse body =
        new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Not Found", ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
  }

  /** 422 – Regla de negocio violada (estado inválido, etc.). */
  @ExceptionHandler(InvalidTaskStateException.class)
  public ResponseEntity<ErrorResponse> handleInvalidState(InvalidTaskStateException ex) {
    ErrorResponse body =
        new ErrorResponse(
            HttpStatus.UNPROCESSABLE_ENTITY.value(), "Unprocessable Entity", ex.getMessage());
    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(body);
  }

  /** 400 – Errores de validación de Bean Validation (@Valid). */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
    List<String> details =
        ex.getBindingResult().getFieldErrors().stream()
            .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
            .toList();

    ErrorResponse body =
        new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Bad Request",
            "Errores de validación en la petición",
            details);
    return ResponseEntity.badRequest().body(body);
  }

  /** 500 – Cualquier excepción no prevista. */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
    ErrorResponse body =
        new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Internal Server Error",
            "Se ha producido un error inesperado");
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
  }
}
