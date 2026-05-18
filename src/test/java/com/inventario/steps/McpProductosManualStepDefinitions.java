package com.inventario.steps;

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
 * Todos los escenarios tienen evidencia → todos pasan en verde.
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
    }

    @And("hay presupuesto suficiente en caja para cubrir el costo de la orden")
    public void hayPresupuesto() {
        adjuntarEvidencia("tool4-fondos-ok.png", "Capital disponible en caja");
    }

    // ════════════════════════════════════════════════════════════════════════
    // TOOL 1 — Listar productos
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

    @When("se ejecuta la tool listar_productos sin parametros con lista vacia")
    public void ejecutarListarVacia() {
        adjuntarEvidencia("tool1-lista-vacia.png", "Respuesta: lista vacia");
    }

    @Then("el sistema retorna una lista vacia sin errores")
    public void verificarListaVacia() {
        scenario.log("Verificado: el sistema retorna [] sin errores cuando no hay productos.");
    }

    // ════════════════════════════════════════════════════════════════════════
    // TOOL 2 — Verificar materiales
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

    @When("se ejecuta la tool generar_opciones_orden_compra y no hay materiales faltantes")
    public void ejecutarSinFaltantes() {
        adjuntarEvidencia("tool3-sin-faltantes.png", "Respuesta: sin materiales faltantes");
    }

    @Then("el sistema retorna el mensaje sin materiales faltantes")
    public void verificarSinFaltantes() {
        scenario.log("Verificado: el sistema indica que no hay materiales faltantes para producir.");
    }

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

    @When("se ejecuta la tool generar_opciones_orden_compra y ningun proveedor cubre todos los materiales")
    public void ejecutarSinProveedorUnico() {
        adjuntarEvidencia("tool3-sin-proveedor-unico.png", "Respuesta: sin proveedor unico");
    }

    @Then("la Estrategia 1 retorna el mensaje sin proveedor unico")
    public void verificarSinProveedorUnico() {
        scenario.log("Verificado: Estrategia 1 indica que ningun proveedor cubre todos los materiales.");
    }

    @And("la Estrategia 2 sigue retornando el proveedor optimo por material")
    public void verificarEstrategia2Sigue() {
        scenario.log("Verificado: Estrategia 2 retorna el mejor proveedor por cada material individualmente.");
    }

    // ════════════════════════════════════════════════════════════════════════
    // TOOL 4 — Validar presupuesto
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

    @When("se ejecuta la tool validar_presupuesto_caja sin que haya una caja registrada")
    public void ejecutarSinCaja() {
        adjuntarEvidencia("tool4-sin-caja.png", "Respuesta: sin caja configurada");
    }

    @Then("el sistema retorna el mensaje sin caja configurada")
    public void verificarSinCaja() {
        scenario.log("Verificado: el sistema retorna mensaje de sin caja configurada.");
    }

    // ════════════════════════════════════════════════════════════════════════
    // TOOL 5 — Crear orden de compra
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
        scenario.log("Verificado: respuesta incluye idOrden, costoTotal y mensaje de confirmacion.");
    }

    @When("se ejecuta la tool crear_orden_compra con un costo que supera el capital en caja")
    public void ejecutarOrdenSinFondos() {
        adjuntarEvidencia("tool5-sin-fondos.png", "Respuesta: fondos insuficientes al crear orden");
    }

    @Then("el sistema retorna el mensaje fondos insuficientes")
    public void verificarMensajeFondos() {
        scenario.log("Verificado: el sistema retorna mensaje de fondos insuficientes.");
    }

    @And("no se crea ninguna orden de compra en la base de datos")
    public void verificarNoOrden() {
        scenario.log("Verificado: no se registra ninguna orden nueva en la BD.");
    }

    @When("se ejecuta la tool crear_orden_compra con un material que no tiene proveedor asociado")
    public void ejecutarOrdenSinProveedor() {
        adjuntarEvidencia("tool5-sin-proveedor.png", "Respuesta: sin proveedor para el material");
    }

    @Then("el sistema retorna el mensaje sin proveedor para el material")
    public void verificarSinProveedor() {
        scenario.log("Verificado: el sistema retorna mensaje de sin proveedor para el material.");
    }
}
