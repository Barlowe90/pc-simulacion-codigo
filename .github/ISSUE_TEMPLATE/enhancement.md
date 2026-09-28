---
name: ✨ Enhancement / Feature Request
about: Propón una mejora o nueva funcionalidad para la aplicación
title: "[ENHANCEMENT] "
labels: enhancement
assignees: ''
---

## ✨ Descripción de la Mejora

<!-- Describe claramente y de forma concisa la mejora o funcionalidad que propones. -->

## 🎯 Motivación y Contexto

<!-- ¿Por qué es necesaria esta mejora? ¿Qué problema resuelve? -->
<!-- Ejemplo: "Actualmente no es posible filtrar tareas por fecha de vencimiento, lo que dificulta..." -->

## 💡 Solución Propuesta

<!-- Describe la solución que propones. Sé tan detallado como sea posible. -->

## 🔄 Alternativas Consideradas

<!-- ¿Has considerado alternativas? ¿Por qué descartaste las otras opciones? -->

## 📐 Diseño / API Propuesta

<!-- Si aplica, describe el diseño técnico, los cambios de API REST o el modelo de datos. -->

**Nuevo endpoint (si aplica):**
```
POST /api/tasks/...
{
  "campo": "valor"
}
```

**Cambios en el modelo de datos (si aplica):**
```java
// Ejemplo de campo nuevo en Task.java
private LocalDate dueDate;
```

## 🧪 Criterios de Aceptación

<!-- Lista los criterios que deben cumplirse para considerar esta mejora completada. -->

- [ ] ...
- [ ] ...
- [ ] Las pruebas unitarias cubren los nuevos casos.
- [ ] La documentación ha sido actualizada (README, Javadoc, etc.).

## 📊 Impacto Estimado

- **Prioridad:** Alta / Media / Baja
- **Complejidad estimada:** Alta / Media / Baja
- **Componentes afectados:** (e.g., `TaskController`, `TaskService`, `TaskRepository`, modelo `Task`)

## 📝 Contexto Adicional

<!-- Añade cualquier otro contexto, mockups o referencias que puedan ser útiles. -->

## ☑️ Checklist

- [ ] He buscado issues o PRs existentes relacionados con esta mejora.
- [ ] He descrito claramente el problema que esta mejora resuelve.
- [ ] He propuesto al menos una solución posible.
