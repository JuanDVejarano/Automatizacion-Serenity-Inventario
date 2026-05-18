# Serenity BDD — Pruebas InvSystem

Proyecto de pruebas E2E y manuales con evidencia para **InvSystem**, construido con **Serenity BDD + Cucumber + Selenium WebDriver**.

Cubre **31 historias de usuario** organizadas en 13 módulos con más de 200 escenarios.

---

## Requisitos previos

| Herramienta | Versión mínima |
| ----------- | -------------- |
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
mvn clean verify serenity:aggregate
```

### Solo compilar y empaquetar sin ejecutar tests

```bash
mvn verify -DskipTests
```

### Por módulo (tags)

```bash
# HU-01 y HU-02 — Autenticación (login y logout)
mvn clean verify -Dcucumber.filter.tags="@autenticacion" serenity:aggregate

# HU-03 — Roles del sistema
mvn clean verify -Dcucumber.filter.tags="@roles" serenity:aggregate

# HU-04, HU-05 y HU-06 — Gestión de empleados
mvn clean verify -Dcucumber.filter.tags="@empleados" serenity:aggregate

# HU-07, HU-08 y HU-09 — Usuarios del sistema
mvn clean verify -Dcucumber.filter.tags="@usuarios" serenity:aggregate

# HU-10, HU-11 y HU-12 — Clientes
mvn clean verify -Dcucumber.filter.tags="@clientes" serenity:aggregate

# HU-13, HU-14 y HU-15 — Ventas
mvn clean verify -Dcucumber.filter.tags="@ventas" serenity:aggregate

# HU-16, HU-17 y HU-18 — Producción (tipos, productos, edición)
mvn clean verify -Dcucumber.filter.tags="@produccion" serenity:aggregate

# HU-20, HU-21 y HU-24 — Materia prima
mvn clean verify -Dcucumber.filter.tags="@materiaPrima" serenity:aggregate

# HU-22 y HU-23 — Proveedores
mvn clean verify -Dcucumber.filter.tags="@proveedores" serenity:aggregate

# HU-25 — Órdenes de compra
mvn clean verify -Dcucumber.filter.tags="@ordenesDeCompra" serenity:aggregate

# HU-26, HU-27 y HU-28 — Caja
mvn clean verify -Dcucumber.filter.tags="@caja" serenity:aggregate

# HU-19, HU-29, HU-30 y HU-31 — MCP e IA (pruebas manuales con evidencia)
mvn clean verify -Dcucumber.filter.tags="@IA" serenity:aggregate
```

### Combinar tags

```bash
# Varios módulos juntos
mvn clean verify -Dcucumber.filter.tags="@autenticacion or @roles" serenity:aggregate

# Todo excepto empleados
mvn clean verify -Dcucumber.filter.tags="not @empleados" serenity:aggregate
```

---

## Ver el reporte HTML

```bash
# Generar el reporte a partir de los resultados existentes sin re-ejecutar tests
mvn serenity:aggregate
```

Después de ejecutar los tests el reporte se genera automáticamente en:

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

| HU    | Escenario |
| ----- | --------- |
| HU-01 | Inicio de sesión exitoso con credenciales válidas |
| HU-01 | Error al ingresar contraseña de 6 caracteres o menos |
| HU-01 | Error al ingresar credenciales incorrectas |
| HU-01 | Error al dejar los campos vacíos |
| HU-01 | Redirección al login cuando el token JWT está expirado |
| HU-02 | Cierre de sesión exitoso |
| HU-02 | Intento de acceso tras cerrar sesión |
| HU-02 | Cierre de sesión detectado desde otra pestaña |

### `@roles` — HU-03

| HU    | Escenario |
| ----- | --------- |
| HU-03 | Los módulos del dashboard reflejan el acceso según rol |
| HU-03 | Los roles no pueden gestionarse desde la interfaz |
| HU-03 | Acceso denegado al intentar acceder a un módulo no permitido |

### `@empleados` — HU-04, HU-05 y HU-06

| HU    | Escenario |
| ----- | --------- |
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
| HU-06 | Listar todos los empleados |
| HU-06 | Buscar empleado por nombre |
| HU-06 | Búsqueda sin resultados |
| HU-06 | Buscar empleado por cédula |
| HU-06 | Filtrar por estado activo |
| HU-06 | Lista vacía _(tag `@pendiente` — requiere BD vacía)_ |
| HU-06 | Acceso no autorizado |

### `@usuarios` — HU-07, HU-08 y HU-09

| HU    | Escenario |
| ----- | --------- |
| HU-07 | Creación exitosa de usuario |
| HU-07 | Nombre de usuario duplicado |
| HU-07 | Contraseña inválida |
| HU-07 | Campos obligatorios vacíos |
| HU-07 | Acceso no autorizado |
| HU-08 | Asignación exitosa de rol |
| HU-08 | Acceso no autorizado |
| HU-09 | Activar usuario inactivo |
| HU-09 | Desactivar usuario activo |
| HU-09 | Acceso no autorizado |

### `@clientes` — HU-10, HU-11 y HU-12

| HU    | Escenario |
| ----- | --------- |
| HU-10 | Registro exitoso de cliente |
| HU-10 | Cédula o NIT duplicado |
| HU-10 | Correo duplicado |
| HU-10 | Campos obligatorios vacíos |
| HU-10 | Acceso no autorizado |
| HU-11 | Edición exitosa de cliente |
| HU-11 | Correo duplicado al editar |
| HU-11 | Cédula duplicada al editar |
| HU-11 | Campos obligatorios vacíos al editar |
| HU-11 | Acceso no autorizado |
| HU-12 | Listar todos los clientes |
| HU-12 | Buscar cliente por nombre |
| HU-12 | Buscar cliente por cédula o NIT |
| HU-12 | Lista vacía _(tag `@pendiente` — requiere BD vacía)_ |
| HU-12 | Acceso no autorizado |

### `@ventas` — HU-13, HU-14 y HU-15

| HU    | Escenario |
| ----- | --------- |
| HU-13 | Creación exitosa de venta |
| HU-13 | Agregar múltiples productos al detalle |
| HU-13 | Stock insuficiente |
| HU-13 | Venta sin productos en el detalle |
| HU-13 | Venta cancelada |
| HU-13 | Acceso no autorizado |
| HU-14 | Agregar producto por código de barras |
| HU-14 | Código de barras no encontrado |
| HU-14 | Producto con stock insuficiente |
| HU-15 | Listar historial de ventas |
| HU-15 | Filtrar ventas por estado |
| HU-15 | Lista vacía de ventas _(tag `@pendiente`)_ |
| HU-15 | Acceso no autorizado |

### `@produccion` — HU-16, HU-17 y HU-18

| HU    | Escenario |
| ----- | --------- |
| HU-16 | Registro exitoso de tipo de producto |
| HU-16 | Nombre de tipo duplicado |
| HU-16 | Campo nombre vacío |
| HU-16 | Acceso no autorizado |
| HU-17 | Registro exitoso de producto |
| HU-17 | Precio inválido |
| HU-17 | Campos obligatorios vacíos |
| HU-17 | Sin tipos de producto disponibles |
| HU-17 | Acceso no autorizado |
| HU-18 | Edición exitosa de producto |
| HU-18 | Precio inválido al editar |
| HU-18 | Nombre vacío al editar |
| HU-18 | Actualizar stock exitosamente |
| HU-18 | Cantidad de stock inválida |
| HU-18 | Identificar producto por código de barras |
| HU-18 | Código de barras no encontrado |
| HU-18 | Acceso no autorizado |

### `@materiaPrima` — HU-20, HU-21 y HU-24

| HU    | Escenario |
| ----- | --------- |
| HU-20 | Registro exitoso de materia prima |
| HU-20 | Nombre duplicado |
| HU-20 | Campo nombre vacío |
| HU-20 | Acceso no autorizado |
| HU-21 | Asociación exitosa de materia prima a producto |
| HU-21 | Asociar múltiples materias primas |
| HU-21 | Materia prima ya asociada (409) |
| HU-21 | Cantidad inválida |
| HU-21 | Editar cantidad de materia prima asociada |
| HU-21 | Eliminar asociación |
| HU-21 | Sin materias primas registradas |
| HU-21 | Acceso no autorizado |
| HU-24 | Aumentar stock de materia prima |
| HU-24 | Reducir stock de materia prima |
| HU-24 | Stock insuficiente para reducir |
| HU-24 | Cantidad inválida |
| HU-24 | Acceso no autorizado |

### `@proveedores` — HU-22 y HU-23

| HU    | Escenario |
| ----- | --------- |
| HU-22 | Registro exitoso de proveedor |
| HU-22 | NIT duplicado |
| HU-22 | Nombre duplicado |
| HU-22 | Correo duplicado |
| HU-22 | Campos obligatorios vacíos |
| HU-22 | Acceso no autorizado |
| HU-23 | Asociación exitosa de proveedor a materia prima |
| HU-23 | Asociar múltiples materias primas a un proveedor |
| HU-23 | Asociación duplicada (409) |
| HU-23 | Costo por unidad inválido |
| HU-23 | Editar costo de asociación |
| HU-23 | Eliminar asociación |
| HU-23 | Sin materias primas registradas |
| HU-23 | Acceso no autorizado |

### `@ordenesDeCompra` — HU-25

| HU    | Escenario |
| ----- | --------- |
| HU-25 | Listar todas las órdenes de compra |
| HU-25 | Ver detalle de una orden |
| HU-25 | Filtrar órdenes por estado |
| HU-25 | Filtrar órdenes por fecha sin resultados |
| HU-25 | Cambiar estado de orden a Completada |
| HU-25 | Cancelar una orden de compra |
| HU-25 | Completada descuenta capital de caja _(tag `@pendiente`)_ |
| HU-25 | Lista vacía _(tag `@pendiente`)_ |
| HU-25 | Acceso no autorizado |

### `@caja` — HU-26, HU-27 y HU-28

| HU    | Escenario |
| ----- | --------- |
| HU-26 | Consulta exitosa del saldo de caja |
| HU-26 | Saldo de caja en cero (simulado) |
| HU-26 | Sin caja configurada (simulado) |
| HU-26 | Acceso no autorizado |
| HU-27 | Registrar gasto operativo |
| HU-27 | Registrar ingreso externo |
| HU-27 | Inyección de capital por Administrador |
| HU-27 | Deducción de saldo por Administrador |
| HU-27 | Capital insuficiente |
| HU-27 | Valor inválido |
| HU-27 | Tipos admin no visibles para Tesorería (simulado) |
| HU-27 | Acceso no autorizado |
| HU-28 | Listar historial de movimientos |
| HU-28 | Filtrar por tipo sin resultados |
| HU-28 | Filtrar por fecha sin resultados |
| HU-28 | Filtrar por tipo de operación |
| HU-28 | Ver detalle de movimiento asociado a venta |
| HU-28 | Ver detalle de movimiento asociado a orden |
| HU-28 | Lista vacía (simulado) |
| HU-28 | Acceso no autorizado |

### `@IA` — HU-19, HU-29, HU-30 y HU-31 _(pruebas manuales con evidencia)_

| HU    | Escenario |
| ----- | --------- |
| HU-19 | Tool 1 — Listar productos (con y sin resultados) |
| HU-19 | Tool 2 — Verificar materiales (suficientes, insuficientes, producto inexistente) |
| HU-19 | Tool 3 — Generar opciones (sin faltantes, dos estrategias, sin proveedor único) |
| HU-19 | Tool 4 — Validar presupuesto (fondos ok, insuficientes, sin caja) |
| HU-19 | Tool 5 — Crear orden de compra (exitosa, sin fondos, sin proveedor) |
| HU-29 | Tool 1 — Consultar estado actual de caja |
| HU-29 | Tool 2 — Consultar historial de movimientos con filtros |
| HU-29 | Tool 3 — Analizar ingresos vs egresos por período |
| HU-29 | Tool 4 — Proyección de flujo de caja |
| HU-29 | Tool 5 — Reporte de rentabilidad por producto |
| HU-29 | Tool 6 — Detectar meses con déficit o superávit |
| HU-29 | Tool 7 — Resumen financiero general |
| HU-30 | Agente encadena tools automáticamente |
| HU-30 | Control de acceso 403 para roles no autorizados |
| HU-30 | Consulta de producción encadenada |
| HU-30 | Confirmación antes de crear_orden_compra |
| HU-31 | Interfaz del chat carga con todos sus elementos |
| HU-31 | Indicador de carga mientras el agente procesa |
| HU-31 | Botones de confirmación en el chat |
| HU-31 | Módulo no visible para roles no autorizados |

> Las pruebas `@IA` no ejecutan navegador — adjuntan evidencia de capturas de pantalla al reporte Serenity usando `Serenity.recordReportData()`.

---

## Evidencias manuales

Las capturas de pantalla de las pruebas manuales se ubican en:

```
manual-evidence/
├── hu-19/    ← MCP de productos
├── hu-29/    ← MCP de finanzas
├── hu-30/    ← Agente LangGraph
└── hu-31/    ← Chat del agente (frontend)
```

---

## Estructura del proyecto

```
serenity-inventario-login/
├── pom.xml
├── .libs/                        ← Librerías de sistema para WSL
├── manual-evidence/              ← Capturas de pantalla para pruebas manuales
│   ├── hu-19/
│   ├── hu-29/
│   ├── hu-30/
│   └── hu-31/
└── src/test/
    ├── java/com/inventario/
    │   ├── pages/                ← Page Objects (un archivo por módulo)
    │   ├── steps/                ← Step Definitions (un archivo por HU)
    │   └── runners/
    │       └── CucumberTestRunner.java
    └── resources/
        ├── features/             ← Archivos .feature (uno por HU)
        └── serenity.conf         ← Configuración de Serenity y Chrome
```

---

## Solución de problemas

**La app no responde en localhost:4200**
Verifica que el frontend esté corriendo con `pnpm run dev`.

**El backend retorna 401 o redirige al login inesperadamente**
Verifica que la API esté corriendo en `http://localhost:3000`.

**Chrome no inicia / libgbm error**
Las librerías en `.libs/` resuelven esto automáticamente. Si el error persiste:

```bash
sudo apt-get install -y libgbm1
```

**Los escenarios `@pendiente` aparecen en amarillo en el reporte**
Es el comportamiento esperado. Esos escenarios requieren una base de datos vacía o evidencia manual que aún no se ha capturado.

**`DuplicateStepDefinitionException` al correr todos los tests**
Asegúrate de que no haya dos métodos con el mismo texto de step en clases diferentes. Usa nombres de step específicos por módulo.
