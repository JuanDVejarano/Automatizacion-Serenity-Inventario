package com.inventario.steps;

import com.inventario.pages.ClientesPage;
import com.inventario.pages.DashboardPage;
import com.inventario.pages.LoginPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.assertj.core.api.Assertions.assertThat;

public class RegistrarClienteStepDefinitions {

    LoginPage loginPage;
    DashboardPage dashboardPage;
    ClientesPage clientesPage;

    // Datos del cliente pre-registrado para los escenarios de duplicado
    private String lastCedula;
    private String lastCorreo;

    private String generateCedula() {
        return String.valueOf(10_000_000L + Math.abs(System.nanoTime() % 89_999_999L));
    }

    private String generateCorreo(String cedula) {
        return "cli" + cedula + "@serenity.test";
    }

    // ── Background ──────────────────────────────────────────────────────────

    @Given("el usuario ha iniciado sesion en el modulo de clientes")
    public void elUsuarioHaIniciadoSesionEnElModuloDeClientes() {
        loginPage.open();
        loginPage.enterUsername("admin");
        loginPage.enterPassword("admin1412");
        loginPage.clickLoginButton();
        dashboardPage.waitForLogoutButton();
        // Navegar al módulo Ventas con token real — pre-carga el lazy module
        clientesPage.open();
        clientesPage.waitForPage();
    }

    // ── Escenario 1: Registro exitoso ────────────────────────────────────────

    @When("el usuario registra un cliente con todos los campos obligatorios")
    public void elUsuarioRegistraUnClienteConTodosLosCamposObligatorios() {
        String cedula = generateCedula();
        clientesPage.clickNuevoCliente();
        clientesPage.fillCamposObligatorios(
            cedula, "Cliente Test " + cedula, generateCorreo(cedula),
            "Bogota", "Calle 100 #10-20", "3001234567");
        clientesPage.submit();
    }

    // ── Escenario 2: Con teléfono secundario ─────────────────────────────────

    @When("el usuario registra un cliente incluyendo el telefono secundario")
    public void elUsuarioRegistraUnClienteIncluyendoElTelefonoSecundario() {
        String cedula = generateCedula();
        clientesPage.clickNuevoCliente();
        clientesPage.fillCamposObligatorios(
            cedula, "Cliente Dos Tel " + cedula, generateCorreo(cedula),
            "Medellin", "Carrera 50 #20-10", "3109876543");
        clientesPage.fillTelefono2("6041234567");
        clientesPage.submit();
    }

    // ── Escenario 3 y 4: Pre-registro para duplicados ─────────────────────────

    @Given("el usuario registra previamente un cliente en el sistema")
    public void elUsuarioRegistraPreviamenteUnClienteEnElSistema() {
        lastCedula = generateCedula();
        lastCorreo = generateCorreo(lastCedula);
        clientesPage.clickNuevoCliente();
        clientesPage.fillCamposObligatorios(
            lastCedula, "Cliente Previo", lastCorreo,
            "Cali", "Av. 6 Norte #23-10", "3152223344");
        clientesPage.submit();
        clientesPage.getFormSuccessMessage(); // espera confirmación del backend
        clientesPage.waitForModalToClose();   // espera que el modal se cierre
    }

    @When("el usuario intenta registrar otro cliente con la misma cedula o NIT")
    public void elUsuarioIntentaRegistrarOtroClienteConLaMismaCedulaONIT() {
        String otroCorreo = generateCorreo(generateCedula());
        clientesPage.clickNuevoCliente();
        clientesPage.fillCamposObligatorios(
            lastCedula, "Otro Cliente", otroCorreo,
            "Barranquilla", "Calle Falsa 123", "3164445566");
        clientesPage.submit();
    }

    @When("el usuario intenta registrar otro cliente con el mismo correo")
    public void elUsuarioIntentaRegistrarOtroClienteConElMismoCorreo() {
        String otraCedula = generateCedula();
        clientesPage.clickNuevoCliente();
        clientesPage.fillCamposObligatorios(
            otraCedula, "Otro Cliente Correo", lastCorreo,
            "Cartagena", "Calle Falsa 456", "3176667788");
        clientesPage.submit();
    }

    // ── Escenario 5: Campos vacíos ────────────────────────────────────────────

    @When("el usuario abre el formulario de registro de cliente")
    public void elUsuarioAbreElFormularioDeRegistroDeCliente() {
        clientesPage.clickNuevoCliente();
    }

    @And("el usuario intenta registrar el cliente sin diligenciar los campos")
    public void elUsuarioIntentaRegistrarElClienteSinDiligenciarLosCampos() {
        clientesPage.submit();
    }

    // ── Escenario 6: Acceso no autorizado ─────────────────────────────────────

    @When("el usuario intenta acceder al modulo de clientes")
    public void elUsuarioIntentaAccederAlModuloDeClientes() {
        clientesPage.open();
    }

    // ── Assertions ────────────────────────────────────────────────────────────

    @Then("el sistema muestra el mensaje de exito de cliente {string}")
    public void elSistemaMuestraElMensajeDeExitoDeCliente(String mensajeEsperado) {
        String mensajeActual = clientesPage.getFormSuccessMessage();
        assertThat(normalizarTexto(mensajeActual))
            .as("El mensaje de exito del cliente debe coincidir")
            .containsIgnoringCase(normalizarTexto(mensajeEsperado));
    }

    @Then("el sistema muestra el mensaje de error de cliente {string}")
    public void elSistemaMuestraElMensajeDeErrorDeCliente(String mensajeEsperado) {
        String mensajeActual = clientesPage.getFormErrorMessage();
        assertThat(normalizarTexto(mensajeActual))
            .as("El mensaje de error del cliente debe coincidir")
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
