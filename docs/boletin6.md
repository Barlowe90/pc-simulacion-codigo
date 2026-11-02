# Boletín 6

## Resumen de commits

| # | Mensaje del commit | Qué se hizo | Autor | Enlace |
|---|---|---|---|---|
| 1 | `feat(ansible): añade requisitos de la collection community.docker` | Se instala Ansible `core 2.16.19` mediante `pipx` y la collection `community.docker:3.7.0`; se declara la dependencia en `deploy/requirements.yml`. | Guille | [deba7bd](https://github.com/Barlowe90/pc-simulacion-codigo/commit/deba7bd) |
| 2 | `feat(deploy): protege credenciales con ansible-vault` | Se crea y cifra `deploy/group_vars/servidores/vault.yml` con Ansible Vault (AES-256) para proteger credenciales de GHCR y PostgreSQL. | Guille | [dd06f03](https://github.com/Barlowe90/pc-simulacion-codigo/commit/dd06f03) |

## Detalle por commit

### 1. `feat(ansible): añade requisitos de la collection community.docker` (`deba7bd`)

- **Qué se hizo:** se instaló Ansible `core 2.16.19` mediante `pipx` y se instaló la collection `community.docker:3.7.0` con su dependencia transitiva `community.library_inventory_filtering_v1:1.1.5`. Se creó el fichero `deploy/requirements.yml` para declarar la dependencia de la collection de forma reproducible, de modo que cualquier miembro del equipo pueda instalarla con un único comando.
- **Autor:** Guille.
- **Relación con el boletín:** Parte A — instalación de Ansible y sus dependencias.
- **Uso de IA:** se utilizó **Antigravity (Google DeepMind)** para verificar la instalación, instalar la collection y generar el fichero `deploy/requirements.yml`.

### 2. `feat(deploy): protege credenciales con ansible-vault` (`dd06f03`)

- **Qué se hizo:** creación del archivo de variables cifrado `deploy/group_vars/servidores/vault.yml` mediante Ansible Vault con el algoritmo AES-256. Se definieron las 5 variables sensibles requeridas por el playbook de despliegue (`ghcr_user`, `ghcr_token`, `db_name`, `db_user` y `db_pass`). De este modo, las credenciales quedan versionadas en el repositorio sin exponer secretos en texto claro ni depender de argumentos `--extra-vars` en la línea de comandos.
- **Autor:** Guille.
- **Relación con el boletín:** Parte E — gestión segura de credenciales con Ansible Vault.
- **Uso de IA:** se utilizó **Antigravity (Google DeepMind)** para comprobar la concordancia con las variables usadas en `deploy/deploy.yml`, cifrar el archivo con `ansible-vault` y estructurar la documentación técnica y pasos de evidencia.


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

*Verificación de instalación de Ansible y collection community.docker:*

![Verificación de instalación de Ansible y collection community.docker](imagenes/boletin6/boletin-6-parte-a-ansible-verify.png)



### Parte B (Adri - hecho)

...


### Parte C (Felipe - hecho)

...


### Parte D (Felipe - hecho)

...


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

| Variable | Descripción | Destino en el despliegue |
|---|---|---|
| `ghcr_user` | Usuario de GitHub (`Barlowe90`) | Login en GitHub Container Registry (`docker_login`) |
| `ghcr_token` | Token de acceso personal (PAT con scope `read:packages`) | Descarga de la imagen de la API desde GHCR |
| `db_name` | Nombre de la base de datos (`tareas`) | Creación y conexión a PostgreSQL |
| `db_user` | Usuario de la base de datos (`app`) | Creación y conexión a PostgreSQL |
| `db_pass` | Contraseña del usuario de la base de datos | Creación y conexión a PostgreSQL |

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

*Fichero vault.yml cifrado en el repositorio:*

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
3. **Réplicas de la API (`app1`, `app2`):** autenticación en GitHub Container Registry (`ghcr.io`) con las credenciales del Vault, descarga de la imagen versionada `ghcr.io/Barlowe90/pc-simulacion-codigo:1.0.0`, arranque del contenedor `tareas-api` con `network_mode: host` (apuntando a `db1:5432`) y ejecución del *smoke test* sobre `/actuator/health` hasta confirmar estado `UP`.

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

*Ejecución inicial del playbook y PLAY RECAP:*

![Playbook ejecutado con éxito sobre app1, app2 y db1](imagenes/boletin6/boletin-6-parte-f-playbook-ejecucion.png)


*Verificación funcional de ambas réplicas con curl:*

![Respuesta de la API en app1 (puerto 8081) y app2 (puerto 8082)](imagenes/boletin6/boletin-6-parte-f-curl-replicas.png)


*Segunda ejecución demostrando idempotencia (changed=0):*

![Segunda ejecución del playbook demostrando idempotencia](imagenes/boletin6/boletin-6-parte-f-idempotencia.png)


*Actualización de versión y Rollback inmediato:*

![Despliegue de actualización y Rollback](imagenes/boletin6/boletin-6-parte-f-rollback.png)



### Parte G (Adri)

...


<!-- Incluir capturas de pantalla de lo que no quede reflejado en commits -->
