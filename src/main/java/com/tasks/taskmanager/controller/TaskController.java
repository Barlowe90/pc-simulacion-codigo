package com.tasks.taskmanager.controller;

import com.tasks.taskmanager.domain.TaskPriority;
import com.tasks.taskmanager.domain.TaskStatus;
import com.tasks.taskmanager.dto.TaskFilterParams;
import com.tasks.taskmanager.dto.TaskPatchRequest;
import com.tasks.taskmanager.dto.TaskRequest;
import com.tasks.taskmanager.dto.TaskResponse;
import com.tasks.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para las operaciones CRUD sobre tareas. Ruta base:
 * /api/v1/tasks
 */
@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /** POST /api/v1/tasks Crea una nueva tarea. */
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskRequest request) {
        TaskResponse created = taskService.createTask(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * GET /api/v1/tasks Devuelve el listado de tareas con filtrado y ordenación opcionales.
     *
     * <p>Query params opcionales:
     *
     * <ul>
     *   <li>{@code status} — PENDING | IN_PROGRESS | DONE | CANCELLED
     *   <li>{@code priority} — LOW | MEDIUM | HIGH | CRITICAL
     *   <li>{@code dueBefore} — fecha ISO (yyyy-MM-dd): tareas con dueDate ≤ este valor
     *   <li>{@code dueAfter} — fecha ISO (yyyy-MM-dd): tareas con dueDate ≥ este valor
     *   <li>{@code sortBy} — dueDate (default) | priority | createdAt
     *   <li>{@code sortDir} — asc (default) | desc
     *   <li>{@code page} — número de página, comenzando en 0 (default: 0)
     *   <li>{@code size} — número máximo de tareas por página (default: 10)
     * </ul>
     */
    @GetMapping
    public ResponseEntity<Page<TaskResponse>> getTasks(
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) TaskPriority priority,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueBefore,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueAfter,
            @RequestParam(required = false, defaultValue = "dueDate") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortDir,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {

        TaskFilterParams params = new TaskFilterParams();
        params.setStatus(status);
        params.setPriority(priority);
        params.setDueBefore(dueBefore);
        params.setDueAfter(dueAfter);
        params.setSortBy(sortBy);
        params.setSortDir(sortDir);

        return ResponseEntity.ok(taskService.getFilteredTasks(params, page, size));
    }

    /** GET /api/v1/tasks/search Busca tareas por texto libre. */
    @GetMapping("/search")
    public ResponseEntity<List<TaskResponse>> searchTasks(
            @RequestParam(name = "q", required = false, defaultValue = "") String query) {
        return ResponseEntity.ok(taskService.searchByText(query));
    }

    /** GET /api/v1/tasks/{id} Devuelve una tarea por su id (404 si no existe). */
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    /** PUT /api/v1/tasks/{id} Actualiza completamente una tarea existente. */
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long id, @Valid @RequestBody TaskRequest request) {
        return ResponseEntity.ok(taskService.updateTask(id, request));
    }

    /** PATCH /api/v1/tasks/{id} Actualiza parcialmente una tarea existente. */
    @PatchMapping("/{id}")
    public ResponseEntity<TaskResponse> patchTask(
            @PathVariable Long id, @Valid @RequestBody TaskPatchRequest request) {
        return ResponseEntity.ok(taskService.patchTask(id, request));
    }

    /** DELETE /api/v1/tasks/{id} Elimina una tarea (404 si no existe). */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
