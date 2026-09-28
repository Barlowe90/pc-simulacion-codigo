package com.tasks.taskmanager.dto;

import com.tasks.taskmanager.domain.Task;
import com.tasks.taskmanager.domain.TaskPriority;
import com.tasks.taskmanager.domain.TaskStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** DTO de salida con los datos de una tarea. */
public class TaskResponse {

  private Long id;
  private String title;
  private String description;
  private TaskStatus status;
  private TaskPriority priority;
  private LocalDate dueDate;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public static TaskResponse from(Task task) {
    TaskResponse r = new TaskResponse();
    r.id = task.getId();
    r.title = task.getTitle();
    r.description = task.getDescription();
    r.status = task.getStatus();
    r.priority = task.getPriority();
    r.dueDate = task.getDueDate();
    r.createdAt = task.getCreatedAt();
    r.updatedAt = task.getUpdatedAt();
    return r;
  }

  // ---- Getters ----

  public Long getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public String getDescription() {
    return description;
  }

  public TaskStatus getStatus() {
    return status;
  }

  public TaskPriority getPriority() {
    return priority;
  }

  public LocalDate getDueDate() {
    return dueDate;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }
}
