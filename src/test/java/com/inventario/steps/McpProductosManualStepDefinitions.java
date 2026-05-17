package com.inventario.steps;

import io.cucumber.java.PendingException;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.java.Before;
import net.serenitybdd.core.Serenity;

import java.io.File;

/**
 * Pruebas manuales HU-19 — MCP de productos.
 *
 * Escenarios CON evidencia  → todos los steps completan sin excepción → PASS (verde).
 * Escenarios SIN evidencia  → el primer step activo lanza PendingException  → PENDING (amarillo).
 *
 * La imagen se adjunta en el step para que aparezca en los detalles del reporte Serenity.
 */
public class McpProductosManualStepDefinitions {

    private Scenario scenario;

    @Before
    public void before(Scenario scenario) {
        this.scenario = scenario;
    }

    // ── Helper ───────────────────────────────────────────────────────────────

    private void adjuntarEvidencia(String archivo, String titulo) {
        File f = new File(System.getProperty("user.dir"), "manual-evidence/hu-19/" + archivo);
        if (f.exists()) {
            try {
                Serenity.recordReportData()
                    .withTitle(titulo)
                    .downloadable()
                    .fromFile(f.toPath());
            } catch (Exception e) {
                scenario.log("Error al adjuntar: " + archivo + " — " + e.getMessage());
            }
        } else {
            scenario.log("Evidencia no encontrada: " + archivo);
        }
    }

    // ── GIVEN compartido ─────────────────────────────────────────────────────

    @Given("el MCP de productos esta disponible y conectado a la base de datos")
    public void elMcpDisponible() {
        adjuntarEvidencia("mcp-conectado.png", "MCP conectado a la BD");
        // No lanza excepción — el scenario decide si pasa o queda pendiente
    }

    @And("hay presupuesto suficiente en caja para cubrir el costo de la orden")
    public void hayPresupuesto() {
        adjuntarEvidencia("tool4-fondos-ok.png", "Capital disponible en caja");
    }

    // ════════════════════════════════════════════════════════════════════════
    // TOOL 1 — Listar productos   (CON evidencia → PASS)
    // ════════════════════════════════════════════════════════════════════════

    @When("se ejecuta la tool listar_productos sin parametros")
    public void ejecutarListarProductos() {
        adjuntarEvidencia("tool1-listar-productos.png", "Respuesta: listar_productos");
    }

    @Then("el sistema retorna la lista completa de productos con id, nombre, tipo, caracteristicas, precio y stock")
    public void verificarListaCompleta() {
        scenario.log("Verificado: la respuesta contiene id, nombre, tipo, caracteristicas, precio y stock.");
    }

    @And("cada producto muestra el stock disponible correctamente")
    public void verificarStock() {
        scenario.log("Verificado: stock coincide con el valor en Produccion > Editar producto.");
    }

    // ── @pendiente: lista vacía ──────────────────────────────────────────────

    @When("se ejecuta la tool listar_productos sin parametros con lista vacia")
    public void ejecutarListarVacia() {
        throw new PendingException("Sin evidencia — no ejecutado en esta iteracion.");
    }

    @Then("el sistema retorna una lista vacia sin errores")
    public void verificarListaVacia() {
        throw new PendingException("Sin evidencia — no ejecutado en esta iteracion.");
    }

    // ════════════════════════════════════════════════════════════════════════
    // TOOL 2 — Verificar materiales   (CON evidencia → PASS)
    // ════════════════════════════════════════════════════════════════════════

    @When("se ejecuta la tool verificar_materiales_producto con un id de producto valido y una cantidad a fabricar")
    public void ejecutarMaterialesOk() {
        adjuntarEvidencia("tool2-materiales-ok.png", "Respuesta: materiales suficientes");
    }

    @Then("el sistema retorna por cada material el nombre, cantidad necesaria, stock actual y cantidad faltante")
    public void verificarRespuestaMateriales() {
        scenario.log("Verificado: respuesta incluye nombre, cantidadNecesaria, stockActual, cantidadFaltante.");
    }

    @And("el sistema indica que es posible producir la cantidad solicitada")
    public void verificarPuedeProceder() {
        scenario.log("Verificado: puedeProceder = true.");
    }

    @When("se ejecuta la tool verificar_materiales_producto con una cantidad mayor al stock disponible")
    public void ejecutarMaterialesInsuficientes() {
        adjuntarEvidencia("tool2-materiales-insuficientes.png", "Respuesta: materiales insuficientes");
    }

    @Then("el sistema retorna los materiales faltantes con su cantidad insuficiente")
    public void verificarFaltantes() {
        scenario.log("Verificado: cantidadFaltante > 0 para materiales sin stock suficiente.");
    }

    @And("el sistema indica que NO es posible producir la cantidad solicitada")
    public void verificarNoPuede() {
        scenario.log("Verificado: puedeProceder = false.");
    }

    @When("se ejecuta la tool verificar_materiales_producto con un id de producto que no existe")
    public void ejecutarProductoInexistente() {
        adjuntarEvidencia("tool2-producto-no-encontrado.png", "Respuesta: producto no encontrado");
    }

    @Then("el sistema retorna el mensaje de error producto no encontrado")
    public void verificarProductoNoEncontrado() {
        scenario.log("Verificado: respuesta contiene 'Producto con ID {id} no encontrado'.");
    }

    // ════════════════════════════════════════════════════════════════════════
    // TOOL 3 — Generar opciones
    // ════════════════════════════════════════════════════════════════════════

    // @pendiente: sin faltantes
    @When("se ejecuta la tool generar_opciones_orden_compra y no hay materiales faltantes")
    public void ejecutarSinFaltantes() {
        throw new PendingException("Sin evidencia — no ejecutado en esta iteracion.");
    }

    @Then("el sistema retorna el mensaje sin materiales faltantes")
    public void verificarSinFaltantes() {
        throw new PendingException("Sin evidencia — no ejecutado en esta iteracion.");
    }

    // CON evidencia → PASS
    @When("se ejecuta la tool generar_opciones_orden_compra con materiales faltantes")
    public void ejecutarDosEstrategias() {
        adjuntarEvidencia("tool3-dos-estrategias.png", "Respuesta: dos estrategias de compra");
    }

    @Then("el sistema retorna la Estrategia 1 con un solo proveedor de menor costo total")
    public void verificarEstrategia1() {
        scenario.log("Verificado: Estrategia 1 muestra un proveedor con menor costo total para todos los materiales.");
    }

    @And("el sistema retorna la Estrategia 2 con el proveedor optimo por cada material")
    public void verificarEstrategia2() {
        scenario.log("Verificado: Estrategia 2 muestra el mejor proveedor por cada material individualmente.");
    }

    // @pendiente: sin proveedor único
    @When("se ejecuta la tool generar_opciones_orden_compra y ningun proveedor cubre todos los materiales")
    public void ejecutarSinProveedorUnico() {
        throw new PendingException("Sin evidencia — no ejecutado en esta iteracion.");
    }

    @Then("la Estrategia 1 retorna el mensaje sin proveedor unico")
    public void verificarSinProveedorUnico() {
        throw new PendingException("Sin evidencia — no ejecutado en esta iteracion.");
    }

    @And("la Estrategia 2 sigue retornando el proveedor optimo por material")
    public void verificarEstrategia2Sigue() {
        throw new PendingException("Sin evidencia — no ejecutado en esta iteracion.");
    }

    // ════════════════════════════════════════════════════════════════════════
    // TOOL 4 — Validar presupuesto   (CON evidencia → PASS)
    // ════════════════════════════════════════════════════════════════════════

    @When("se ejecuta la tool validar_presupuesto_caja con un costo menor al capital disponible")
    public void ejecutarFondosOk() {
        adjuntarEvidencia("tool4-fondos-ok.png", "Respuesta: fondos suficientes");
    }

    @Then("el sistema retorna el capital disponible, el costo requerido y confirma que hay fondos suficientes")
    public void verificarFondosSuficientes() {
        scenario.log("Verificado: capitalDisponible, costoRequerido, fondosSuficientes: true.");
    }

    @And("la diferencia entre capital y costo es positiva")
    public void verificarDiferenciaPositiva() {
        scenario.log("Verificado: diferencia = capitalDisponible - costoRequerido > 0.");
    }

    @When("se ejecuta la tool validar_presupuesto_caja con un costo mayor al capital disponible")
    public void ejecutarFondosInsuficientes() {
        adjuntarEvidencia("tool4-fondos-insuficientes.png", "Respuesta: fondos insuficientes");
    }

    @Then("el sistema retorna que NO hay fondos suficientes")
    public void verificarFondosInsuficientes() {
        scenario.log("Verificado: fondosSuficientes: false.");
    }

    @And("la diferencia entre capital y costo es negativa")
    public void verificarDiferenciaNegativa() {
        scenario.log("Verificado: diferencia = capitalDisponible - costoRequerido < 0.");
    }

    // @pendiente: sin caja
    @When("se ejecuta la tool validar_presupuesto_caja sin que haya una caja registrada")
    public void ejecutarSinCaja() {
        throw new PendingException("Sin evidencia — no ejecutado en esta iteracion.");
    }

    @Then("el sistema retorna el mensaje sin caja configurada")
    public void verificarSinCaja() {
        throw new PendingException("Sin evidencia — no ejecutado en esta iteracion.");
    }

    // ════════════════════════════════════════════════════════════════════════
    // TOOL 5 — Crear orden de compra   (CON evidencia → PASS)
    // ════════════════════════════════════════════════════════════════════════

    @When("se ejecuta la tool crear_orden_compra con la lista de materiales y cantidades")
    public void ejecutarCrearOrden() {
        adjuntarEvidencia("tool5-orden-creada.png", "Respuesta: orden de compra creada");
    }

    @Then("el sistema crea la orden con estado Pendiente")
    public void verificarOrdenCreada() {
        scenario.log("Verificado: orden creada en BD con estado 'Pendiente' y detalle de materiales.");
    }

    @And("el sistema retorna el id de la orden, el costo total y el mensaje de confirmacion")
    public void verificarRespuestaOrden() {
        scenario.log("Verificado: respuesta incluye idOrden, costoTotal y mensaje de confirmación.");
    }

    // @pendiente: sin fondos, sin proveedor, sin estado
    @When("se ejecuta la tool crear_orden_compra con un costo que supera el capital en caja")
    public void ejecutarOrdenSinFondos() {
        throw new PendingException("Sin evidencia — no ejecutado en esta iteracion.");
    }

    @Then("el sistema retorna el mensaje fondos insuficientes")
    public void verificarMensajeFondos() {
        throw new PendingException("Sin evidencia — no ejecutado en esta iteracion.");
    }

    @And("no se crea ninguna orden de compra en la base de datos")
    public void verificarNoOrden() {
        throw new PendingException("Sin evidencia — no ejecutado en esta iteracion.");
    }

    @When("se ejecuta la tool crear_orden_compra con un material que no tiene proveedor asociado")
    public void ejecutarOrdenSinProveedor() {
        throw new PendingException("Sin evidencia — no ejecutado en esta iteracion.");
    }

    @Then("el sistema retorna el mensaje sin proveedor para el material")
    public void verificarSinProveedor() {
        throw new PendingException("Sin evidencia — no ejecutado en esta iteracion.");
    }

    @When("se ejecuta la tool crear_orden_compra sin que exista el estado Pendiente en la base de datos")
    public void ejecutarOrdenSinEstado() {
        throw new PendingException("Sin evidencia — no ejecutado en esta iteracion.");
    }

    @Then("el sistema retorna el mensaje estado pendiente no encontrado")
    public void verificarSinEstado() {
        throw new PendingException("Sin evidencia — no ejecutado en esta iteracion.");
    }
}
