# Boletín 6

## Resumen de commits

| #   | Mensaje del commit                                                  | Qué se hizo                                                                                                | Autor                        | Enlace                                                                                                       |
| --- | ------------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------- | ---------------------------- | ------------------------------------------------------------------------------------------------------------ |
| 1   | `feat(ansible): añade requisitos de la collection community.docker` | Declara la colección `community.docker:3.7.0` en `deploy/requirements.yml`.                                | Guille (`guillermolfuentes`) | [deba7bd](https://github.com/Barlowe90/pc-simulacion-codigo/commit/deba7bd3f5c46f434b9c239eb8e1124b9c907475) |
| 2   | `feat: añadido deploy en docker-compose 2 app y 1 db`               | Crea tres servidores simulados con SSH y Docker anidado en `deploy/servidores/docker-compose.yml`.         | Adri (`Barlowe90`)           | [24d95bd](https://github.com/Barlowe90/pc-simulacion-codigo/commit/24d95bd04f85c8c06b13079e7e3b7fe34cd133b2) |
| 3   | `feat(ansible): añadir inventario de servidores para Ansible`       | Crea `deploy/inventory.ini` con los grupos `app`, `db` y `servidores`, y configuración SSH.                | Felipe (`felipehdzc`)        | [af9cd48](https://github.com/Barlowe90/pc-simulacion-codigo/commit/af9cd4861b25a546be4212b8bf8e6ab366b93d75) |
| 4   | `feat(ansible): añadir playbook de despliegue`                      | Crea `deploy/deploy.yml` para preparar Docker, desplegar PostgreSQL y dos réplicas de la API.              | Felipe (`felipehdzc`)        | [03e83c4](https://github.com/Barlowe90/pc-simulacion-codigo/commit/03e83c41da15d3411e8e26056aaef0811328c3f3) |
| 5   | `feat(deploy): protege credenciales con ansible-vault`              | Cifra las credenciales de GHCR y PostgreSQL en `deploy/group_vars/servidores/vault.yml`.                   | Guille (`guillermolfuentes`) | [dd06f03](https://github.com/Barlowe90/pc-simulacion-codigo/commit/dd06f03)                                  |
| 6   | `feat: actualizo realese`                                           | Amplía `.github/workflows/release.yml` con el job de despliegue, runner autoalojado, aprobación y Ansible. | Adri (`Barlowe90`)           | [5fd22ad](https://github.com/Barlowe90/pc-simulacion-codigo/commit/5fd22ad38ce07f273ed58093ec312ef58331893d) |
| 7   | `fix: actualiza credenciales GHCR del despliegue`                   | Actualiza el token de GHCR dentro del Vault cifrado, sin publicar credenciales en claro.                   | Adri (`Barlowe90`)           | [9a460f6](https://github.com/Barlowe90/pc-simulacion-codigo/commit/9a460f66a2dcea5ccf5418700d1e200c0cd49483) |

## Detalle por commit

### 1. `feat(ansible): añade requisitos de la collection community.docker` (`deba7bd`)

- **Qué se hizo:** se instaló Ansible `core 2.16.19` mediante `pipx` y se instaló la collection `community.docker:3.7.0` con su dependencia transitiva `community.library_inventory_filtering_v1:1.1.5`. Se creó el fichero `deploy/requirements.yml` para declarar la dependencia de la collection de forma reproducible, de modo que cualquier miembro del equipo pueda instalarla con un único comando.
- **Autor:** Guille.
- **Relación con el boletín:** Parte A — instalación de Ansible y sus dependencias.
- **Uso de IA:** se utilizó **Antigravity (Google DeepMind)** para verificar la instalación, instalar la collection y generar el fichero `deploy/requirements.yml`.

### 2. `feat: añadido deploy en docker-compose 2 app y 1 db` (`24d95bd`)

- **Qué se hizo:** Se creó `deploy/servidores/docker-compose.yml` con los tres servidores simulados `app1`, `app2` y `db1`, basados en `rastasheep/ubuntu-sshd:18.04`. Cada servidor tiene SSH accesible desde un puerto diferente y `privileged: true` para permitir la ejecución de Docker anidado. Se publicaron además los puertos HTTP `8081` y `8082` para las dos réplicas de la API.
- **Autor:** Adri (`Barlowe90`).
- **Relación con el boletín:** Parte B — preparación de una flota de tres servidores accesibles mediante SSH.
- **Uso de IA:** Se utilizó ChatGPT como apoyo durante la preparación y verificación del entorno Docker y la conectividad de la flota.

### 3. `feat(ansible): añadir inventario de servidores para Ansible`

- **Qué se hizo:** Se creó `deploy/inventory.ini`. Los servidores `app1` y `app2` se incluyeron en el grupo `app`, mientras que `db1` se incluyó en `db`. Ambos grupos se agruparon bajo `servidores`. Se configuraron la dirección de conexión, el puerto SSH de cada servidor, el usuario y las variables comunes. La conexión se verificó con el módulo `ping` de Ansible, obteniendo `pong` en los tres servidores.
- **Autor:** Felipe
- **Relación con el boletín:** Parte C, creación del inventario y comprobación del acceso de Ansible a toda la flota.
- **Uso de IA:** Se utilizó ChatGPT para diagnosticar la incompatibilidad entre Ansible y el Python de los contenedores.

### 4. `feat(ansible): añadir playbook de despliegue`

- **Qué se hizo:** Se creó `deploy/deploy.yml`, organizado en tres plays: preparación de Docker en toda la flota, despliegue de PostgreSQL en el grupo `db` y despliegue de la API en el grupo `app`. Se configuró la imagen de GHCR del proyecto con una versión concreta, el volumen persistente de PostgreSQL, las variables de conexión de Spring Boot y las comprobaciones de disponibilidad. Las tareas que manejan credenciales utilizan `no_log: true`.
- **Autor:** Felipe
- **Relación con el boletín:** Parte D, implementación del playbook de despliegue sobre varios servidores.
- **Uso de IA:** Se utilizó ChatGPT para adaptar el playbook del enunciado al repositorio y la descripción del PR.

### 5. `feat(deploy): protege credenciales con ansible-vault` (`dd06f03`)

- **Qué se hizo:** creación del archivo de variables cifrado `deploy/group_vars/servidores/vault.yml` mediante Ansible Vault con el algoritmo AES-256. Se definieron las 5 variables sensibles requeridas por el playbook de despliegue (`ghcr_user`, `ghcr_token`, `db_name`, `db_user` y `db_pass`). De este modo, las credenciales quedan versionadas en el repositorio sin exponer secretos en texto claro ni depender de argumentos `--extra-vars` en la línea de comandos.
- **Autor:** Guille.
- **Relación con el boletín:** Parte E — gestión segura de credenciales con Ansible Vault.
- **Uso de IA:** se utilizó **Antigravity (Google DeepMind)** para comprobar la concordancia con las variables usadas en `deploy/deploy.yml`, cifrar el archivo con `ansible-vault` y estructurar la documentación técnica y pasos de evidencia.

### 6. `feat: actualizo realese` (`5fd22ad`)

- **Qué se hizo:** Se amplió `.github/workflows/release.yml` para que, después de publicar la imagen en GHCR, un job `deploy` ejecute Ansible en el runner autoalojado. El despliegue se activa por etiquetas de versión, requiere aprobación del entorno `produccion` y utiliza `VAULT_PASS` como secreto de GitHub Actions.
- **Autor:** Adri (`Barlowe90`).
- **Relación con el boletín:** Parte G — automatización del despliegue con GitHub Actions; [PR #35](https://github.com/Barlowe90/pc-simulacion-codigo/pull/35).
- **Uso de IA:** Se utilizó ChatGPT para configurar y comprobar el workflow, el runner y la ejecución de Ansible.

### 7. `fix: actualiza credenciales GHCR del despliegue` (`9a460f6`)

- **Qué se hizo:** Se actualizó la credencial de lectura de GHCR dentro del archivo Ansible Vault cifrado después de detectar un fallo de autenticación. Se verificó el acceso a GHCR y se publicó la versión `v1.1.1`. La segunda ejecución de Release #17 terminó correctamente tras arrancar los contenedores creados en el primer intento.
- **Autor:** Adri (`Barlowe90`).
- **Relación con el boletín:** Parte G — corrección y verificación del despliegue; [PR #37](https://github.com/Barlowe90/pc-simulacion-codigo/pull/37).
- **Uso de IA:** Se utilizó ChatGPT para diagnosticar y comprobar el acceso al registro y la ejecución de Ansible.

## Descripción narrativa de lo hecho en la sesión y relación con los commits

### Parte A (Guille)

#### A.1. Instalación de Ansible

Ansible se instaló mediante **`pipx`**, que aísla el paquete y sus dependencias en un entorno virtual propio sin interferir con el Python del sistema:

```bash
pipx install --include-deps ansible-core
```

Se verificó la versión instalada con:

```bash
~/.local/bin/ansible --version
```

Salida obtenida:

```text
ansible [core 2.16.19]
  config file = None
  configured module search path = ['/Users/guillermo/.ansible/plugins/modules', '/usr/share/ansible/plugins/modules']
  ansible python module location = /Users/guillermo/Library/Application Support/pipx/venvs/ansible-core/lib/python3.12/site-packages/ansible
  ansible collection location = /Users/guillermo/.ansible/collections:/usr/share/ansible/collections
  executable location = /Users/guillermo/.local/bin/ansible
  python version = 3.12.16 (main, Oct 25 2026, 18:24:37) [Clang 21.0.0 (clang-2100.3.34.2)]
  jinja version = 3.1.6
  libyaml = True
```

El ejecutable de Ansible reside en `~/.local/bin/`. Para invocarlo directamente sin la ruta completa se añadió esa carpeta al `PATH`:

```bash
echo 'export PATH="$HOME/.local/bin:$PATH"' >> ~/.zshrc && source ~/.zshrc
```

#### A.2. Instalación de la collection `community.docker`

La collection `community.docker` aporta los módulos `docker_container`, `docker_image`, `docker_login` y demás módulos usados en los playbooks del boletín. Se instaló la versión exacta indicada en el enunciado:

```bash
ansible-galaxy collection install community.docker:3.7.0
```

Salida obtenida:

```text
Starting galaxy collection install process
Process install dependency map
Starting collection install process
Downloading https://galaxy.ansible.com/api/v3/.../community-docker-3.7.0.tar.gz
Installing 'community.docker:3.7.0' to '/Users/guillermo/.ansible/collections/ansible_collections/community/docker'
community.docker:3.7.0 was installed successfully
Installing 'community.library_inventory_filtering_v1:1.1.5' to '...'
community.library_inventory_filtering_v1:1.1.5 was installed successfully
```

Se verificó que la collection quedó instalada correctamente:

```bash
ansible-galaxy collection list | grep community.docker
```

Salida:

```text
community.docker                         3.7.0
```

#### A.3. Declarar la dependencia en el repositorio

Para que la instalación sea reproducible en cualquier entorno (máquina de otro compañero, runner de CI), se creó el fichero `deploy/requirements.yml`:

```yaml
collections:
    - name: community.docker
      version: "3.7.0"
# se instala con:  ansible-galaxy collection install -r deploy/requirements.yml
```

Se verificó que el fichero funciona correctamente instalando desde él:

```bash
ansible-galaxy collection install -r deploy/requirements.yml
```

Salida (idempotente: la collection ya estaba instalada):

```text
Starting galaxy collection install process
Nothing to do. All requested collections are already installed.
```

#### Evidencias

_Verificación de instalación de Ansible y collection community.docker:_

![Verificación de instalación de Ansible y collection community.docker](imagenes/boletin6/boletin-6-parte-a-ansible-verify.png)

### Parte B (Adri) — Preparación de la flota de servidores destino

#### B.1. Infraestructura simulada con Docker Compose

Se creó `deploy/servidores/docker-compose.yml` para simular tres servidores Linux independientes que Ansible administra mediante SSH: `app1` y `app2` para las dos réplicas de Spring Boot, y `db1` para PostgreSQL. El archivo se añadió en el [commit `24d95bd`](https://github.com/Barlowe90/pc-simulacion-codigo/commit/24d95bd04f85c8c06b13079e7e3b7fe34cd133b2) y se integró mediante el [PR #30](https://github.com/Barlowe90/pc-simulacion-codigo/pull/30).

Se empleó la imagen `rastasheep/ubuntu-sshd:18.04`, que incorpora un servidor SSH. Los contenedores tienen `privileged: true`, necesario en esta práctica para ejecutar posteriormente Docker dentro de cada servidor simulado. Este archivo representa la **infraestructura destino** y es independiente del Compose de la aplicación del Boletín 3.

| Servidor | Rol                       | Puerto SSH anfitrión → contenedor | Puerto HTTP anfitrión → contenedor |
| -------- | ------------------------- | --------------------------------- | ---------------------------------- |
| `app1`   | Primera réplica de la API | `2223 → 22`                       | `8081 → 8080`                      |
| `app2`   | Segunda réplica de la API | `2224 → 22`                       | `8082 → 8080`                      |
| `db1`    | Base de datos PostgreSQL  | `2225 → 22`                       | No publicado al anfitrión          |

#### B.2. Arranque y comprobación de conectividad SSH

Desde Ubuntu WSL2 en la torre, con Docker Desktop y la integración WSL habilitados, se levantaron los tres servidores:

```bash
docker compose -f deploy/servidores/docker-compose.yml up -d
docker ps
```

Después se comprobó el acceso SSH a cada servidor con el usuario `root` y el puerto correspondiente. En esta imagen de práctica, la contraseña inicial del usuario `root` es `root`:

```bash
ssh root@localhost -p 2223   # app1
ssh root@localhost -p 2224   # app2
ssh root@localhost -p 2225   # db1
```

Las conexiones SSH permiten que Ansible trabaje contra tres destinos diferenciados, aunque todos se ejecuten como contenedores en la misma torre. Más adelante se verificó también el acceso de Ansible a los tres servidores con el módulo `ping` (Parte C).

#### Evidencias

_Conexión SSH a los servidores simulados:_

![Prueba de conexión SSH a la flota de servidores](imagenes/boletin6/boletin-6-parte-b-conexion-ssh.png)

**Uso de IA:** Se utilizó ChatGPT como apoyo para configurar y comprobar la infraestructura simulada y su conectividad SSH.

### Parte C (Felipe)

En esta parte, commit [af9cd48](https://github.com/Barlowe90/pc-simulacion-codigo/commit/af9cd4861b25a546be4212b8bf8e6ab366b93d75), añadí el inventario `deploy/inventory.ini`.

Se han separado las máquinas según su función. El grupo `app` contiene `app1` y `app2`, y el grupo `db` contiene `db1`. El grupo `servidores` reúne a ambos para poder aplicar tareas comunes a toda la flota.

Todos los destinos utilizan `ansible_host=localhost`, porque Ansible se ejecuta en WSL y accede a cada contenedor mediante un puerto SSH diferente. También se definieron el intérprete `/usr/bin/python3`, el usuario y las opciones de conexión comunes.

La primera comprobación con Ansible falló con el siguiente error:

```text
SyntaxError: future feature annotations is not defined
```

Para localizar la causa comprobé las versiones:

```bash
ansible --version
python3 --version
docker exec app1 python3 --version
```

En WSL tenía Ansible Core `2.21.5`, ejecutándose con Python `3.14.4`, mientras que el contenedor utilizaba Python `3.6.5`. La versión de Ansible instalada no era compatible con ese Python remoto.

Para seguir la versión indicada en el boletín preparé un entorno con Python 3.12 y Ansible Core 2.16.x:

```bash
pipx install uv
uv venv --python 3.12 ~/.venvs/ansible216
uv pip install --python ~/.venvs/ansible216/bin/python 'ansible-core>=2.16,<2.17'
source ~/.venvs/ansible216/bin/activate
```

Con el entorno activado, instalé la colección declarada en el proyecto y repetí la comprobación:

```bash
ansible-galaxy collection install -r deploy/requirements.yml
ansible -i deploy/inventory.ini servidores -m ping
```

Esta vez al intentar hacer ping a toda la flota obtuve:

![Ping a la flota](imagenes/boletin6/boletín-6-parte-C-pingPong.png)

El resultado confirmó que Ansible podía conectarse por SSH y ejecutar su módulo en los tres servidores.

### Parte D (Felipe)

Para realizar esta parte, commit [03e83c4](https://github.com/Barlowe90/pc-simulacion-codigo/commit/03e83c41da15d3411e8e26056aaef0811328c3f3), añadí el playbook `deploy/deploy.yml`.

El playbook se divide en tres bloques que se ejecutan en orden:

| Play                 | Grupo destino | Tareas principales                                                                           |
| -------------------- | ------------- | -------------------------------------------------------------------------------------------- |
| Preparar Docker      | `servidores`  | Instalar `docker.io` y `python3-docker`, y arrancar `dockerd`.                               |
| Desplegar PostgreSQL | `db`          | Arrancar `postgres:16` con un volumen persistente y esperar a que acepte conexiones.         |
| Desplegar la API     | `app`         | Autenticarse en GHCR, descargar la imagen, arrancar la API y consultar su endpoint de salud. |

En el primer bloque se instala Docker en cada servidor simulado. Como estos contenedores no utilizan systemd, el demonio se arranca con `nohup` y se espera a que `docker info` responda. La opción `creates` evita repetir la tarea de arranque mientras exista el archivo `/var/run/docker.pid`. También se establece `cache_valid_time: 3600` para reutilizar durante una hora la caché de paquetes.

El segundo bloque define el contenedor `tareas-db`, basado en `postgres:16`. El volumen `tareas-db-data` guarda los datos de PostgreSQL. El usuario, la contraseña y el nombre de la base se reciben mediante las variables `db_user`, `db_pass` y `db_name`. Se configura una comprobación con `pg_isready` y una espera sobre el puerto `5432` antes de continuar.

En el tercer bloque adapté el nombre de la imagen al que publica el workflow de release del repositorio:

```yaml
version_app: "1.0.0"
imagen: "ghcr.io/barlowe90/pc-simulacion-codigo:{{ version_app }}"
host_bd: "{{ groups['db'][0] }}"
```

La variable `version_app` selecciona una versión concreta de la imagen y puede sobrescribirse en los despliegues posteriores. La variable `host_bd` toma el primer servidor del grupo `db`, que en este inventario es `db1`.

Ambas réplicas reciben las variables de entorno de Spring Boot para conectarse a la misma base de datos. La URL se construye de esta forma:

```text
jdbc:postgresql://db1:5432/<nombre_de_la_base>
```

Los contenedores desplegados utilizan `network_mode: host` respecto al Docker que corre dentro de cada servidor simulado. Así, la API comparte la red de `app1` o `app2`, y PostgreSQL comparte la de `db1`. Los servidores exteriores pertenecen a la red común de Compose, donde `db1` es el nombre del servicio de base de datos.

Para la API se utilizan `state: started`, `restart_policy: always` y `recreate: false`. Esta configuración evita forzar una recreación en cada ejecución cuando el contenedor ya coincide con el estado solicitado. La comprobación práctica de la idempotencia mediante ejecuciones repetidas corresponde a la parte F.

El último paso del playbook consulta `http://localhost:8080/actuator/health` desde cada servidor de aplicación y espera una respuesta HTTP `200` con estado `UP`.

Las credenciales de GHCR y PostgreSQL se referencian mediante variables. Las tareas que las manejan incluyen `no_log: true`. Su configuración con Ansible Vault corresponde a la parte E.

Los dos commits se enviaron a la rama `feat/ansible-inventario-playbook` y se presentó el [PR #32](https://github.com/Barlowe90/pc-simulacion-codigo/pull/32) hacia `main`.

Las comprobaciones realizadas en esta sesión fueron el arranque de la flota, el acceso SSH y el `ping` de Ansible a los tres servidores. El despliegue completo, las pruebas de la API, la idempotencia, el cambio de versión y el rollback quedan para la parte F, una vez preparadas las credenciales de la parte E. La automatización desde GitHub Actions corresponde a la parte G.

### Parte E (Guille)

#### E.1. Ubicación de variables en Ansible (`group_vars`)

Ansible incorpora un mecanismo canónico para asociar variables a grupos de máquinas: cualquier fichero YAML ubicado en la ruta `deploy/group_vars/<nombre_grupo>/` se carga automáticamente para todos los hosts pertenecientes a dicho grupo.

Dado que en `deploy/inventory.ini` se definió el grupo paraguas `[servidores:children]` (que engloba a `app` y `db`), se ubicó el archivo cifrado en:

```text
deploy/group_vars/servidores/vault.yml
```

Así, tanto las máquinas de aplicación (`app1`, `app2`) como la de base de datos (`db1`) disponen de acceso a sus respectivas credenciales durante la ejecución del playbook.

#### E.2. Variables protegidas

El archivo contiene las 5 credenciales necesarias para los tres plays de `deploy/deploy.yml`:

| Variable     | Descripción                                              | Destino en el despliegue                            |
| ------------ | -------------------------------------------------------- | --------------------------------------------------- |
| `ghcr_user`  | Usuario de GitHub (`Barlowe90`)                          | Login en GitHub Container Registry (`docker_login`) |
| `ghcr_token` | Token de acceso personal (PAT con scope `read:packages`) | Descarga de la imagen de la API desde GHCR          |
| `db_name`    | Nombre de la base de datos (`tareas`)                    | Creación y conexión a PostgreSQL                    |
| `db_user`    | Usuario de la base de datos (`app`)                      | Creación y conexión a PostgreSQL                    |
| `db_pass`    | Contraseña del usuario de la base de datos               | Creación y conexión a PostgreSQL                    |

#### E.3. Creación y cifrado con Ansible Vault (AES-256)

Se creó y cifró el fichero mediante `ansible-vault` empleando el estándar de cifrado simétrico AES-256:

```bash
ansible-vault create deploy/group_vars/servidores/vault.yml
```

Para modificar el archivo en el futuro manteniendo el cifrado se utiliza:

```bash
ansible-vault edit deploy/group_vars/servidores/vault.yml
```

Y para consultar su contenido sin abrir el editor:

```bash
ansible-vault view deploy/group_vars/servidores/vault.yml
```

#### E.4. Verificación de la seguridad en el repositorio

Se verificó con el comando `cat` que el archivo almacenado en el repositorio Git es completamente ilegible y está protegido con la cabecera `$ANSIBLE_VAULT;1.1;AES256`:

```bash
cat deploy/group_vars/servidores/vault.yml
```

Los tokens utilizados durante las pruebas fueron revocados posteriormente

#### Evidencias

_Fichero vault.yml cifrado en el repositorio:_

![Contenido cifrado de vault.yml mediante AES256](imagenes/boletin6/boletin-6-parte-e-vault-cifrado.png)

### Parte F (Guille)

#### F.1. Arranque de la infraestructura simulada

Antes de ejecutar el playbook es necesario que los tres contenedores que hacen de servidores destino (`app1`, `app2`, `db1`) estén levantados y escuchando por SSH:

```bash
docker compose -f deploy/servidores/docker-compose.yml up -d
```

#### F.2. Lanzamiento del despliegue con Ansible Vault

Se invocó el playbook indicando el inventario, solicitando interactivamente la clave maestra del almacén seguro (`--ask-vault-pass`) e inyectando la versión semántica de la aplicación a desplegar:

```bash
ansible-playbook -i deploy/inventory.ini deploy/deploy.yml \
  --ask-vault-pass --extra-vars "version_app=1.0.0"
```

El despliegue transcurre en tres fases orquestadas secuencialmente:

1. **Flota completa (`servidores`):** instalación de `docker.io` y arranque del demonio `dockerd` en segundo plano en cada contenedor.
2. **Base de datos (`db1`):** arranque del contenedor `tareas-db` (PostgreSQL 16) con volumen persistente `tareas-db-data` y comprobación de salud mediante `pg_isready`.
3. **Réplicas de la API (`app1`, `app2`):** autenticación en GitHub Container Registry (`ghcr.io`) con las credenciales del Vault, descarga de la imagen versionada `ghcr.io/Barlowe90/pc-simulacion-codigo:1.0.0`, arranque del contenedor `tareas-api` con `network_mode: host` (apuntando a `db1:5432`) y ejecución del _smoke test_ sobre `/actuator/health` hasta confirmar estado `UP`.

#### F.3. Verificación de alta disponibilidad y réplicas con cURL

Una vez finalizado el playbook se comprobó el correcto funcionamiento de ambas réplicas, las cuales responden en los puertos mapeados al anfitrión (`8081` para `app1` y `8082` para `app2`), accediendo de forma compartida al mismo backend de base de datos en `db1`:

```bash
# Comprobación de salud en ambas réplicas
curl -i http://localhost:8081/actuator/health
curl -i http://localhost:8082/actuator/health

# Comprobación del endpoint funcional de tareas
curl -i http://localhost:8081/api/v1/tasks
curl -i http://localhost:8082/api/v1/tasks
```

Ambas réplicas devuelven código `200 OK` y estado `{"status":"UP"}`.

#### F.4. Verificación de idempotencia (`changed=0`)

Una propiedad fundamental de Ansible es la **idempotencia**: ejecutar el mismo playbook repetidas veces sobre un sistema en el estado deseado no debe alterar nada ni provocar efectos secundarios.

Para comprobarlo, se reejecutó exactamente el mismo comando:

```bash
ansible-playbook -i deploy/inventory.ini deploy/deploy.yml \
  --ask-vault-pass --extra-vars "version_app=1.0.0"
```

En la salida final (`PLAY RECAP`), todas las tareas reportaron estado `ok` y **`changed=0`** en las tres máquinas (`app1`, `app2`, `db1`), confirmando que Ansible verificó el estado actual y no realizó operaciones redundantes (el demonio dockerd ya corría, la base de datos ya existía y los contenedores de la API ya ejecutaban la versión solicitada).

#### F.5. Actualización de versión y Rollback inmediato

Se probó el ciclo de vida continuo mediante versionado inmutable:

1. **Actualización:** se desplegó una versión incremental (`version_app=1.1.0`):
    ```bash
    ansible-playbook -i deploy/inventory.ini deploy/deploy.yml \
      --ask-vault-pass --extra-vars "version_app=1.1.0"
    ```
    Ansible detectó que el tag de la imagen cambió, descargó la nueva versión de GHCR y recreó únicamente los contenedores `tareas-api` en `app1` y `app2` sin interrumpir la base de datos `db1`.
2. **Rollback:** ante cualquier problema en producción, se ejecutó la reversión inmediata volviendo a especificar la versión estable anterior:
    ```bash
    ansible-playbook -i deploy/inventory.ini deploy/deploy.yml \
      --ask-vault-pass --extra-vars "version_app=1.0.0"
    ```
    En menos de un minuto, ambas réplicas volvieron a quedar ejecutando la versión `1.0.0` demostrando la inmutabilidad de los artefactos y la seguridad del despliegue declarativo.

#### Evidencias

_Ejecución inicial del playbook y PLAY RECAP:_

![Playbook ejecutado con éxito sobre app1, app2 y db1](imagenes/boletin6/boletin-6-parte-f-playbook-ejecucion.png)

_Verificación funcional de ambas réplicas con curl:_

![Respuesta de la API en app1 (puerto 8081) y app2 (puerto 8082)](imagenes/boletin6/boletin-6-parte-f-curl-replicas.png)

_Segunda ejecución demostrando idempotencia (changed=0):_

![Segunda ejecución del playbook demostrando idempotencia](imagenes/boletin6/boletin-6-parte-f-idempotencia.png)

_Actualización de versión y Rollback inmediato:_

![Despliegue de actualización y Rollback](imagenes/boletin6/boletin-6-parte-f-rollback.png)

### Parte G (Adri)

#### G.1. Registro y configuración del runner autoalojado

Se registró un runner **self-hosted** de GitHub Actions llamado `torre`, con etiquetas Linux/X64, en Ubuntu WSL2. Se utilizó este equipo porque los servidores simulados (`app1`, `app2` y `db1`) se ejecutan localmente en Docker y no son accesibles directamente desde los runners alojados en GitHub.

El runner se inicia desde `~/actions-runner` mediante `./run.sh` y permanece conectado para recibir el job `deploy`. En WSL2 se dispone de Ansible, `sshpass`, la colección `community.docker` y los tres servidores Docker con sus puertos SSH publicados. De este modo, Ansible puede administrar la flota mediante `deploy/inventory.ini`.

_Configuración del runner en el equipo anfitrión:_

![Configuración del runner self-hosted en el PC](imagenes/boletin6/boletin-6-parte-g-confi-runner-pc.png)

_Runner conectado y preparado para ejecutar trabajos:_

![Runner self-hosted conectado a GitHub Actions](imagenes/boletin6/boletin-6-parte-g-runner-completado.png)

#### G.2. Entorno protegido `produccion` y secreto de Ansible Vault

Se configuró el entorno **`produccion`** en GitHub, con Guillermo (`guillermolfuentes`) como revisor obligatorio. El job de despliegue queda bloqueado hasta que este aprueba expresamente la ejecución. También se creó el secreto del entorno **`VAULT_PASS`**, que contiene la contraseña maestra necesaria para descifrar `deploy/group_vars/servidores/vault.yml` durante el despliegue, sin almacenarla en el workflow.

_Configuración del entorno de producción y protección de despliegues:_

![Entorno produccion con aprobación requerida](imagenes/boletin6/boletin-6-parte-g-entorno-produccion.png)

#### G.3. Automatización del despliegue en `release.yml`

Se amplió el workflow `.github/workflows/release.yml` mediante el [PR #35](https://github.com/Barlowe90/pc-simulacion-codigo/pull/35), manteniendo el job `publish` y añadiendo el job `deploy`:

- **`publish`** construye y publica la imagen de la aplicación en GitHub Container Registry (GHCR). Expone la versión semántica publicada mediante `outputs.version`.
- **`deploy`** depende de `publish` (`needs: publish`), se ejecuta únicamente al publicar un tag `vX.Y.Z`, utiliza el runner `self-hosted` y requiere aprobación del entorno `produccion`.
- Tras la aprobación, el runner instala las colecciones declaradas en `deploy/requirements.yml` y ejecuta `deploy/deploy.yml` con el inventario de la flota, el secreto `VAULT_PASS` mediante un archivo temporal y la versión recién publicada como `version_app`. El archivo temporal se elimina al finalizar.

_Ejecución de Ansible desde GitHub Actions:_

![Job deploy ejecutando Ansible en GitHub Actions](imagenes/boletin6/boletin-6-parte-g-ansible-github-actions.png)

#### G.4. Prueba extremo a extremo y comprobaciones funcionales

Se publicaron los tags `v1.1.0` y `v1.1.1`. En las primeras ejecuciones se detectaron problemas de autenticación en GHCR y de arranque inicial de los contenedores de la API. La credencial de GHCR se actualizó dentro del Vault cifrado mediante el [PR #37](https://github.com/Barlowe90/pc-simulacion-codigo/pull/37). Una vez arrancados los contenedores creados durante el primer intento, se volvió a ejecutar el job fallido de `v1.1.1`.

La segunda ejecución de **[Release #17 (`v1.1.1`)](https://github.com/Barlowe90/pc-simulacion-codigo/actions/runs/41640250025)** terminó correctamente, con los jobs `publish` y `deploy` en estado **success**. El despliegue quedó validado sobre los contenedores existentes; la necesidad de intervenir en el arranque inicial se documenta como una limitación de esta prueba.

Para verificar las dos réplicas de la API se realizaron las siguientes peticiones desde Ubuntu WSL2:

```bash
curl -i http://localhost:8081/actuator/health
curl -i http://localhost:8082/actuator/health
curl -i http://localhost:8081/api/v1/tasks
curl -i http://localhost:8082/api/v1/tasks
```

Las cuatro peticiones devolvieron **HTTP 200**. Los endpoints de salud respondieron `{"status":"UP"}` y ambos endpoints funcionales devolvieron una respuesta JSON válida (lista paginada vacía, al no haber tareas registradas).

_Comprobaciones funcionales en las dos réplicas:_

![Peticiones curl con HTTP 200 en app1 y app2](imagenes/boletin6/boletin-6-parte-g-comprobaciones-funcionales.png)

##### Diagrama de arquitectura del despliegue

El siguiente diagrama representa el recorrido desde el `git push` de una etiqueta hasta las dos réplicas desplegadas: repositorio y Actions en GitHub, GHCR, aprobación y secreto del entorno `produccion`, runner y controlador Ansible en la torre WSL2, Docker anfitrión y los tres contenedores que simulan servidores (`app1`, `app2` y `db1`). En cada servidor se ejecuta Docker anidado; `app1` y `app2` alojan la API Spring Boot, mientras que `db1` aloja PostgreSQL.

![Arquitectura completa del despliegue con GitHub Actions, Ansible y Docker](imagenes/boletin6/boletin-6-parte-g-arquitectura.png)

**Uso de IA:** Se utilizó ChatGPT para orientar la configuración del runner y del workflow, diagnosticar los errores de despliegue y ayudar a documentar las evidencias de la Parte G.
