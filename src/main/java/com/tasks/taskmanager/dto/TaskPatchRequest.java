package com.tasks.taskmanager.dto;

import com.tasks.taskmanager.domain.TaskPriority;
import com.tasks.taskmanager.domain.TaskStatus;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/** DTO de entrada para actualización parcial de una tarea (PATCH). Campo null = no modificar. */
public class TaskPatchRequest {

  @Size(min = 3, max = 120, message = "El título debe tener entre 3 y 120 caracteres")
  private String title;

  @Size(max = 1000, message = "La descripción no puede superar los 1000 caracteres")
  private String description;

  private TaskStatus status;

  private TaskPriority priority;

  private LocalDate dueDate;

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
