package com.tasks.taskmanager.exception;

import java.time.LocalDateTime;
import java.util.List;

/** Cuerpo estándar devuelto en respuestas de error. */
public class ErrorResponse {

  private int status;
  private String error;
  private String message;
  private LocalDateTime timestamp;
  private List<String> details;

  public ErrorResponse(int status, String error, String message) {
    this.status = status;
    this.error = error;
    this.message = message;
    this.timestamp = LocalDateTime.now();
  }

  public ErrorResponse(int status, String error, String message, List<String> details) {
    this(status, error, message);
    this.details = details;
  }

  public int getStatus() {
    return status;
  }

  public String getError() {
    return error;
  }

  public String getMessage() {
    return message;
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  public List<String> getDetails() {
    return details;
  }
}
