# Serenity BDD - Pruebas de Login InvSystem

Proyecto de automatización de pruebas para la HU-01 (Inicio de sesión) del sistema InvSystem, construido con **Serenity BDD + Cucumber + Selenium**.

---

## Requisitos previos

| Herramienta | Versión mínima |
| ----------- | -------------- |
| Java        | 17             |
| Maven       | 3.8+           |

> **Chrome** es descargado automáticamente por Selenium Manager al primera ejecución. No necesitas instalarlo manualmente.

> **WSL**: Las librerías de sistema que Chrome necesita (`libgbm1`, `libwayland-server0`) ya están incluidas en la carpeta `.libs/` del proyecto. El `pom.xml` las configura automáticamente.

---

## Requisito: app corriendo

Los tests apuntan a `http://localhost:4200`. Antes de ejecutar, asegúrate de que el frontend Angular esté levantado:

```bash
# Desde la raíz del monorepo
cd ~/invetarioMonoRepoMCP
pnpm run dev
```

---

## Ejecutar los tests

```bash
cd ~/Projects-Test/serenity-inventario-login
mvn test
```

Esto ejecuta los 4 escenarios de la HU-01 y genera los resultados en `target/`.

---

## Ver el reporte HTML

Después de correr los tests, genera el reporte completo con:

```bash
mvn verify -DskipTests
```

Luego abre en el navegador:

```
target/site/serenity/index.html
```

En WSL puedes abrirlo con:

```bash
explorer.exe $(wslpath -w target/site/serenity/index.html)
```

---

## Escenarios cubiertos (HU-01)

| Escenario                | Datos de entrada                     | Resultado esperado                                        |
| ------------------------ | ------------------------------------ | --------------------------------------------------------- |
| Login exitoso            | usuario: `admin`, clave: `admin1412` | Redirige a `/dashboard`                                   |
| Contraseña muy corta     | clave de 6 caracteres o menos        | Mensaje: _"La contraseña debe tener más de 6 caracteres"_ |
| Credenciales incorrectas | usuario/clave que no existen         | Mensaje: _"Usuario o contraseña incorrectos"_             |
| Campos vacíos            | Sin ingresar nada                    | Mensaje: _"Todos los campos son obligatorios"_            |

---

## Estructura del proyecto

```
serenity-inventario-login/
├── pom.xml
├── .libs/                              ← Librerías de sistema para WSL (no modificar)
└── src/
    └── test/
        ├── java/com/inventario/
        │   ├── pages/
        │   │   └── LoginPage.java      ← Page Object con selectores del formulario
        │   ├── steps/
        │   │   └── LoginStepDefinitions.java  ← Implementación de los pasos Gherkin
        │   └── runners/
        │       └── CucumberTestRunner.java    ← Runner de Cucumber con Serenity
        └── resources/
            ├── features/
            │   └── login.feature       ← Escenarios en lenguaje Gherkin
            └── serenity.conf           ← Configuración de Serenity y Chrome
```

---

## Correr un escenario específico por tag

Agrega un tag en el `.feature`:

```gherkin
@login-exitoso
Scenario: Inicio de sesion exitoso con credenciales validas
```

Y ejecuta:

```bash
mvn test -Dcucumber.filter.tags="@login-exitoso"
```

---

## Solución de problemas

**La app no responde en localhost:4200**
Verifica que el servidor esté corriendo antes de lanzar los tests.

**Chrome no inicia / libgbm error**
Las librerías en `.libs/` resuelven esto automáticamente. Si el error persiste, instala la dependencia del sistema:

```bash
sudo apt-get install -y libgbm1
```

**Los tests fallan por timeout**
El login hace una petición al backend. Verifica que la API también esté corriendo (`http://localhost:3000` o el puerto configurado)
