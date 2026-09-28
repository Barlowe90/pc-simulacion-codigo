package com.tasks.taskmanager.dto;

import com.tasks.taskmanager.domain.TaskPriority;
import com.tasks.taskmanager.domain.TaskStatus;
import java.time.LocalDate;

/**
 * Parámetros de filtrado y ordenación para el listado de tareas. Todos los campos son opcionales:
 * si son {@code null} ese criterio no se aplica.
 */
public class TaskFilterParams {

  /** Filtra por estado exacto. */
  private TaskStatus status;

  /** Filtra por nivel de prioridad exacto. */
  private TaskPriority priority;

  /** Devuelve tareas cuya fecha límite es anterior o igual a esta fecha. */
  private LocalDate dueBefore;

  /** Devuelve tareas cuya fecha límite es posterior o igual a esta fecha. */
  private LocalDate dueAfter;

  /**
   * Campo por el que se ordena el resultado. Valores aceptados: {@code "dueDate"} (por defecto),
   * {@code "priority"}, {@code "createdAt"}.
   */
  private String sortBy = "dueDate";

  /** Dirección del orden. Valores aceptados: {@code "asc"} (por defecto), {@code "desc"}. */
  private String sortDir = "asc";

  // ---- Getters & Setters ----

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

  public LocalDate getDueBefore() {
    return dueBefore;
  }

  public void setDueBefore(LocalDate dueBefore) {
    this.dueBefore = dueBefore;
  }

  public LocalDate getDueAfter() {
    return dueAfter;
  }

  public void setDueAfter(LocalDate dueAfter) {
    this.dueAfter = dueAfter;
  }

  public String getSortBy() {
    return sortBy;
  }

  public void setSortBy(String sortBy) {
    this.sortBy = sortBy;
  }

  public String getSortDir() {
    return sortDir;
  }

  public void setSortDir(String sortDir) {
    this.sortDir = sortDir;
  }
}
