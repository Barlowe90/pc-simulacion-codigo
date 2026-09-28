package com.tasks.taskmanager.dto;

import com.tasks.taskmanager.domain.TaskPriority;
import com.tasks.taskmanager.domain.TaskStatus;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

/** DTO de entrada para crear o actualizar una tarea. */
public class TaskRequest {

  @NotBlank(message = "El título no puede estar vacío")
  @Size(min = 3, max = 120, message = "El título debe tener entre 3 y 120 caracteres")
  private String title;

  @Size(max = 1000, message = "La descripción no puede superar los 1000 caracteres")
  private String description;

  @NotNull(message = "El estado es obligatorio")
  private TaskStatus status;

  @NotNull(message = "La prioridad es obligatoria")
  private TaskPriority priority;

  @NotNull(message = "La fecha límite es obligatoria")
  @Future(message = "La fecha límite debe ser una fecha futura")
  private LocalDate dueDate;

  // ---- Getters & Setters ----

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public TaskStatus getStatus() {
    return status;
  }

  public void setStatus(TaskStatus status) {
    this.status = status;
  }

  public TaskPriority getPriority() {
    return priority;
  }

  public void setPriority(TaskPriority priority) {
    this.priority = priority;
  }

  public LocalDate getDueDate() {
    return dueDate;
  }

  public void setDueDate(LocalDate dueDate) {
    this.dueDate = dueDate;
  }
}
