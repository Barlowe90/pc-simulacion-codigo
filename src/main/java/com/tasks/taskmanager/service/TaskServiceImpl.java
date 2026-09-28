package com.tasks.taskmanager.service;

import com.tasks.taskmanager.domain.Task;
import com.tasks.taskmanager.domain.TaskStatus;
import com.tasks.taskmanager.dto.TaskFilterParams;
import com.tasks.taskmanager.dto.TaskRequest;
import com.tasks.taskmanager.dto.TaskResponse;
import com.tasks.taskmanager.exception.InvalidTaskStateException;
import com.tasks.taskmanager.exception.TaskNotFoundException;
import com.tasks.taskmanager.repository.TaskRepository;
import com.tasks.taskmanager.repository.TaskSpecification;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementación de la capa de servicio para tareas. Centraliza la lógica de negocio y las
 * validaciones de dominio.
 */
@Service
@Transactional
public class TaskServiceImpl implements TaskService {

  /** Campos por los que se permite ordenar. */
  private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("dueDate", "priority", "createdAt");

  private final TaskRepository taskRepository;

  public TaskServiceImpl(TaskRepository taskRepository) {
    this.taskRepository = taskRepository;
  }

  // ------------------------------------------------------------------ CREATE

  @Override
  public TaskResponse createTask(TaskRequest request) {
    validateDueDateNotPast(request.getDueDate());

    Task task =
        new Task(
            request.getTitle(),
            request.getDescription(),
            request.getStatus(),
            request.getPriority(),
            request.getDueDate());

    return TaskResponse.from(taskRepository.save(task));
  }

  // ------------------------------------------------------------------- READ

  @Override
  @Transactional(readOnly = true)
  public List<TaskResponse> getAllTasks() {
    return taskRepository.findAll().stream().map(TaskResponse::from).toList();
  }

  @Override
  @Transactional(readOnly = true)
  public List<TaskResponse> getFilteredTasks(TaskFilterParams params) {
    Specification<Task> spec =
        TaskSpecification.withFilters(
            params.getStatus(), params.getPriority(), params.getDueBefore(), params.getDueAfter());

    Sort sort = buildSort(params.getSortBy(), params.getSortDir());

    return taskRepository.findAll(spec, sort).stream().map(TaskResponse::from).toList();
  }

  @Override
  @Transactional(readOnly = true)
  public TaskResponse getTaskById(Long id) {
    return taskRepository
        .findById(id)
        .map(TaskResponse::from)
        .orElseThrow(() -> new TaskNotFoundException(id));
  }

  // ------------------------------------------------------------------ UPDATE

  @Override
  public TaskResponse updateTask(Long id, TaskRequest request) {
    Task task = taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));

    validateDueDateNotPast(request.getDueDate());
    validateTransition(task.getStatus(), request.getStatus());

    task.setTitle(request.getTitle());
    task.setDescription(request.getDescription());
    task.setStatus(request.getStatus());
    task.setPriority(request.getPriority());
    task.setDueDate(request.getDueDate());

    return TaskResponse.from(taskRepository.save(task));
  }

  // ------------------------------------------------------------------ DELETE

  @Override
  public void deleteTask(Long id) {
    if (!taskRepository.existsById(id)) {
      throw new TaskNotFoundException(id);
    }
    taskRepository.deleteById(id);
  }

  // ----------------------------------------------------------- SEARCH

  @Override
  @Transactional(readOnly = true)
  public List<TaskResponse> searchByText(String query) {
    if (query == null || query.isBlank()) {
      return List.of();
    }
    return taskRepository
        .findAll(TaskSpecification.containsText(query.trim()))
        .stream()
        .map(TaskResponse::from)
        .toList();
  }

  // -------------------------------------------------------- BUSINESS RULES

  /** Regla: la fecha límite no puede ser hoy ni en el pasado. */
  private void validateDueDateNotPast(LocalDate dueDate) {
    if (dueDate != null && !dueDate.isAfter(LocalDate.now())) {
      throw new InvalidTaskStateException(
          "La fecha límite debe ser una fecha futura, no: " + dueDate);
    }
  }

  /** Regla: una tarea CANCELADA no puede volver a ningún estado activo. */
  private void validateTransition(TaskStatus current, TaskStatus next) {
    if (current == TaskStatus.CANCELLED && next != TaskStatus.CANCELLED) {
      throw new InvalidTaskStateException("Una tarea cancelada no puede cambiar de estado.");
    }
    if (current == TaskStatus.DONE && next == TaskStatus.PENDING) {
      throw new InvalidTaskStateException("Una tarea completada no puede volver a estado PENDING.");
    }
  }

  // ------------------------------------------------------------ HELPERS

  /**
   * Construye un {@link Sort} a partir de los parámetros recibidos. Si {@code sortBy} no es un
   * campo permitido se usa {@code "dueDate"} como fallback seguro.
   */
  private Sort buildSort(String sortBy, String sortDir) {
    String field = ALLOWED_SORT_FIELDS.contains(sortBy) ? sortBy : "dueDate";
    Sort.Direction direction =
        "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;
    return Sort.by(direction, field);
  }
}
