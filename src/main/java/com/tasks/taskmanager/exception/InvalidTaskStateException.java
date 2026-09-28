package com.tasks.taskmanager.exception;

/**
 * Excepción de regla de negocio: la tarea se encuentra en un estado que no permite la operación
 * solicitada.
 */
public class InvalidTaskStateException extends RuntimeException {

  public InvalidTaskStateException(String message) {
    super(message);
  }
}
