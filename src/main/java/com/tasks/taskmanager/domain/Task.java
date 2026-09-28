package com.tasks.taskmanager.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** Entidad principal que representa una tarea en el sistema. */
@Entity
@Table(name = "tasks")
public class Task {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "El título no puede estar vacío")
  @Size(min = 3, max = 120, message = "El título debe tener entre 3 y 120 caracteres")
  @Column(nullable = false, length = 120)
  private String title;

  @Size(max = 1000, message = "La descripción no puede superar los 1000 caracteres")
  @Column(length = 1000)
  private String description;

  @NotNull(message = "El estado es obligatorio")
  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private TaskStatus status;

  @NotNull(message = "La prioridad es obligatoria")
  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 10)
  private TaskPriority priority;

  @NotNull(message = "La fecha límite es obligatoria")
  @Future(message = "La fecha límite debe ser una fecha futura")
  @Column(name = "due_date", nullable = false)
  private LocalDate dueDate;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  protected Task() {}

  public Task(
      String title,
      String description,
      TaskStatus status,
      TaskPriority priority,
      LocalDate dueDate) {
    this.title = title;
    this.description = description;
    this.status = status;
    this.priority = priority;
    this.dueDate = dueDate;
  }

  @PrePersist
  private void onPersist() {
    createdAt = LocalDateTime.now();
    updatedAt = createdAt;
  }

  @PreUpdate
  private void onUpdate() {
    updatedAt = LocalDateTime.now();
  }

  // ---- Getters & Setters ----

  public Long getId() {
    return id;
  }

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

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }
}
