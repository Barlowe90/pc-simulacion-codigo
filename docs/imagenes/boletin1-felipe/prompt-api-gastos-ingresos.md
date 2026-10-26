Genera un proyecto Maven con Spring Boot 3 y Java 21: una API REST de gestión de gastos e ingresos personales. Usa Spring Data JPA con Hibernate para la persistencia y H2 en memoria como base de datos. Incluye endpoints CRUD, capa de servicio, repositorio JPA, validaciones con Bean Validation, manejo de errores con `@RestControllerAdvice` y tests unitarios con JUnit 5.

**Requisitos mínimos** de la aplicación para considerarse “no trivial”:

- Al menos una entidad de dominio con varios campos y validaciones.
- Persistencia mediante Spring Data JPA/Hibernate.
- Operaciones CRUD completas (crear, leer, actualizar, borrar).
- Separación en capas (controlador / servicio / repositorio).
- Manejo de errores centralizado: un recurso inexistente devuelve 404, no una traza de excepción.
- Al menos 5 tests que comprueben reglas de negocio reales, no simples `assertNotNull`.
- No debe incluir tests de integración que levanten el contexto de la aplicación y/o una base de datos. Solo debe incluir tests unitarios.
- El proyecto debe generar mediante `mvn package` un JAR ejecutable (fat JAR) que incluya todas las dependencias y pueda iniciarse mediante `java -jar target/<nombre>.jar`.

Implementa el proyecto completo, no solo ejemplos o una propuesta de estructura.

### Dominio y validaciones

La entidad principal será `FinancialTransaction`, con los siguientes campos:

- `id`: identificador generado automáticamente.
- `concept`: concepto obligatorio, no vacío y de un máximo de 100 caracteres.
- `description`: descripción opcional de un máximo de 500 caracteres.
- `amount`: importe obligatorio, estrictamente positivo, con un máximo de 12 dígitos enteros y 2 decimales. Utiliza `BigDecimal`, nunca `float` ni `double`.
- `type`: enumeración obligatoria con los valores `INCOME` y `EXPENSE`.
- `date`: fecha obligatoria de tipo `LocalDate`, que no puede ser futura.

Todos los movimientos se expresan en euros. El importe siempre será positivo: el tipo determina si se suma o se resta al calcular el balance. Se permite un balance negativo.

Utiliza Bean Validation para validar las peticiones. Garantiza también que las reglas de importe y fecha se cumplan al invocar directamente el servicio: no dependas exclusivamente de la validación del controlador.

Inyecta un `Clock` en el servicio para comprobar la fecha actual y poder escribir tests deterministas.

### Endpoints

Implementa:

- `POST /api/transactions`: crear un movimiento. Devuelve `201 Created`.
- `GET /api/transactions`: listar todos los movimientos. Si no existen, devuelve una lista vacía.
- `GET /api/transactions/{id}`: consultar un movimiento.
- `PUT /api/transactions/{id}`: actualizar completamente un movimiento existente, manteniendo su identificador.
- `DELETE /api/transactions/{id}`: eliminar un movimiento. Devuelve `204 No Content`.
- `GET /api/transactions/balance`: devolver el total de ingresos, el total de gastos y el balance, calculado como ingresos menos gastos. Si no hay movimientos, los tres importes serán cero.

La creación y la actualización deben aplicar las mismas reglas de validación.

### Arquitectura y errores

- Separa claramente controlador, servicio y repositorio.
- Utiliza un repositorio de Spring Data JPA y una entidad persistente con Hibernate.
- Utiliza DTOs de petición y respuesta para no exponer directamente la entidad JPA.
- Persiste la enumeración como texto.
- Centraliza el manejo de errores mediante `@RestControllerAdvice`.
- Consultar, actualizar o eliminar un movimiento inexistente debe devolver `404 Not Found`.
- Los errores de validación y las infracciones de reglas de negocio deben devolver `400 Bad Request`.
- Devuelve errores en un formato JSON uniforme y comprensible, incluyendo detalles de los campos inválidos cuando corresponda. No expongas trazas de excepciones.

### Tests unitarios obligatorios

Utiliza JUnit 5 y Mockito. Los tests deben ejecutarse sin iniciar Spring ni ninguna base de datos; simula el repositorio y utiliza un reloj fijo.

Incluye, como mínimo, tests que comprueben:

1. Se rechaza un movimiento con importe cero y no se guarda.
2. Se rechaza un movimiento con importe negativo y no se guarda.
3. Se rechaza un importe con más de dos decimales.
4. Se rechaza un movimiento con fecha futura y no se guarda.
5. Se acepta un movimiento válido con fecha de hoy.
6. Actualizar un movimiento con datos inválidos no lo guarda.
7. Consultar un movimiento inexistente genera la excepción correspondiente.
8. Actualizar o eliminar un movimiento inexistente genera la excepción correspondiente.
9. El balance suma ingresos y resta gastos correctamente.
10. Sin movimientos, el balance y los totales son cero.
11. Se permite un balance negativo cuando los gastos superan los ingresos.

Los tests deben comprobar resultados concretos, excepciones e interacciones relevantes; no deben limitarse a `assertNotNull`.

No incluyas tests de integración, `@SpringBootTest`, `@DataJpaTest`, `@WebMvcTest` ni un test generado de tipo `contextLoads`. Puedes comprobar Bean Validation mediante un validador creado directamente, sin contexto de Spring.

### Compilación y entrega

- Configura el plugin Maven de Spring Boot para que `mvn package` ejecute los tests y genere un JAR ejecutable que incluya todas las dependencias.
- El proyecto debe poder iniciarse mediante `java -jar target/<nombre>.jar`.
- Configura H2 en memoria y documenta que los datos se pierden al detener la aplicación.
- Incluye un README con requisitos, estructura del proyecto, reglas de negocio, comandos de compilación, ejecución y tests, y ejemplos `curl` de los endpoints.
- Mantén el alcance sencillo: no añadas autenticación, frontend, Docker ni servicios externos.
- Ejecuta `mvn package` para verificar el proyecto si el entorno lo permite. Si no puedes hacerlo, explica el impedimento y no afirmes que la verificación ha sido satisfactoria.
