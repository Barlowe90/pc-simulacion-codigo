Vamos a añadir **filtrado de movimientos por rango de fechas**. Es un cambio pequeño, útil para consultar los gastos e ingresos de un mes y con suficiente lógica para justificar que no es trivial.

Por ejemplo:
```vbnet
GET /api/transactions?from=2026-09-01&to=2026-09-30
```

El comportamiento sería:

- &#x20;Sin parámetros: devuelve todos los movimientos, conservando el funcionamiento actual.&#x20;
- &#x20;Solo `from`: devuelve movimientos desde esa fecha.&#x20;
- &#x20;Solo `to`: devuelve movimientos hasta esa fecha.&#x20;
- &#x20;Ambos: devuelve los movimientos dentro del intervalo, incluyendo sus extremos.&#x20;
- &#x20;Si `from` es posterior a `to`: devuelve `400 Bad Request` con un mensaje claro.&#x20;
- &#x20;Si no hay coincidencias: devuelve una lista vacía.&#x20;

**El cambio afecta a las tres capas:** el controlador recibe los parámetros opcionales, el servicio valida el intervalo y el repositorio realiza la consulta filtrada. No requiere modificar la entidad ni añadir dependencias.

Puedes demostrarlo con estos tests unitarios, usando Mockito sin levantar Spring ni H2:

1. &#x20;Un intervalo invertido genera una excepción y no consulta el repositorio.&#x20;
2. &#x20;Un intervalo válido utiliza la consulta correspondiente y devuelve sus resultados.&#x20;
3. &#x20;Solo fecha inicial aplica el límite inferior.&#x20;
4. &#x20;Solo fecha final aplica el límite superior.&#x20;
5. &#x20;Sin fechas mantiene la consulta de todos los movimientos.&#x20;
6. &#x20;Un intervalo sin resultados devuelve una lista vacía.&#x20;

Ten en cuenta que, al simular el repositorio, estos tests verifican la lógica del servicio y qué consulta solicita; **no demuestran que la consulta real a la base de datos filtre correctamente**.