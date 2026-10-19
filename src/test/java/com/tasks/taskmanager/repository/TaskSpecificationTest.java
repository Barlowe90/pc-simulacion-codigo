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
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
  @DisplayName("TF01 - Sin filtros se construye una página con el tamaño solicitado")
  void getFilteredTasks_noFilters_buildsPageWithRequestedSize() {
    Task t = buildTask(TaskStatus.PENDING, TaskPriority.LOW, futureDate);
    when(taskRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(new PageImpl<>(List.of(t)));

    TaskFilterParams params = new TaskFilterParams(); // defaults: sortBy=dueDate, sortDir=asc
    var result = taskService.getFilteredTasks(params, 0, 5);

    assertThat(result).hasSize(1);
    ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
    verify(taskRepository).findAll(any(Specification.class), pageableCaptor.capture());
    assertThat(pageableCaptor.getValue().getPageSize()).isEqualTo(5);
  }

  // -----------------------------------------------------------------------
  // TEST 2: Filtro por status
  // -----------------------------------------------------------------------
  @Test
  @DisplayName("TF02 - Filtro por status delega correctamente al repositorio")
  void getFilteredTasks_withStatus_delegatesToRepository() {
    Task t = buildTask(TaskStatus.IN_PROGRESS, TaskPriority.HIGH, futureDate);
    when(taskRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(new PageImpl<>(List.of(t)));

    TaskFilterParams params = new TaskFilterParams();
    params.setStatus(TaskStatus.IN_PROGRESS);

    var result = taskService.getFilteredTasks(params, 0, 10);

    assertThat(result).hasSize(1);
    assertThat(result.getContent().get(0).getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
    verify(taskRepository).findAll(any(Specification.class), any(Pageable.class));
  }

  // -----------------------------------------------------------------------
  // TEST 3: Filtro por prioridad
  // -----------------------------------------------------------------------
  @Test
  @DisplayName("TF03 - Filtro por priority delega correctamente al repositorio")
  void getFilteredTasks_withPriority_delegatesToRepository() {
    Task t = buildTask(TaskStatus.PENDING, TaskPriority.CRITICAL, futureDate);
    when(taskRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(new PageImpl<>(List.of(t)));

    TaskFilterParams params = new TaskFilterParams();
    params.setPriority(TaskPriority.CRITICAL);

    var result = taskService.getFilteredTasks(params, 0, 10);

    assertThat(result).hasSize(1);
    assertThat(result.getContent().get(0).getPriority()).isEqualTo(TaskPriority.CRITICAL);
  }

  // -----------------------------------------------------------------------
  // TEST 4: Resultado vacío cuando el repositorio no encuentra coincidencias
  // -----------------------------------------------------------------------
  @Test
  @DisplayName("TF04 - Sin coincidencias devuelve página vacía")
  void getFilteredTasks_noMatches_returnsEmptyPage() {
    when(taskRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(new PageImpl<>(List.of()));

    TaskFilterParams params = new TaskFilterParams();
    params.setStatus(TaskStatus.DONE);
    params.setPriority(TaskPriority.CRITICAL);

    var result = taskService.getFilteredTasks(params, 0, 10);

    assertThat(result).isEmpty();
  }

  // -----------------------------------------------------------------------
  // TEST 5: Ordenación descendente por fecha
  // -----------------------------------------------------------------------
  @Test
  @DisplayName("TF05 - Ordenación desc por dueDate pasa Sort correcto al repositorio")
  void getFilteredTasks_sortDescByDueDate_passesSortToRepository() {
    when(taskRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(new PageImpl<>(List.of()));

    TaskFilterParams params = new TaskFilterParams();
    params.setSortBy("dueDate");
    params.setSortDir("desc");

    taskService.getFilteredTasks(params, 2, 5);

    ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
    verify(taskRepository).findAll(any(Specification.class), pageableCaptor.capture());
    assertThat(pageableCaptor.getValue().getPageNumber()).isEqualTo(2);
    assertThat(pageableCaptor.getValue().getPageSize()).isEqualTo(5);
    assertThat(pageableCaptor.getValue().getSort())
        .isEqualTo(Sort.by(Sort.Direction.DESC, "dueDate"));
  }

  // -----------------------------------------------------------------------
  // TEST 6: sortBy con valor no permitido hace fallback a dueDate
  // -----------------------------------------------------------------------
  @Test
  @DisplayName("TF06 - sortBy inválido hace fallback a 'dueDate'")
  void getFilteredTasks_invalidSortBy_fallbackToDueDate() {
    when(taskRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(new PageImpl<>(List.of()));

    TaskFilterParams params = new TaskFilterParams();
    params.setSortBy("campoInexistente");
    params.setSortDir("asc");

    taskService.getFilteredTasks(params, 0, 10);

    ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
    verify(taskRepository).findAll(any(Specification.class), pageableCaptor.capture());
    assertThat(pageableCaptor.getValue().getSort())
        .isEqualTo(Sort.by(Sort.Direction.ASC, "dueDate"));
  }

  // -----------------------------------------------------------------------
  // TEST 7: Combinación de filtros status + dueBefore
  // -----------------------------------------------------------------------
  @Test
  @DisplayName("TF07 - Filtros combinados status + dueBefore se delegan al repositorio")
  void getFilteredTasks_statusAndDueBefore_delegatesToRepository() {
    Task t = buildTask(TaskStatus.PENDING, TaskPriority.MEDIUM, futureDate);
    when(taskRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(new PageImpl<>(List.of(t)));

    TaskFilterParams params = new TaskFilterParams();
    params.setStatus(TaskStatus.PENDING);
    params.setDueBefore(futureDate.plusDays(5));

    var result = taskService.getFilteredTasks(params, 1, 3);

    assertThat(result).hasSize(1);
    verify(taskRepository, times(1))
        .findAll(
            any(Specification.class),
            argThat((Pageable p) -> p.getPageNumber() == 1 && p.getPageSize() == 3));
  }

  // -----------------------------------------------------------------------
  // TEST 8: Ordenación por createdAt
  // -----------------------------------------------------------------------
  @Test
  @DisplayName("TF08 - Ordenación por createdAt se pasa correctamente")
  void getFilteredTasks_sortByCreatedAt_passesSortToRepository() {
    when(taskRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(new PageImpl<>(List.of()));

    TaskFilterParams params = new TaskFilterParams();
    params.setSortBy("createdAt");
    params.setSortDir("asc");

    taskService.getFilteredTasks(params, 0, 10);

    ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
    verify(taskRepository).findAll(any(Specification.class), pageableCaptor.capture());
    assertThat(pageableCaptor.getValue().getSort())
        .isEqualTo(Sort.by(Sort.Direction.ASC, "createdAt"));
  }

  // -----------------------------------------------------------------------
  // Helpers
  // -----------------------------------------------------------------------

  private Task buildTask(TaskStatus status, TaskPriority priority, LocalDate dueDate) {
    return new Task("Tarea test", "descripción", status, priority, dueDate);
  }
}
