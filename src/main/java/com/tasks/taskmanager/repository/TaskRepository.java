package com.tasks.taskmanager.repository;

import com.tasks.taskmanager.domain.Task;
import com.tasks.taskmanager.domain.TaskPriority;
import com.tasks.taskmanager.domain.TaskStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Repositorio JPA para la entidad Task. Extiende {@link JpaSpecificationExecutor} para soportar
 * consultas dinámicas construidas con el patrón Specification (Criteria API).
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {

  /** Filtra por estado. */
  List<Task> findByStatus(TaskStatus status);

  /** Filtra por prioridad. */
  List<Task> findByPriority(TaskPriority priority);
}
