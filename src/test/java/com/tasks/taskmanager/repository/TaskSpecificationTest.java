package com.tasks.taskmanager.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.tasks.taskmanager.domain.Task;
import com.tasks.taskmanager.domain.TaskPriority;
import com.tasks.taskmanager.domain.TaskStatus;
import com.tasks.taskmanager.dto.TaskFilterParams;
import com.tasks.taskmanager.service.TaskServiceImpl;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

/**
 * Tests unitarios para {@link TaskSpecification} y la integración con el servicio. Se usa Mockito
 * para aislar el repositorio y verificar que las Specifications correctas se construyen y se pasan
 * al repositorio.
 */
@ExtendWith(MockitoExtension.class)
class TaskSpecificationTest {

  @Mock private TaskRepository taskRepository;

  private TaskServiceImpl taskService;

  private final LocalDate futureDate = LocalDate.now().plusDays(15);

  @BeforeEach
  void setUp() {
    taskService = new TaskServiceImpl(taskRepository);
  }

  // -----------------------------------------------------------------------
  // TEST 1: Sin filtros devuelve todas las tareas (Specification vacía)
  // -----------------------------------------------------------------------
  @Test
  @DisplayName("TF01 - Sin filtros se invoca findAll con Specification y Sort")
  void getFilteredTasks_noFilters_callsFindAllWithSpecAndSort() {
    Task t = buildTask(TaskStatus.PENDING, TaskPriority.LOW, futureDate);
    when(taskRepository.findAll(any(Specification.class), any(Sort.class))).thenReturn(List.of(t));

    TaskFilterParams params = new TaskFilterParams(); // defaults: sortBy=dueDate, sortDir=asc
    var result = taskService.getFilteredTasks(params);

    assertThat(result).hasSize(1);
    verify(taskRepository).findAll(any(Specification.class), any(Sort.class));
  }

  // -----------------------------------------------------------------------
  // TEST 2: Filtro por status
  // -----------------------------------------------------------------------
  @Test
  @DisplayName("TF02 - Filtro por status delega correctamente al repositorio")
  void getFilteredTasks_withStatus_delegatesToRepository() {
    Task t = buildTask(TaskStatus.IN_PROGRESS, TaskPriority.HIGH, futureDate);
    when(taskRepository.findAll(any(Specification.class), any(Sort.class))).thenReturn(List.of(t));

    TaskFilterParams params = new TaskFilterParams();
    params.setStatus(TaskStatus.IN_PROGRESS);

    var result = taskService.getFilteredTasks(params);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
  }

  // -----------------------------------------------------------------------
  // TEST 3: Filtro por prioridad
  // -----------------------------------------------------------------------
  @Test
  @DisplayName("TF03 - Filtro por priority delega correctamente al repositorio")
  void getFilteredTasks_withPriority_delegatesToRepository() {
    Task t = buildTask(TaskStatus.PENDING, TaskPriority.CRITICAL, futureDate);
    when(taskRepository.findAll(any(Specification.class), any(Sort.class))).thenReturn(List.of(t));

    TaskFilterParams params = new TaskFilterParams();
    params.setPriority(TaskPriority.CRITICAL);

    var result = taskService.getFilteredTasks(params);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getPriority()).isEqualTo(TaskPriority.CRITICAL);
  }

  // -----------------------------------------------------------------------
  // TEST 4: Resultado vacío cuando el repositorio no encuentra coincidencias
  // -----------------------------------------------------------------------
  @Test
  @DisplayName("TF04 - Sin coincidencias devuelve lista vacía")
  void getFilteredTasks_noMatches_returnsEmptyList() {
    when(taskRepository.findAll(any(Specification.class), any(Sort.class))).thenReturn(List.of());

    TaskFilterParams params = new TaskFilterParams();
    params.setStatus(TaskStatus.DONE);
    params.setPriority(TaskPriority.CRITICAL);

    var result = taskService.getFilteredTasks(params);

    assertThat(result).isEmpty();
  }

  // -----------------------------------------------------------------------
  // TEST 5: Ordenación descendente por fecha
  // -----------------------------------------------------------------------
  @Test
  @DisplayName("TF05 - Ordenación desc por dueDate pasa Sort correcto al repositorio")
  void getFilteredTasks_sortDescByDueDate_passesSortToRepository() {
    when(taskRepository.findAll(any(Specification.class), any(Sort.class))).thenReturn(List.of());

    TaskFilterParams params = new TaskFilterParams();
    params.setSortBy("dueDate");
    params.setSortDir("desc");

    taskService.getFilteredTasks(params);

    verify(taskRepository)
        .findAll(any(Specification.class), eq(Sort.by(Sort.Direction.DESC, "dueDate")));
  }

  // -----------------------------------------------------------------------
  // TEST 6: sortBy con valor no permitido hace fallback a dueDate
  // -----------------------------------------------------------------------
  @Test
  @DisplayName("TF06 - sortBy inválido hace fallback a 'dueDate'")
  void getFilteredTasks_invalidSortBy_fallbackToDueDate() {
    when(taskRepository.findAll(any(Specification.class), any(Sort.class))).thenReturn(List.of());

    TaskFilterParams params = new TaskFilterParams();
    params.setSortBy("campoInexistente");
    params.setSortDir("asc");

    taskService.getFilteredTasks(params);

    verify(taskRepository)
        .findAll(any(Specification.class), eq(Sort.by(Sort.Direction.ASC, "dueDate")));
  }

  // -----------------------------------------------------------------------
  // TEST 7: Combinación de filtros status + dueBefore
  // -----------------------------------------------------------------------
  @Test
  @DisplayName("TF07 - Filtros combinados status + dueBefore se delegan al repositorio")
  void getFilteredTasks_statusAndDueBefore_delegatesToRepository() {
    Task t = buildTask(TaskStatus.PENDING, TaskPriority.MEDIUM, futureDate);
    when(taskRepository.findAll(any(Specification.class), any(Sort.class))).thenReturn(List.of(t));

    TaskFilterParams params = new TaskFilterParams();
    params.setStatus(TaskStatus.PENDING);
    params.setDueBefore(futureDate.plusDays(5));

    var result = taskService.getFilteredTasks(params);

    assertThat(result).hasSize(1);
    verify(taskRepository, times(1)).findAll(any(Specification.class), any(Sort.class));
  }

  // -----------------------------------------------------------------------
  // TEST 8: Ordenación por createdAt
  // -----------------------------------------------------------------------
  @Test
  @DisplayName("TF08 - Ordenación por createdAt se pasa correctamente")
  void getFilteredTasks_sortByCreatedAt_passesSortToRepository() {
    when(taskRepository.findAll(any(Specification.class), any(Sort.class))).thenReturn(List.of());

    TaskFilterParams params = new TaskFilterParams();
    params.setSortBy("createdAt");
    params.setSortDir("asc");

    taskService.getFilteredTasks(params);

    verify(taskRepository)
        .findAll(any(Specification.class), eq(Sort.by(Sort.Direction.ASC, "createdAt")));
  }

  // -----------------------------------------------------------------------
  // Helpers
  // -----------------------------------------------------------------------

  private Task buildTask(TaskStatus status, TaskPriority priority, LocalDate dueDate) {
    return new Task("Tarea test", "descripción", status, priority, dueDate);
  }
}
