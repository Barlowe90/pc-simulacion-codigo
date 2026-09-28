package com.tasks.taskmanager.service;

import com.tasks.taskmanager.dto.TaskFilterParams;
import com.tasks.taskmanager.dto.TaskRequest;
import com.tasks.taskmanager.dto.TaskResponse;
import java.util.List;

/** Contrato del servicio de gestión de tareas. */
public interface TaskService {

  /** Crea una nueva tarea. */
  TaskResponse createTask(TaskRequest request);

  /** Devuelve todas las tareas existentes sin ningún filtro. */
  List<TaskResponse> getAllTasks();

  /**
   * Devuelve las tareas que cumplen los filtros indicados en {@code params}, ordenadas según los
   * campos {@code sortBy} y {@code sortDir}.
   */
  List<TaskResponse> getFilteredTasks(TaskFilterParams params);

  /**
   * Devuelve una tarea por su identificador. Lanza {@link
   * com.tasks.taskmanager.exception.TaskNotFoundException} si no existe.
   */
  TaskResponse getTaskById(Long id);

  /** Actualiza completamente una tarea existente. */
  TaskResponse updateTask(Long id, TaskRequest request);

  /**
   * Elimina una tarea. Lanza {@link com.tasks.taskmanager.exception.TaskNotFoundException} si no
   * existe.
   */
  void deleteTask(Long id);

  /**
   * Busca tareas cuyo título o descripción contengan {@code query} (insensible a mayúsculas).
   * Devuelve lista vacía si no hay coincidencias.
   */
  List<TaskResponse> searchByText(String query);
}
