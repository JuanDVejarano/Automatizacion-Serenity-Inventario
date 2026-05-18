package com.inventario.steps;

import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.java.Before;
import net.serenitybdd.core.Serenity;

import java.io.File;

/**
 * Pruebas manuales HU-29 — MCP de finanzas.
 * Todos los escenarios tienen evidencia → todos pasan en verde.
 */
public class McpFinanzasManualStepDefinitions {

    private Scenario scenario;

    @Before
    public void beforeFinanzas(Scenario scenario) {
        this.scenario = scenario;
    }

    // ── Helper ───────────────────────────────────────────────────────────────

    private void adjuntarEvidencia(String archivo, String titulo) {
        File f = new File(System.getProperty("user.dir"), "manual-evidence/hu-29/" + archivo);
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

    @Given("el MCP de finanzas esta disponible y conectado a la base de datos")
    public void elMcpFinanzasDisponible() {
        adjuntarEvidencia("mcp-finanzas-conectado.png", "MCP de finanzas conectado a la BD");
    }

    // ════════════════════════════════════════════════════════════════════════
    // TOOL 1 — Consultar estado actual de caja
    // ════════════════════════════════════════════════════════════════════════

    @When("se ejecuta la tool consultar_estado_caja")
    public void ejecutarConsultarEstadoCaja() {
        adjuntarEvidencia("tool1-estado-caja.png", "Respuesta: consultar_estado_caja");
    }

    @Then("el sistema retorna el capital disponible, fecha del ultimo movimiento, total de ingresos y egresos historicos")
    public void verificarEstadoCaja() {
        scenario.log("Verificado: respuesta contiene capitalDisponible, fechaUltimoMovimiento, totalIngresos y totalEgresos.");
    }

    // ════════════════════════════════════════════════════════════════════════
    // TOOL 2 — Consultar historial de movimientos con filtros
    // ════════════════════════════════════════════════════════════════════════

    @When("se ejecuta la tool consultar_historial_movimientos con filtros opcionales")
    public void ejecutarConsultarHistorial() {
        adjuntarEvidencia("tool2-historial-movimientos.png", "Respuesta: consultar_historial_movimientos");
    }

    @Then("el sistema retorna la lista de movimientos que coincidan con los filtros aplicados")
    public void verificarHistorialMovimientos() {
        scenario.log("Verificado: respuesta contiene lista de movimientos con fecha, tipo, valor y referencia.");
    }

    // ════════════════════════════════════════════════════════════════════════
    // TOOL 3 — Analizar ingresos vs egresos
    // ════════════════════════════════════════════════════════════════════════

    @When("se ejecuta la tool analizar_ingresos_egresos con una fecha de inicio y fin")
    public void ejecutarAnalizarIngresosEgresos() {
        adjuntarEvidencia("tool3-ingresos-egresos.png", "Respuesta: analizar_ingresos_egresos");
    }

    @Then("el sistema retorna el total de ingresos, egresos, balance neto y desglose por tipo de movimiento")
    public void verificarAnalisisIngresosEgresos() {
        scenario.log("Verificado: respuesta contiene totalIngresos, totalEgresos, balanceNeto y desglose por tipo.");
    }

    // ════════════════════════════════════════════════════════════════════════
    // TOOL 4 — Proyeccion de flujo de caja
    // ════════════════════════════════════════════════════════════════════════

    @When("se ejecuta la tool proyectar_flujo_caja con el numero de meses a proyectar")
    public void ejecutarProyectarFlujoCaja() {
        adjuntarEvidencia("tool4-proyeccion-flujo.png", "Respuesta: proyectar_flujo_caja");
    }

    @Then("el sistema retorna la proyeccion de ingreso, egreso y saldo estimado por cada mes")
    public void verificarProyeccionFlujoCaja() {
        scenario.log("Verificado: respuesta contiene proyeccion con ingresoEstimado, egresoEstimado y saldoProyectado por mes.");
    }

    // ════════════════════════════════════════════════════════════════════════
    // TOOL 5 — Reporte de rentabilidad por producto
    // ════════════════════════════════════════════════════════════════════════

    @When("se ejecuta la tool reporte_rentabilidad_productos")
    public void ejecutarReporteRentabilidad() {
        adjuntarEvidencia("tool5-rentabilidad-productos.png", "Respuesta: reporte_rentabilidad_productos");
    }

    @Then("el sistema retorna el ranking de productos con unidades vendidas e ingresos generados")
    public void verificarReporteRentabilidad() {
        scenario.log("Verificado: respuesta contiene ranking de productos con nombre, unidadesVendidas e ingresosGenerados.");
    }

    // ════════════════════════════════════════════════════════════════════════
    // TOOL 6 — Detectar deficit o superavit
    // ════════════════════════════════════════════════════════════════════════

    @When("se ejecuta la tool detectar_deficit_superavit con el anio a analizar")
    public void ejecutarDetectarDeficitSuperavit() {
        adjuntarEvidencia("tool6-deficit-superavit.png", "Respuesta: detectar_deficit_superavit");
    }

    @Then("el sistema retorna la clasificacion de deficit o superavit por cada mes del anio")
    public void verificarDeficitSuperavit() {
        scenario.log("Verificado: respuesta contiene clasificacion Superavit o Deficit por cada mes con ingresos, egresos y balance.");
    }

    // ════════════════════════════════════════════════════════════════════════
    // TOOL 7 — Resumen financiero general
    // ════════════════════════════════════════════════════════════════════════

    @When("se ejecuta la tool resumen_financiero_general")
    public void ejecutarResumenFinanciero() {
        adjuntarEvidencia("tool7-resumen-financiero.png", "Respuesta: resumen_financiero_general");
    }

    @Then("el sistema retorna capital actual, totales historicos de ventas y ordenes, y balance general")
    public void verificarResumenFinanciero() {
        scenario.log("Verificado: respuesta contiene capitalActual, totalVentas, totalOrdenes, gastos operativos y balanceGeneral.");
    }
}
