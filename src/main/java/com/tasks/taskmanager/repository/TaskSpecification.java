package com.tasks.taskmanager.repository;

import com.tasks.taskmanager.domain.Task;
import com.tasks.taskmanager.domain.TaskPriority;
import com.tasks.taskmanager.domain.TaskStatus;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

/**
 * Factoría de {@link Specification} para construir consultas dinámicas sobre {@link Task}. Cada
 * método estático devuelve un predicado independiente que puede combinarse con otros.
 */
public final class TaskSpecification {

  private TaskSpecification() {
    /* utilidad, no instanciable */
  }

  /** Combina todos los filtros opcionales en una sola Specification. */
  public static Specification<Task> withFilters(
      TaskStatus status, TaskPriority priority, LocalDate dueBefore, LocalDate dueAfter) {

    return (root, query, cb) -> {
      List<Predicate> predicates = new ArrayList<>();

      if (status != null) {
        predicates.add(cb.equal(root.get("status"), status));
      }
      if (priority != null) {
        predicates.add(cb.equal(root.get("priority"), priority));
      }
      if (dueBefore != null) {
        predicates.add(cb.lessThanOrEqualTo(root.get("dueDate"), dueBefore));
      }
      if (dueAfter != null) {
        predicates.add(cb.greaterThanOrEqualTo(root.get("dueDate"), dueAfter));
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }

  // ---- Specifications atómicas (útiles en tests) ----

  /** Filtra por estado exacto. */
  public static Specification<Task> hasStatus(TaskStatus status) {
    return (root, query, cb) -> cb.equal(root.get("status"), status);
  }

  /** Filtra por prioridad exacta. */
  public static Specification<Task> hasPriority(TaskPriority priority) {
    return (root, query, cb) -> cb.equal(root.get("priority"), priority);
  }

  /** Filtra tareas con {@code dueDate <= dueBefore}. */
  public static Specification<Task> dueBefore(LocalDate date) {
    return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("dueDate"), date);
  }

  /** Filtra tareas con {@code dueDate >= dueAfter}. */
  public static Specification<Task> dueAfter(LocalDate date) {
    return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("dueDate"), date);
  }

  /**
   * Filtra tareas cuyo {@code title} o {@code description} contengan {@code text} (insensible a
   * mayúsculas).
   */
  public static Specification<Task> containsText(String text) {
    return (root, query, cb) -> {
      String pattern = "%" + text.toLowerCase() + "%";
      return cb.or(
          cb.like(cb.lower(root.get("title")), pattern),
          cb.like(cb.lower(root.get("description")), pattern));
    };
  }
}
