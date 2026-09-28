package com.tasks.taskmanager.exception;

/** Excepción lanzada cuando no se encuentra una tarea con el identificador dado. */
public class TaskNotFoundException extends RuntimeException {

  private final Long taskId;

  public TaskNotFoundException(Long taskId) {
    super("Tarea no encontrada con id: " + taskId);
    this.taskId = taskId;
  }

  public Long getTaskId() {
    return taskId;
  }
}
