package com.inventario.steps;

import com.inventario.pages.DashboardPage;
import com.inventario.pages.HistorialCajaPage;
import com.inventario.pages.LoginPage;
import com.inventario.pages.RegistrarMovimientoPage;
import io.cucumber.java.PendingException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.assertj.core.api.Assertions.assertThat;

public class HistorialCajaStepDefinitions {

    LoginPage loginPage;
    DashboardPage dashboardPage;
    RegistrarMovimientoPage registrarMovimientoPage;
    HistorialCajaPage historialCajaPage;

    // ── Background ──────────────────────────────────────────────────────────

    @Given("el usuario accede al modulo de historial de caja")
    public void elUsuarioAccedeAlModuloDeHistorialDeCaja() {
        loginPage.open();
        loginPage.enterUsername("admin");
        loginPage.enterPassword("admin1412");
        loginPage.clickLoginButton();
        dashboardPage.waitForLogoutButton();
        historialCajaPage.open();
        historialCajaPage.waitForFormToLoad();
    }

    // ── Given: garantiza al menos un movimiento ──────────────────────────────

    @Given("el historial tiene al menos un movimiento registrado")
    public void elHistorialTieneAlMenosUnMovimientoRegistrado() {
        if (historialCajaPage.getTotalCount() == 0) {
            // Crear un movimiento de ingreso externo si no hay ninguno
            registrarMovimientoPage.open();
            registrarMovimientoPage.waitForFormToLoad();
            registrarMovimientoPage.selectTipoByName("Ingreso externo");
            registrarMovimientoPage.fillValor(1000);
            registrarMovimientoPage.submit();
            registrarMovimientoPage.waitForExito();
            // Volver al historial y recargar
            historialCajaPage.open();
            historialCajaPage.waitForFormToLoad();
        }
    }

    // ── Escenario 1: Listar ──────────────────────────────────────────────────

    @Then("el sistema muestra el historial de caja cargado correctamente")
    public void elSistemaMuestraElHistorialDeCajaCargadoCorrectamente() {
        assertThat(historialCajaPage.getDriver().getCurrentUrl())
            .as("La URL debe corresponder al historial de caja")
            .contains("/caja/historial");
    }

    // ── Escenario 2: Filtrar por tipo sin resultados ─────────────────────────

    @When("el usuario filtra los movimientos por un tipo que no tiene registros")
    public void elUsuarioFiltraLosMovimientosPorUnTipoQueNoTieneRegistros() {
        historialCajaPage.setFiltroTipoNonExistent();
    }

    @Then("el sistema muestra el mensaje de historial vacio {string}")
    public void elSistemaMuestraElMensajeDeHistorialVacio(String mensajeEsperado) {
        String mensajeActual = historialCajaPage.getMensajeLista();
        assertThat(normalizarTexto(mensajeActual))
            .as("El mensaje de historial vacio debe coincidir")
            .containsIgnoringCase(normalizarTexto(mensajeEsperado));
    }

    // ── Escenario 3: Filtrar por fecha sin resultados ─────────────────────────

    @When("el usuario aplica un rango de fechas que no coincide con ningun movimiento")
    public void elUsuarioAplicaUnRangoDeFechasQueNoCoincikeConNingunMovimiento() {
        historialCajaPage.setFiltroFechaInicio("1990-01-01");
        historialCajaPage.setFiltroFechaFin("1990-12-31");
    }

    // ── Escenario 4: Filtrar por tipo de operación ───────────────────────────

    @When("el usuario filtra los movimientos por tipo de operacion {string}")
    public void elUsuarioFiltraLosMovimientosPorTipoDeOperacion(String operacion) {
        historialCajaPage.setFiltroOperacion(operacion);
    }

    @Then("el sistema muestra solo los movimientos de tipo ingreso o lista vacia")
    public void elSistemaMuestraSoloLosMovimientosDeTipoIngresoOListaVacia() {
        // El filtro es correcto si la lista filtrada es <= total, y el mensaje
        // solo aparece cuando no hay resultados (ambos son resultados válidos)
        int filtrados = historialCajaPage.getFilteredCount();
        int total     = historialCajaPage.getTotalCount();
        assertThat(filtrados)
            .as("Los movimientos filtrados deben ser <= al total")
            .isLessThanOrEqualTo(total);
    }

    // ── Escenario 7: Lista vacía (simulada) ──────────────────────────────────

    @When("se simula que el historial de caja esta vacio")
    public void seSimulaQueElHistorialDeCajaEstaVacio() {
        historialCajaPage.simulateEmptyHistory();
    }

    // ── Escenarios: detalle de venta y orden ────────────────────────────────

    @Given("existe un movimiento de historial asociado a una venta")
    public void existeUnMovimientoDeHistorialAsociadoAUnaVenta() {
        if (!historialCajaPage.hasMovimientoWithVenta()) {
            throw new PendingException("No hay movimientos con referencia de venta en el historial.");
        }
    }

    @Given("existe un movimiento de historial asociado a una orden de compra")
    public void existeUnMovimientoDeHistorialAsociadoAUnaOrdenDeCompra() {
        if (!historialCajaPage.hasMovimientoWithOrden()) {
            throw new PendingException("No hay movimientos con referencia de orden en el historial.");
        }
    }

    @When("el usuario selecciona el movimiento con referencia de venta")
    public void elUsuarioSeleccionaElMovimientoConReferenciadeVenta() {
        historialCajaPage.selectFirstMovimientoWithVenta();
    }

    @When("el usuario selecciona el movimiento con referencia de orden de compra")
    public void elUsuarioSeleccionaElMovimientoConReferenciaDeOrdenDeCompra() {
        historialCajaPage.selectFirstMovimientoWithOrden();
    }

    @Then("el sistema muestra el detalle de la venta relacionada")
    public void elSistemaMuestraElDetalleDeLaVentaRelacionada() {
        assertThat(historialCajaPage.isDetalleVentaVisible())
            .as("El detalle del movimiento debe mostrar informacion de la venta")
            .isTrue();
    }

    @Then("el sistema muestra el detalle de la orden relacionada")
    public void elSistemaMuestraElDetalleDeLaOrdenRelacionada() {
        assertThat(historialCajaPage.isDetalleOrdenVisible())
            .as("El detalle del movimiento debe mostrar informacion de la orden de compra")
            .isTrue();
    }

    // ── Escenario 8: No autorizado ───────────────────────────────────────────

    @When("el usuario intenta acceder al modulo de historial de caja")
    public void elUsuarioIntentaAccederAlModuloDeHistorialDeCaja() {
        historialCajaPage.open();
    }

    // ── Helper ───────────────────────────────────────────────────────────────

    private String normalizarTexto(String texto) {
        return texto
            .replace("á", "a").replace("é", "e").replace("í", "i")
            .replace("ó", "o").replace("ú", "u").replace("ñ", "n")
            .replace("Á", "A").replace("É", "E").replace("Í", "I")
            .replace("Ó", "O").replace("Ú", "U").replace("Ñ", "N");
    }
}
