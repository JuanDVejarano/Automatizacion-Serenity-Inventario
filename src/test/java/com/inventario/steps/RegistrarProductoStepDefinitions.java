package com.inventario.steps;

import com.inventario.pages.RegistrarProductoPage;
import com.inventario.pages.TipoProductoPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.assertj.core.api.Assertions.assertThat;

public class RegistrarProductoStepDefinitions {

    TipoProductoPage tipoProductoPage;
    RegistrarProductoPage registrarProductoPage;

    private String generateNombre() {
        return "Prod " + Math.abs(System.nanoTime() % 999_999L);
    }

    // ── Background: garantiza que haya al menos un tipo y navega al formulario ──

    @Given("existe al menos un tipo de producto disponible para seleccionar")
    public void existeAlMenosUnTipoDeProductoDisponibleParaSeleccionar() {
        // Crear un tipo de producto para garantizar que el select no esté vacío
        tipoProductoPage.open();
        tipoProductoPage.waitForFormToLoad();
        tipoProductoPage.fillNombre("Tipo Prod " + Math.abs(System.nanoTime() % 999_999L));
        tipoProductoPage.submit();
        tipoProductoPage.getSuccessMessage();

        // Navegar al formulario de producto y esperar que los tipos carguen
        registrarProductoPage.open();
        registrarProductoPage.waitForFormToLoad();
    }

    // ── Escenario 1: Registro exitoso ────────────────────────────────────────

    @When("el usuario registra un producto con todos los campos obligatorios")
    public void elUsuarioRegistraUnProductoConTodosLosCamposObligatorios() {
        registrarProductoPage.fillNombre(generateNombre());
        registrarProductoPage.selectFirstTipo();
        registrarProductoPage.fillPrecio("15000");
        registrarProductoPage.submit();
    }

    // ── Escenario 2: Con características ────────────────────────────────────

    @When("el usuario registra un producto incluyendo las caracteristicas del producto")
    public void elUsuarioRegistraUnProductoIncluyendoLasCaracteristicasDelProducto() {
        registrarProductoPage.fillNombre(generateNombre());
        registrarProductoPage.selectFirstTipo();
        registrarProductoPage.fillPrecio("25000");
        registrarProductoPage.fillCaracteristicas("Material quirurgico de alta resistencia");
        registrarProductoPage.submit();
    }

    // ── Escenario 3: Precio inválido ─────────────────────────────────────────

    @When("el usuario intenta registrar un producto con precio cero")
    public void elUsuarioIntentaRegistrarUnProductoConPrecioCero() {
        registrarProductoPage.fillNombre(generateNombre());
        registrarProductoPage.selectFirstTipo();
        registrarProductoPage.fillPrecio("0"); // <= 0 → error frontend
        registrarProductoPage.submit();
    }

    // ── Escenario 4: Campos vacíos ────────────────────────────────────────────

    @When("el usuario intenta registrar un producto sin diligenciar los campos obligatorios")
    public void elUsuarioIntentaRegistrarUnProductoSinDiligenciarLosCamposObligatorios() {
        // nombre vacío → validación frontend falla antes de llegar al backend
        registrarProductoPage.submit();
    }

    // ── Escenario 5: Sin tipos disponibles ────────────────────────────────────

    @When("se simula que no hay tipos de producto disponibles en el sistema")
    public void seSimulaQueNoHayTiposDeProductoDisponiblesEnElSistema() {
        registrarProductoPage.simulateNoTipos();
    }

    @Then("el sistema muestra la advertencia de que no hay tipos de producto registrados")
    public void elSistemaMuestraLaAdvertenciaDeQueNoHayTiposDeProductoRegistrados() {
        assertThat(registrarProductoPage.isWarningVisible())
            .as("La advertencia de sin tipos de producto debe ser visible")
            .isTrue();
    }

    @And("el boton de registrar producto esta deshabilitado")
    public void elBotonDeRegistrarProductoEstaDeshabilitado() {
        assertThat(registrarProductoPage.isSubmitButtonDisabled())
            .as("El boton debe estar deshabilitado cuando no hay tipos de producto")
            .isTrue();
    }

    // ── Escenario 6: Acceso no autorizado ────────────────────────────────────

    @When("el usuario intenta acceder al modulo de registro de productos")
    public void elUsuarioIntentaAccederAlModuloDeRegistroDeProductos() {
        registrarProductoPage.open();
    }

    // ── Assertions ────────────────────────────────────────────────────────────

    @Then("el sistema muestra el mensaje de exito de producto {string}")
    public void elSistemaMuestraElMensajeDeExitoDeProducto(String mensajeEsperado) {
        String mensajeActual = registrarProductoPage.getSuccessMessage();
        assertThat(normalizarTexto(mensajeActual))
            .as("El mensaje de exito del producto debe coincidir")
            .containsIgnoringCase(normalizarTexto(mensajeEsperado));
    }

    @Then("el sistema muestra el mensaje de error de producto {string}")
    public void elSistemaMuestraElMensajeDeErrorDeProducto(String mensajeEsperado) {
        String mensajeActual = registrarProductoPage.getErrorMessage();
        assertThat(normalizarTexto(mensajeActual))
            .as("El mensaje de error del producto debe coincidir")
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
