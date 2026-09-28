package com.tasks.taskmanager.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.tasks.taskmanager.domain.Task;
import com.tasks.taskmanager.domain.TaskPriority;
import com.tasks.taskmanager.domain.TaskStatus;
import com.tasks.taskmanager.dto.TaskRequest;
import com.tasks.taskmanager.dto.TaskResponse;
import com.tasks.taskmanager.exception.InvalidTaskStateException;
import com.tasks.taskmanager.exception.TaskNotFoundException;
import com.tasks.taskmanager.repository.TaskRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests unitarios de {@link TaskServiceImpl}. No se levanta ningún contexto de Spring: solo
 * Mockito.
 */
@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

  @Mock private TaskRepository taskRepository;

  @InjectMocks private TaskServiceImpl taskService;

  private TaskRequest validRequest;
  private LocalDate futureDate;

  @BeforeEach
  void setUp() {
    futureDate = LocalDate.now().plusDays(10);

    validRequest = new TaskRequest();
    validRequest.setTitle("Implementar login");
    validRequest.setDescription("OAuth2 con Google");
    validRequest.setStatus(TaskStatus.PENDING);
    validRequest.setPriority(TaskPriority.HIGH);
    validRequest.setDueDate(futureDate);
  }

  // -----------------------------------------------------------------------
  // TEST 1: No se puede crear una tarea con fecha límite en el pasado
  // -----------------------------------------------------------------------
  @Test
  @DisplayName("T01 - Crear tarea con fecha pasada lanza InvalidTaskStateException")
  void createTask_withPastDueDate_throwsInvalidTaskStateException() {
    validRequest.setDueDate(LocalDate.now().minusDays(1));

    assertThatThrownBy(() -> taskService.createTask(validRequest))
        .isInstanceOf(InvalidTaskStateException.class)
        .hasMessageContaining("fecha límite debe ser una fecha futura");

    verify(taskRepository, never()).save(any());
  }

  // -----------------------------------------------------------------------
  // TEST 2: No se puede crear una tarea con fecha límite igual a hoy
  // -----------------------------------------------------------------------
  @Test
  @DisplayName("T02 - Crear tarea con fecha límite igual a hoy lanza InvalidTaskStateException")
  void createTask_withTodayDueDate_throwsInvalidTaskStateException() {
    validRequest.setDueDate(LocalDate.now());

    assertThatThrownBy(() -> taskService.createTask(validRequest))
        .isInstanceOf(InvalidTaskStateException.class);

    verify(taskRepository, never()).save(any());
  }

  // -----------------------------------------------------------------------
  // TEST 3: Crear tarea válida devuelve el DTO correctamente mapeado
  // -----------------------------------------------------------------------
  @Test
  @DisplayName("T03 - Crear tarea válida persiste y devuelve el DTO correcto")
  void createTask_withValidRequest_returnsMappedTaskResponse() {
    Task savedTask =
        buildTask(1L, "Implementar login", TaskStatus.PENDING, TaskPriority.HIGH, futureDate);
    when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

    TaskResponse response = taskService.createTask(validRequest);

    assertThat(response.getId()).isEqualTo(1L);
    assertThat(response.getTitle()).isEqualTo("Implementar login");
    assertThat(response.getStatus()).isEqualTo(TaskStatus.PENDING);
    assertThat(response.getPriority()).isEqualTo(TaskPriority.HIGH);
    assertThat(response.getDueDate()).isEqualTo(futureDate);
    verify(taskRepository).save(any(Task.class));
  }

  // -----------------------------------------------------------------------
  // TEST 4: Obtener tarea inexistente lanza TaskNotFoundException
  // -----------------------------------------------------------------------
  @Test
  @DisplayName("T04 - Obtener tarea inexistente lanza TaskNotFoundException")
  void getTaskById_withNonExistentId_throwsTaskNotFoundException() {
    when(taskRepository.findById(99L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> taskService.getTaskById(99L))
        .isInstanceOf(TaskNotFoundException.class)
        .hasMessageContaining("99");
  }

  // -----------------------------------------------------------------------
  // TEST 5: Una tarea CANCELADA no puede cambiar de estado
  // -----------------------------------------------------------------------
  @Test
  @DisplayName("T05 - Actualizar tarea CANCELADA a otro estado lanza InvalidTaskStateException")
  void updateTask_fromCancelledToAnyOtherStatus_throwsInvalidTaskStateException() {
    Task cancelledTask =
        buildTask(2L, "Tarea vieja", TaskStatus.CANCELLED, TaskPriority.LOW, futureDate);
    when(taskRepository.findById(2L)).thenReturn(Optional.of(cancelledTask));

    validRequest.setStatus(TaskStatus.IN_PROGRESS); // intento reactivar

    assertThatThrownBy(() -> taskService.updateTask(2L, validRequest))
        .isInstanceOf(InvalidTaskStateException.class)
        .hasMessageContaining("cancelada no puede cambiar de estado");

    verify(taskRepository, never()).save(any());
  }

  // -----------------------------------------------------------------------
  // TEST 6: Una tarea DONE no puede volver a PENDING
  // -----------------------------------------------------------------------
  @Test
  @DisplayName("T06 - Actualizar tarea DONE a PENDING lanza InvalidTaskStateException")
  void updateTask_fromDoneToPending_throwsInvalidTaskStateException() {
    Task doneTask =
        buildTask(3L, "Tarea terminada", TaskStatus.DONE, TaskPriority.MEDIUM, futureDate);
    when(taskRepository.findById(3L)).thenReturn(Optional.of(doneTask));

    validRequest.setStatus(TaskStatus.PENDING);

    assertThatThrownBy(() -> taskService.updateTask(3L, validRequest))
        .isInstanceOf(InvalidTaskStateException.class)
        .hasMessageContaining("completada no puede volver a estado PENDING");
  }

  // -----------------------------------------------------------------------
  // TEST 7: Eliminar tarea inexistente lanza TaskNotFoundException
  // -----------------------------------------------------------------------
  @Test
  @DisplayName("T07 - Eliminar tarea inexistente lanza TaskNotFoundException")
  void deleteTask_withNonExistentId_throwsTaskNotFoundException() {
    when(taskRepository.existsById(42L)).thenReturn(false);

    assertThatThrownBy(() -> taskService.deleteTask(42L))
        .isInstanceOf(TaskNotFoundException.class)
        .hasMessageContaining("42");

    verify(taskRepository, never()).deleteById(any());
  }

  // -----------------------------------------------------------------------
  // TEST 8: getAllTasks devuelve lista correctamente mapeada
  // -----------------------------------------------------------------------
  @Test
  @DisplayName("T08 - getAllTasks devuelve el listado completo mapeado a DTOs")
  void getAllTasks_returnsMappedList() {
    Task t1 = buildTask(1L, "Tarea A", TaskStatus.PENDING, TaskPriority.HIGH, futureDate);
    Task t2 =
        buildTask(
            2L, "Tarea B", TaskStatus.IN_PROGRESS, TaskPriority.MEDIUM, futureDate.plusDays(5));
    when(taskRepository.findAll()).thenReturn(List.of(t1, t2));

    List<TaskResponse> responses = taskService.getAllTasks();

    assertThat(responses).hasSize(2);
    assertThat(responses.get(0).getTitle()).isEqualTo("Tarea A");
    assertThat(responses.get(1).getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
  }

  // -----------------------------------------------------------------------
  // TEST 9: searchByText con query vacía devuelve lista vacía sin llamar al repo
  // -----------------------------------------------------------------------
  @Test
  @DisplayName("T09 - searchByText con query vacía devuelve lista vacía")
  void searchByText_withBlankQuery_returnsEmptyList() {
    List<TaskResponse> result = taskService.searchByText("   ");

    assertThat(result).isEmpty();
    verify(taskRepository, never()).findAll(any(org.springframework.data.jpa.domain.Specification.class));
  }

  // -----------------------------------------------------------------------
  // TEST 10: searchByText con query válida delega en el repositorio
  // -----------------------------------------------------------------------
  @Test
  @DisplayName("T10 - searchByText con query válida devuelve tareas coincidentes")
  void searchByText_withValidQuery_returnsmatchingTasks() {
    Task t1 = buildTask(1L, "Implementar login OAuth", TaskStatus.PENDING, TaskPriority.HIGH, futureDate);
    when(taskRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class)))
        .thenReturn(List.of(t1));

    List<TaskResponse> result = taskService.searchByText("oauth");

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getTitle()).isEqualTo("Implementar login OAuth");
    verify(taskRepository).findAll(any(org.springframework.data.jpa.domain.Specification.class));
  }

  // -----------------------------------------------------------------------
  // TEST 11: searchByText con query sin coincidencias devuelve lista vacía
  // -----------------------------------------------------------------------
  @Test
  @DisplayName("T11 - searchByText sin coincidencias devuelve lista vacía")
  void searchByText_withNoMatches_returnsEmptyList() {
    when(taskRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class)))
        .thenReturn(List.of());

    List<TaskResponse> result = taskService.searchByText("xyz_inexistente");

    assertThat(result).isEmpty();
  }

  // -----------------------------------------------------------------------
  // Helpers
  // -----------------------------------------------------------------------

  /**
   * Construye una instancia de Task usando reflexión para inyectar el id (que normalmente establece
   * JPA).
   */
  private Task buildTask(
      Long id, String title, TaskStatus status, TaskPriority priority, LocalDate dueDate) {
    Task task = new Task(title, null, status, priority, dueDate);
    try {
      var field = Task.class.getDeclaredField("id");
      field.setAccessible(true);
      field.set(task, id);
    } catch (Exception e) {
      throw new RuntimeException("No se pudo inyectar el id en Task", e);
    }
    return task;
  }
}
