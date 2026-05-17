package com.inventario.steps;

import com.inventario.pages.DashboardPage;
import com.inventario.pages.LoginPage;
import com.inventario.pages.TipoProductoPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.assertj.core.api.Assertions.assertThat;

public class RegistrarTipoProductoStepDefinitions {

    LoginPage loginPage;
    DashboardPage dashboardPage;
    TipoProductoPage tipoProductoPage;

    private String lastNombre;

    private String generateNombre() {
        return "TipoProd " + Math.abs(System.nanoTime() % 999_999L);
    }

    // ── Background ──────────────────────────────────────────────────────────

    @Given("el usuario ha iniciado sesion en el modulo de produccion")
    public void elUsuarioHaIniciadoSesionEnElModuloDeProduccion() {
        loginPage.open();
        loginPage.enterUsername("admin");
        loginPage.enterPassword("admin1412");
        loginPage.clickLoginButton();
        dashboardPage.waitForLogoutButton();
        // Pre-carga el lazy module de Produccion con token real
        tipoProductoPage.open();
        tipoProductoPage.waitForFormToLoad();
    }

    // ── Escenario 1: Registro exitoso ────────────────────────────────────────

    @When("el usuario registra un tipo de producto con nombre unico")
    public void elUsuarioRegistraUnTipoDeProductoConNombreUnico() {
        tipoProductoPage.fillNombre(generateNombre());
        tipoProductoPage.fillDescripcion("Descripcion de prueba opcional");
        tipoProductoPage.submit();
    }

    // ── Escenario 2: Nombre duplicado ─────────────────────────────────────────

    @Given("el usuario registra previamente un tipo de producto")
    public void elUsuarioRegistraPreviamenteUnTipoDeProducto() {
        lastNombre = generateNombre();
        tipoProductoPage.fillNombre(lastNombre);
        tipoProductoPage.submit();
        tipoProductoPage.getSuccessMessage(); // espera confirmacion del backend
    }

    @When("el usuario intenta registrar otro tipo de producto con el mismo nombre")
    public void elUsuarioIntentaRegistrarOtroTipoDeProductoConElMismoNombre() {
        tipoProductoPage.fillNombre(lastNombre);
        tipoProductoPage.submit();
    }

    // ── Escenario 3: Nombre vacío ─────────────────────────────────────────────

    @When("el usuario intenta registrar un tipo de producto sin nombre")
    public void elUsuarioIntentaRegistrarUnTipoDeProductoSinNombre() {
        // Enviar el formulario con el campo nombre vacío
        tipoProductoPage.submit();
    }

    // ── Escenario 4: Acceso no autorizado ────────────────────────────────────

    @When("el usuario intenta acceder al modulo de tipos de producto")
    public void elUsuarioIntentaAccederAlModuloDeTiposDeProducto() {
        // roleGuard deniega al rol Ventas → redirige a /unauthorized
        tipoProductoPage.open();
    }

    // ── Assertions ────────────────────────────────────────────────────────────

    @Then("el sistema muestra el mensaje de exito de tipo de producto {string}")
    public void elSistemaMuestraElMensajeDeExitoDeTipoDeProducto(String mensajeEsperado) {
        String mensajeActual = tipoProductoPage.getSuccessMessage();
        assertThat(normalizarTexto(mensajeActual))
            .as("El mensaje de exito del tipo de producto debe coincidir")
            .containsIgnoringCase(normalizarTexto(mensajeEsperado));
    }

    @Then("el sistema muestra el mensaje de error de tipo de producto {string}")
    public void elSistemaMuestraElMensajeDeErrorDeTipoDeProducto(String mensajeEsperado) {
        String mensajeActual = tipoProductoPage.getErrorMessage();
        assertThat(normalizarTexto(mensajeActual))
            .as("El mensaje de error del tipo de producto debe coincidir")
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
