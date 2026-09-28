# Contribuir al proyecto

Gracias por tu interés en contribuir al proyecto.

## Preparar el entorno de desarrollo

### Requisitos

Es necesario disponer de:

- Git 2.x
- JDK 21
- Maven 3.9.x

### Clonar el repositorio

```bash
git clone https://github.com/Barlowe90/pc-simulacion-codigo.git
cd pc-simulacion-codigo
```

### Construir el proyecto

Compila el proyecto, ejecuta los tests y genera el JAR:

```bash
mvn clean package
```

El JAR ejecutable se generará en:

```text
target/task-manager-1.0.0.jar
```

### Arrancar la aplicación

```bash
java -jar target/task-manager-1.0.0.jar
```

La API estará disponible en:

```text
http://localhost:8080
```

## Hooks de Git

Los hooks del proyecto están versionados en `.githooks/`.

Después de clonar el repositorio, hay que activarlos una sola vez mediante:

```bash
git config core.hooksPath .githooks
```

Para comprobar que están correctamente configurados:

```bash
git config core.hooksPath
```

El resultado esperado es:

```text
.githooks
```

El hook de `pre-commit` ejecuta Spotless para aplicar automáticamente el formato del código antes de crear el commit.

También se puede comprobar o aplicar manualmente el formato:

```bash
mvn spotless:check
mvn spotless:apply
```

## Flujo de trabajo

No se debe trabajar directamente sobre `main`.

Antes de comenzar un nuevo cambio, actualiza la rama principal:

```bash
git switch main
git pull --ff-only
```

Después crea una rama específica para el cambio.

Para una nueva funcionalidad:

```bash
git switch -c feat/nombre-de-la-funcionalidad
```

Para corregir un error:

```bash
git switch -c fix/nombre-del-error
```

Realiza los cambios necesarios y añade o actualiza los tests correspondientes.

Cuando el trabajo esté preparado:

```bash
git add .
git commit -m "tipo(ámbito): descripción del cambio"
git push -u origin nombre-de-la-rama
```

## Convención de commits

El proyecto utiliza Conventional Commits:

```text
tipo(ámbito): descripción
```

Tipos habituales:

- `feat`: nueva funcionalidad.
- `fix`: corrección de errores.
- `test`: cambios en tests.
- `refactor`: refactorización.
- `docs`: documentación.
- `chore`: configuración o tareas de mantenimiento.
- `ci`: integración continua.

Ejemplo:

```text
feat(tasks): añade filtro de búsqueda por prioridad
```

## Issues

Antes de implementar una mejora o corregir un error, crea un Issue utilizando la plantilla correspondiente:

- `bug`: errores o comportamientos inesperados.
- `enhancement`: mejoras o nuevas funcionalidades.

## Pull Requests

Los cambios deben integrarse en `main` mediante Pull Request.

Al crear el PR:

1. Utiliza la plantilla de Pull Request del repositorio.
2. Describe claramente qué hace el cambio.
3. Enlaza el Issue correspondiente mediante `Closes #N`.
4. Indica cómo se ha probado.
5. Solicita la revisión correspondiente.
6. Responde y resuelve los comentarios de revisión antes de fusionar.

## Política de revisión

Los Pull Requests requieren la revisión de otro miembro del equipo.

Los propietarios definidos en `.github/CODEOWNERS` serán responsables de revisar los cambios correspondientes.

No debe fusionarse un Pull Request mientras existan conversaciones de revisión pendientes.

## Política de fusión

Se utilizará **Squash and merge**.

Cada Pull Request se integrará en `main` como un único commit que represente el cambio completo realizado.

Esta estrategia permite mantener un historial de `main` sencillo y legible, siguiendo la relación:

```text
1 Pull Request = 1 cambio con significado = 1 commit en main
```

Una vez fusionado el Pull Request se eliminará la rama correspondiente.

## Tests

Antes de abrir un Pull Request hay que comprobar que el proyecto compila y que todos los tests pasan correctamente:

```bash
mvn clean package
```

También debe comprobarse el formato:

```bash
mvn spotless:check
```

El Pull Request debe incluir tests nuevos o actualizados cuando el cambio realizado lo requiera.

## Después del merge

Una vez fusionado el Pull Request:

1. Elimina la rama de feature si ya no es necesaria.
2. Actualiza tu rama `main` local:

```bash
git switch main
git pull --ff-only
```
