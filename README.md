# Task Manager API

API REST de gestión de tareas construida con **Spring Boot 3** y **Java 21**, gestionada con Maven. Proyecto desarrollado como parte del [Boletín 1: Git y proyecto Maven](https://practicas-continuas-umu.github.io/apuntes/practicas/boletin1-git-maven.html) de Prácticas Continuas UMU.

## Descripción

La aplicación expone una API REST completa para gestionar tareas (*To-Do*). Cada tarea tiene:

- `id` — identificador único generado automáticamente
- `title` — título (obligatorio)
- `description` — descripción opcional
- `status` — estado: `TODO`, `IN_PROGRESS`, `DONE`
- `priority` — prioridad: `LOW`, `MEDIUM`, `HIGH`
- `dueDate` — fecha límite

### Características

- **CRUD completo**: crear, listar (con filtros), obtener, actualizar y borrar tareas.
- **Filtrado**: por estado, prioridad y fecha límite mediante parámetros de query.
- **Paginación y ordenación**: listado por páginas configurable y compatible con los filtros.
- **Validaciones de negocio**: no se puede crear una tarea con fecha límite pasada, ni realizar transiciones de estado inválidas.
- **Manejo de errores centralizado**: respuestas `404` y `400` con cuerpo JSON estructurado (sin trazas de excepción).
- **Persistencia en memoria**: H2 embebido; los datos se pierden al reiniciar (sin base de datos externa).
- **Formateo automático**: Spotless con Google Java Format, aplicado en cada commit mediante un hook de pre-commit.
- **Tests unitarios**: cobertura de reglas de negocio reales en servicio y repositorio (sin tests de integración que levanten contexto Spring).

### Estructura del proyecto

```
src/
├── main/java/com/tasks/taskmanager/
│   ├── TaskManagerApplication.java      # Punto de entrada
│   ├── controller/TaskController.java   # Endpoints REST
│   ├── domain/                          # Entidades y enumerados
│   ├── dto/                             # Objetos de transferencia (request/response/filtros)
│   ├── exception/                       # Manejo global de errores
│   ├── repository/                      # Repositorio JPA + Specification para filtros
│   └── service/                         # Lógica de negocio
└── test/java/com/tasks/taskmanager/
    ├── repository/TaskSpecificationTest.java
    └── service/TaskServiceImplTest.java
```

---

## Requisitos previos

| Herramienta | Versión mínima | Comprobación   |
|-------------|----------------|----------------|
| Git         | 2.x            | `git --version` |
| JDK         | 21             | `java -version` |
| Maven       | 3.9.x          | `mvn -version`  |

> **Tip:** Si usas VS Code, instala la extensión [EditorConfig](https://marketplace.visualstudio.com/items?itemName=EditorConfig.EditorConfig) para que se apliquen automáticamente las reglas de `.editorconfig`.

---

## Cómo construir

```bash
# 1. Clona el repositorio
git clone https://github.com/Barlowe90/pc-simulacion-codigo.git
cd pc-simulacion-codigo

# 2. Compila, ejecuta los tests y genera el fat JAR
mvn clean package
```

El JAR ejecutable quedará en `target/task-manager-1.0.0.jar`.

Para **solo verificar o aplicar el formato** sin compilar:

```bash
mvn spotless:check   # falla si hay código sin formatear
mvn spotless:apply   # aplica el formato automáticamente
```

---

## Cómo arrancar

```bash
java -jar target/task-manager-1.0.0.jar
```

La API estará disponible en `http://localhost:8080`.

### Ejemplos de uso con curl

```bash
# Crear una tarea
curl -s -X POST http://localhost:8080/api/tasks \
     -H "Content-Type: application/json" \
     -d '{"title":"Mi primera tarea","description":"Descripción","priority":"HIGH","dueDate":"2099-12-31"}'

# Listar la primera página con un máximo de 10 tareas
curl -s "http://localhost:8080/api/v1/tasks?page=0&size=10"

# Combinar la paginación con el filtro por estado
curl -s "http://localhost:8080/api/v1/tasks?status=PENDING&page=0&size=5"

# Obtener una tarea por id
curl -s http://localhost:8080/api/tasks/1

# Actualizar una tarea
curl -s -X PUT http://localhost:8080/api/tasks/1 \
     -H "Content-Type: application/json" \
     -d '{"title":"Tarea actualizada","status":"IN_PROGRESS","priority":"MEDIUM","dueDate":"2099-12-31"}'

# Borrar una tarea
curl -s -X DELETE http://localhost:8080/api/tasks/1
```

---

## Activar los hooks de Git (pre-commit con Spotless)

Los hooks están versionados en `.githooks/` para que todo el equipo los comparta. Sin embargo, Git **no los activa automáticamente** al clonar: cada persona debe ejecutar este comando **una sola vez** tras clonar el repositorio:

```bash
git config core.hooksPath .githooks
```

A partir de ese momento, cada `git commit` ejecutará automáticamente `mvn spotless:apply` y re-añadirá los archivos reformateados antes de confirmar el commit.

### Verificar que el hook está activo

```bash
git config core.hooksPath   # debe mostrar: .githooks
```

> **Nota:** El hook vive en `.githooks/pre-commit`. El directorio `.git/hooks/` no se versiona, por eso se usa `.githooks/` apuntado con `core.hooksPath`: así el hook viaja con el repositorio.
>
> **Aviso:** Un hook local **nunca sustituye** a la comprobación en CI. Siempre se puede saltar con `git commit --no-verify`.

---

## Convención de commits

Este proyecto sigue [Conventional Commits](https://www.conventionalcommits.org/):

```
tipo(ámbito): descripción en imperativo
```

| Tipo       | Cuándo usarlo                                       |
|------------|-----------------------------------------------------|
| `feat`     | Nueva funcionalidad                                 |
| `fix`      | Corrección de un error                              |
| `test`     | Añadir o corregir tests                             |
| `refactor` | Cambio interno sin nueva funcionalidad ni fix       |
| `docs`     | Documentación                                       |
| `chore`    | Herramientas de build, configuración, dependencias  |
| `ci`       | Cambios en el pipeline de CI/CD                     |

Ejemplo: `feat(tasks): añade filtro de búsqueda por prioridad`
prueba
