package com.inventario.steps;

import com.inventario.pages.DashboardPage;
import com.inventario.pages.LoginPage;
import com.inventario.pages.RegistrarMovimientoPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.assertj.core.api.Assertions.assertThat;

public class RegistrarMovimientoStepDefinitions {

    LoginPage loginPage;
    DashboardPage dashboardPage;
    RegistrarMovimientoPage registrarMovimientoPage;

    // ── Background ──────────────────────────────────────────────────────────

    @Given("el usuario accede al modulo de registrar movimiento de caja")
    public void elUsuarioAccedeAlModuloDeRegistrarMovimientoDeCaja() {
        loginPage.open();
        loginPage.enterUsername("admin");
        loginPage.enterPassword("admin1412");
        loginPage.clickLoginButton();
        dashboardPage.waitForLogoutButton();
        registrarMovimientoPage.open();
        registrarMovimientoPage.waitForFormToLoad();
    }

    // ── Escenarios 1-4: Registro exitoso ────────────────────────────────────

    @When("el usuario registra un movimiento de tipo {string} con valor {double}")
    public void elUsuarioRegistraUnMovimientoDeTipoConValor(String tipo, double valor) {
        registrarMovimientoPage.selectTipoByName(tipo);
        registrarMovimientoPage.fillValor(valor);
        registrarMovimientoPage.submit();
    }

    @Then("el sistema muestra el mensaje de exito del movimiento {string}")
    public void elSistemaMuestraElMensajeDeExitoDelMovimiento(String mensajeEsperado) {
        String mensajeActual = registrarMovimientoPage.waitForExito();
        assertThat(normalizarTexto(mensajeActual))
            .as("El mensaje de exito del movimiento debe coincidir")
            .containsIgnoringCase(normalizarTexto(mensajeEsperado));
    }

    // ── Escenario 5: Capital insuficiente ────────────────────────────────────

    @When("el usuario intenta registrar un gasto operativo con valor mayor al capital disponible")
    public void elUsuarioIntentaRegistrarUnGastoOperativoConValorMayorAlCapitalDisponible() {
        registrarMovimientoPage.selectTipoByName("Gasto operativo");
        registrarMovimientoPage.fillValor(999_999_999_999.0); // siempre supera el capital disponible
        registrarMovimientoPage.submit();
    }

    @Then("el sistema muestra el mensaje de error del movimiento {string}")
    public void elSistemaMuestraElMensajeDeErrorDelMovimiento(String mensajeEsperado) {
        String mensajeActual = registrarMovimientoPage.waitForErrorMsg();
        assertThat(normalizarTexto(mensajeActual))
            .as("El mensaje de error del movimiento debe coincidir")
            .containsIgnoringCase(normalizarTexto(mensajeEsperado));
    }

    // ── Escenario 6: Valor inválido ──────────────────────────────────────────

    @When("el usuario intenta registrar un movimiento con valor cero")
    public void elUsuarioIntentaRegistrarUnMovimientoConValorCero() {
        registrarMovimientoPage.selectTipoByName("Gasto operativo");
        registrarMovimientoPage.fillValor(0);
        registrarMovimientoPage.submit();
    }

    @Then("el sistema muestra el error de validacion del movimiento {string}")
    public void elSistemaMuestraElErrorDeValidacionDelMovimiento(String mensajeEsperado) {
        String mensajeActual = registrarMovimientoPage.waitForValorError();
        assertThat(normalizarTexto(mensajeActual))
            .as("El error de validacion del movimiento debe coincidir")
            .containsIgnoringCase(normalizarTexto(mensajeEsperado));
    }

    // ── Escenario 7: Tipos no visibles para Tesorería ────────────────────────

    @When("se simula el acceso al modulo con rol Tesoreria")
    public void seSimulaElAccesoAlModuloConRolTesoreria() {
        // Filtra el signal 'tipos' para excluir los admin-only, simulando
        // lo que vería un usuario Tesorería tras cargar el módulo
        registrarMovimientoPage.simulateTesoreriaFilter();
    }

    @Then("el tipo de movimiento {string} no debe aparecer en el formulario")
    public void elTipoDeMovimientoNoDebeAparecerEnElFormulario(String tipoNombre) {
        assertThat(registrarMovimientoPage.hasTipoByName(tipoNombre))
            .as("El tipo '" + tipoNombre + "' NO debe estar disponible para Tesoreria")
            .isFalse();
    }

    @And("el formulario debe mostrar solo los tipos de movimiento permitidos para Tesoreria")
    public void elFormularioDebeMostrarSoloLosTiposPermitidosParaTesoreria() {
        assertThat(registrarMovimientoPage.getTipoCount())
            .as("Solo deben haber 2 tipos de movimiento disponibles para Tesoreria")
            .isEqualTo(2);
    }

    // ── Escenario 8: Acceso no autorizado ────────────────────────────────────

    @When("el usuario intenta acceder al modulo de registrar movimiento")
    public void elUsuarioIntentaAccederAlModuloDeRegistrarMovimiento() {
        registrarMovimientoPage.open();
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
