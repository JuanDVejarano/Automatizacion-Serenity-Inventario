package com.inventario.steps;

import com.inventario.pages.DashboardPage;
import com.inventario.pages.LoginPage;
import com.inventario.pages.SaldoCajaPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.assertj.core.api.Assertions.assertThat;

public class SaldoCajaStepDefinitions {

    LoginPage loginPage;
    DashboardPage dashboardPage;
    SaldoCajaPage saldoCajaPage;

    // ── Background ──────────────────────────────────────────────────────────

    @Given("el usuario accede al modulo de saldo de caja")
    public void elUsuarioAccedeAlModuloDeSaldoDeCaja() {
        loginPage.open();
        loginPage.enterUsername("admin");
        loginPage.enterPassword("admin1412");
        loginPage.clickLoginButton();
        dashboardPage.waitForLogoutButton();
        saldoCajaPage.open();
        saldoCajaPage.waitForFormToLoad();
    }

    // ── Escenario 1: Consulta exitosa ────────────────────────────────────────

    @Then("el sistema muestra la tarjeta de saldo con el capital disponible")
    public void elSistemaMuestraLaTarjetaDeSaldoConElCapitalDisponible() {
        assertThat(saldoCajaPage.isSaldoCardVisible())
            .as("La tarjeta de saldo de caja debe estar visible")
            .isTrue();
    }

    // ── Escenario 2: Saldo en cero ───────────────────────────────────────────

    @When("se simula que el capital de la caja es cero")
    public void seSimulaQueElCapitalDeLaCajaEsCero() {
        saldoCajaPage.simulateSaldoCero();
    }

    @Then("el sistema muestra la alerta de que no hay capital disponible")
    public void elSistemaMuestraLaAlertaDeQueNoHayCapitalDisponible() {
        assertThat(saldoCajaPage.isSinCapitalAlertVisible())
            .as("La alerta de sin capital debe estar visible cuando el saldo es cero")
            .isTrue();
    }

    // ── Escenario 3: Sin caja configurada ────────────────────────────────────

    @When("se simula que no hay caja configurada")
    public void seSimulaQueNoHayCajaConfigurada() {
        saldoCajaPage.simulateNoCaja();
    }

    @Then("el sistema muestra el mensaje de sin caja {string}")
    public void elSistemaMuestraElMensajeDeSinCaja(String mensajeEsperado) {
        String mensajeActual = saldoCajaPage.getNoCajaMessage();
        assertThat(normalizarTexto(mensajeActual))
            .as("El mensaje de sin caja debe coincidir")
            .containsIgnoringCase(normalizarTexto(mensajeEsperado));
    }

    // ── Escenario 4: Acceso no autorizado ────────────────────────────────────

    @When("el usuario intenta acceder al modulo de saldo de caja")
    public void elUsuarioIntentaAccederAlModuloDeSaldoDeCaja() {
        saldoCajaPage.open();
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
