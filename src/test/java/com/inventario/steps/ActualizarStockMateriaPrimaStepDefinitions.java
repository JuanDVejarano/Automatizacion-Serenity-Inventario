package com.inventario.steps;

import com.inventario.pages.ActualizarStockMateriaPrimaPage;
import com.inventario.pages.DashboardPage;
import com.inventario.pages.LoginPage;
import com.inventario.pages.RegistrarMateriaPrimaPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.assertj.core.api.Assertions.assertThat;

public class ActualizarStockMateriaPrimaStepDefinitions {

    LoginPage loginPage;
    DashboardPage dashboardPage;
    RegistrarMateriaPrimaPage registrarMateriaPrimaPage;
    ActualizarStockMateriaPrimaPage actualizarStockPage;

    // ── Background ──────────────────────────────────────────────────────────

    @Given("el usuario accede al modulo de actualizar stock de materia prima")
    public void elUsuarioAccedeAlModuloDeActualizarStockDeMateriaPrima() {
        loginPage.open();
        loginPage.enterUsername("admin");
        loginPage.enterPassword("admin1412");
        loginPage.clickLoginButton();
        dashboardPage.waitForLogoutButton();

        // Crear una materia prima con stock 0 para usar en los escenarios
        registrarMateriaPrimaPage.open();
        registrarMateriaPrimaPage.waitForFormToLoad();
        registrarMateriaPrimaPage.fillNombre("StockMP " + Math.abs(System.nanoTime() % 999_999L));
        registrarMateriaPrimaPage.submit();
        registrarMateriaPrimaPage.getSuccessMessage();

        actualizarStockPage.open();
        actualizarStockPage.waitForFormToLoad();
    }

    // ── Given: materia con stock disponible ─────────────────────────────────

    @Given("la materia prima tiene stock disponible para reducir")
    public void laMateriaPrimaTieneStockDisponibleParaReducir() {
        // Aumenta el stock primero para que haya unidades que reducir
        actualizarStockPage.selectFirstMateria();
        actualizarStockPage.setOperacion("aumentar");
        actualizarStockPage.fillCantidad(20);
        actualizarStockPage.actualizar();
        actualizarStockPage.getSuccessMessage();
        // La materia sigue seleccionada para el siguiente paso
    }

    // ── Escenario 1: Aumentar stock ──────────────────────────────────────────

    @When("el usuario aumenta el stock de la primera materia prima en 10 unidades")
    public void elUsuarioAumentaElStockDeLaPrimeraMateriaPrimaEn10Unidades() {
        actualizarStockPage.selectFirstMateria();
        actualizarStockPage.setOperacion("aumentar");
        actualizarStockPage.fillCantidad(10);
        actualizarStockPage.actualizar();
    }

    // ── Escenario 2: Reducir stock ───────────────────────────────────────────

    @When("el usuario reduce el stock de la primera materia prima en 5 unidades")
    public void elUsuarioReduceElStockDeLaPrimeraMateriaPrimaEn5Unidades() {
        // La materia ya está seleccionada del Given; sólo cambia operación y cantidad
        actualizarStockPage.setOperacion("reducir");
        actualizarStockPage.fillCantidad(5);
        actualizarStockPage.actualizar();
    }

    // ── Escenario 3: Stock insuficiente ──────────────────────────────────────
    // La materia recién creada tiene stock = 0; reducir cualquier cantidad falla en backend

    @When("el usuario intenta reducir el stock en una cantidad mayor al disponible")
    public void elUsuarioIntentaReducirElStockEnUnaCantidadMayorAlDisponible() {
        actualizarStockPage.selectFirstMateria();
        int stockActual = actualizarStockPage.getCurrentStock();
        actualizarStockPage.setOperacion("reducir");
        actualizarStockPage.fillCantidad(stockActual + 1); // siempre supera el stock disponible
        actualizarStockPage.actualizar();
    }

    // ── Escenario 4: Cantidad inválida ───────────────────────────────────────

    @When("el usuario intenta actualizar el stock con cantidad cero")
    public void elUsuarioIntentaActualizarElStockConCantidadCero() {
        actualizarStockPage.selectFirstMateria();
        actualizarStockPage.fillCantidad(0);
        actualizarStockPage.actualizar();
    }

    // ── Escenario 5: Acceso no autorizado ────────────────────────────────────

    @When("el usuario intenta acceder al modulo de actualizar stock de materia prima")
    public void elUsuarioIntentaAccederAlModuloDeActualizarStockDeMateriaPrima() {
        actualizarStockPage.open();
    }

    // ── Assertions ────────────────────────────────────────────────────────────

    @Then("el sistema muestra el mensaje de exito de actualizacion de stock {string}")
    public void elSistemaMuestraElMensajeDeExitoDeActualizacionDeStock(String mensajeEsperado) {
        String mensajeActual = actualizarStockPage.getSuccessMessage();
        assertThat(normalizarTexto(mensajeActual))
            .as("El mensaje de exito de actualizacion de stock debe coincidir")
            .containsIgnoringCase(normalizarTexto(mensajeEsperado));
    }

    @Then("el sistema muestra el mensaje de error de actualizacion de stock {string}")
    public void elSistemaMuestraElMensajeDeErrorDeActualizacionDeStock(String mensajeEsperado) {
        String mensajeActual = actualizarStockPage.getErrorMessage();
        assertThat(normalizarTexto(mensajeActual))
            .as("El mensaje de error de actualizacion de stock debe coincidir")
            .containsIgnoringCase(normalizarTexto(mensajeEsperado));
    }

    private String normalizarTexto(String texto) {
        return texto
            .replace("á", "a").replace("é", "e").replace("í", "i")
            .replace("ó", "o").replace("ú", "u").replace("ñ", "n")
            .replace("Á", "A").replace("É", "E").replace("Í", "I")
            .replace("Ó", "O").replace("Ú", "U").replace("Ñ", "N");
    }
}
