# Serenity BDD — Pruebas automatizadas InvSystem

Proyecto de automatización E2E para el sistema **InvSystem**, construido con **Serenity BDD + Cucumber + Selenium WebDriver**.

Cubre 7 historias de usuario con 50+ escenarios de prueba organizados por módulo.

---

## Requisitos previos

| Herramienta | Versión mínima |
|-------------|----------------|
| Java        | 17             |
| Maven       | 3.8+           |

> **Chrome** es descargado automáticamente por Selenium Manager en la primera ejecución.

> **WSL**: Las librerías de sistema que Chrome necesita están incluidas en `.libs/`. El `pom.xml` las configura automáticamente.

---

## Requisito: aplicación corriendo

Los tests apuntan a `http://localhost:4200` (frontend) y `http://localhost:3000` (backend).
Ambos deben estar activos antes de ejecutar.

```bash
# Desde la raíz del monorepo
cd ~/invetarioMonoRepoMCP
pnpm run dev
```

---

## Ejecutar los tests

### Todos los tests

```bash
cd ~/Projects-Test/serenity-inventario-login
mvn verify
```

### Por módulo (tags)

```bash
# HU-01 y HU-02 — Autenticación (login y logout)
mvn verify -Dcucumber.filter.tags="@autenticacion"

# HU-03 — Roles del sistema
mvn verify -Dcucumber.filter.tags="@roles"

# HU-04, HU-05 y HU-06 — Gestión de empleados
mvn verify -Dcucumber.filter.tags="@empleados"

# HU-07, HU-08 y HU-09 — Usuarios del sistema
mvn verify -Dcucumber.filter.tags="@usuarios"

# HU-10 — Clientes
mvn verify -Dcucumber.filter.tags="@clientes"
```

### Combinar tags

```bash
# Autenticación y roles juntos
mvn verify -Dcucumber.filter.tags="@autenticacion or @roles"

# Todo excepto empleados
mvn verify -Dcucumber.filter.tags="not @empleados"
```

---

## Ver el reporte HTML

Después de ejecutar los tests, el reporte se genera automáticamente en:

```
target/site/serenity/index.html
```

Para abrirlo en WSL:

```bash
explorer.exe $(wslpath -w target/site/serenity/index.html)
```

---

## Historias de usuario cubiertas

### `@autenticacion` — HU-01 y HU-02

| HU | Escenario |
|----|-----------|
| HU-01 | Inicio de sesión exitoso con credenciales válidas |
| HU-01 | Error al ingresar contraseña de 6 caracteres o menos |
| HU-01 | Error al ingresar credenciales incorrectas |
| HU-01 | Error al dejar los campos vacíos |
| HU-01 | Redirección al login cuando el token JWT está expirado |
| HU-02 | Cierre de sesión exitoso |
| HU-02 | Intento de acceso tras cerrar sesión |
| HU-02 | Cierre de sesión detectado desde otra pestaña |

### `@roles` — HU-03

| HU | Escenario |
|----|-----------|
| HU-03 | Los módulos del dashboard reflejan el acceso según rol (12 ejemplos) |
| HU-03 | Los roles no pueden gestionarse desde la interfaz |
| HU-03 | Acceso denegado al intentar acceder a un módulo no permitido (8 ejemplos) |

### `@empleados` — HU-04, HU-05 y HU-06

| HU | Escenario |
|----|-----------|
| HU-04 | Registro exitoso de empleado |
| HU-04 | Registro con teléfono secundario |
| HU-04 | Cédula duplicada |
| HU-04 | Correo duplicado |
| HU-04 | Campos obligatorios vacíos |
| HU-04 | Acceso no autorizado al módulo de registro |
| HU-05 | Edición exitosa de información general |
| HU-05 | Edición de cédula por Administrador |
| HU-05 | Campo cédula deshabilitado para rol Recursos Humanos |
| HU-05 | Correo duplicado al editar |
| HU-05 | Cédula duplicada al editar |
| HU-05 | Campos obligatorios vacíos al editar |
| HU-05 | Acceso no autorizado al módulo de edición |
| HU-06 | Listar todos los empleados con sus datos |
| HU-06 | Buscar empleado por nombre |
| HU-06 | Búsqueda por nombre sin resultados |
| HU-06 | Buscar empleado por cédula |
| HU-06 | Búsqueda por cédula sin resultados |
| HU-06 | Filtrar empleados por estado activo |
| HU-06 | Lista vacía de empleados _(requiere BD vacía — tag `@pendiente`, excluido por defecto)_ |
| HU-06 | Acceso denegado para roles no autorizados |

### `@usuarios` — HU-07

| HU | Escenario |
|----|-----------|
| HU-07 | Creación exitosa de usuario |
| HU-07 | Empleado con múltiples usuarios |
| HU-07 | Nombre de usuario duplicado |
| HU-07 | Contraseña inválida |
| HU-07 | Campos obligatorios vacíos |
| HU-07 | Acceso no autorizado al módulo de creación de usuarios |

### `@clientes` — HU-10

| HU | Escenario |
|----|-----------|
| HU-10 | Registro exitoso de cliente |
| HU-10 | Registro con teléfono secundario |
| HU-10 | Cédula o NIT duplicado |
| HU-10 | Correo duplicado en clientes |
| HU-10 | Campos obligatorios vacíos |
| HU-10 | Acceso no autorizado al módulo de clientes |
| HU-11 | Edición exitosa de información general del cliente |
| HU-11 | Edición de cédula o NIT por Administrador |
| HU-11 | Campo cédula deshabilitado para rol Ventas |
| HU-11 | Correo duplicado al editar cliente |
| HU-11 | Cédula duplicada al editar cliente |
| HU-11 | Campos obligatorios vacíos al editar cliente |
| HU-11 | Acceso no autorizado al módulo de edición de clientes |

---

## Estructura del proyecto

```
serenity-inventario-login/
├── pom.xml
├── .libs/                                    ← Librerías de sistema para WSL
└── src/
    └── test/
        ├── java/com/inventario/
        │   ├── pages/
        │   │   ├── LoginPage.java            ← HU-01: formulario de login
        │   │   ├── DashboardPage.java        ← Dashboard, sesiones y JWT
        │   │   ├── UnauthorizedPage.java     ← Página de acceso denegado
        │   │   ├── EmpleadosPage.java        ← Lista, búsqueda y modal de edición
        │   │   ├── RegistrarEmpleadoPage.java← Formulario de registro de empleado
        │   │   └── CrearUsuarioPage.java     ← Formulario de creación de usuario
        │   ├── steps/
        │   │   ├── LoginStepDefinitions.java     ← HU-01
        │   │   ├── LogoutStepDefinitions.java    ← HU-02
        │   │   ├── RolesStepDefinitions.java     ← HU-03
        │   │   ├── RegistrarEmpleadoStepDefinitions.java ← HU-04
        │   │   ├── EditarEmpleadoStepDefinitions.java    ← HU-05
        │   │   ├── ListarEmpleadosStepDefinitions.java   ← HU-06
        │   │   └── CrearUsuarioStepDefinitions.java      ← HU-07
        │   └── runners/
        │       └── CucumberTestRunner.java   ← Runner principal
        └── resources/
            ├── features/
            │   ├── login.feature             ← @autenticacion
            │   ├── logout.feature            ← @autenticacion
            │   ├── roles.feature             ← @roles
            │   ├── registrar-empleado.feature← @empleados
            │   ├── editar-empleado.feature   ← @empleados
            │   ├── listar-empleados.feature  ← @empleados
            │   └── crear-usuario.feature     ← @usuarios
            └── serenity.conf                 ← Configuración de Serenity y Chrome
```

---

## Solución de problemas

**La app no responde en localhost:4200**
Verifica que el frontend esté corriendo con `pnpm run dev`.

**El backend retorna 401 / la prueba redirige al login inesperadamente**
Verifica que la API esté corriendo en `http://localhost:3000`. Los escenarios de empleados y usuarios necesitan el backend para insertar y consultar datos.

**Chrome no inicia / libgbm error**
Las librerías en `.libs/` resuelven esto automáticamente. Si el error persiste:

```bash
sudo apt-get install -y libgbm1
```

**Los tests de HU-06 fallan en "Lista vacía"**
Este escenario usa `ng.getComponent()` de Angular, disponible únicamente cuando la app corre con `ng serve` (modo desarrollo). No funciona con builds de producción.

**Timeout en escenarios de edición o registro**
Aumenta el timeout de Serenity en `serenity.conf`:

```
serenity.timeout = 15000
```
