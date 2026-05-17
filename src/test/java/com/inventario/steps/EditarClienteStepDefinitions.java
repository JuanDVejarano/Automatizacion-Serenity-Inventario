package com.inventario.steps;

import com.inventario.pages.ClientesPage;
import com.inventario.pages.DashboardPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.assertj.core.api.Assertions.assertThat;

public class EditarClienteStepDefinitions {

    DashboardPage dashboardPage;
    ClientesPage clientesPage;

    private String lastCedula1;
    private String lastCorreo1;
    private String lastCedula2;
    private String lastCorreo2;
    private String newCedula;

    private String generateCedula() {
        return String.valueOf(10_000_000L + Math.abs(System.nanoTime() % 89_999_999L));
    }

    private String generateCorreo(String cedula) {
        return "edit" + cedula + "@serenity.test";
    }

    private void createClient(String cedula, String nombre, String correo) {
        clientesPage.clickNuevoCliente();
        clientesPage.fillCamposObligatorios(
            cedula, nombre, correo, "Bogota", "Calle Test 1", "3000000010");
        clientesPage.submit();
        clientesPage.getFormSuccessMessage();
        clientesPage.waitForModalToClose();
    }

    // ── Precondiciones ───────────────────────────────────────────────────────

    @Given("existe un cliente registrado para la prueba de edicion")
    public void existeUnClienteRegistradoParaLaPruebaDeEdicion() {
        lastCedula1 = generateCedula();
        lastCorreo1 = generateCorreo(lastCedula1);
        createClient(lastCedula1, "Cliente Edicion", lastCorreo1);
    }

    @Given("existen dos clientes registrados para la prueba de duplicados en edicion")
    public void existenDosClientesRegistradosParaLaPruebaDeDuplicadosEnEdicion() {
        lastCedula1 = generateCedula();
        lastCorreo1 = generateCorreo(lastCedula1);
        createClient(lastCedula1, "Cliente Edicion Uno", lastCorreo1);

        lastCedula2 = generateCedula();
        lastCorreo2 = generateCorreo(lastCedula2);
        createClient(lastCedula2, "Cliente Edicion Dos", lastCorreo2);
    }

    // Mismo truco que HU-05: la lista ya cargó con token admin real.
    // esAdmin() en clientes aún no se ha evaluado (modal cerrado → @if no renderizado).
    // Al swapear el token y abrir el modal, esAdmin() se evalúa por primera vez
    // con el token Ventas → false → campo cédula deshabilitado.
    @And("se establece el token de sesion con rol Ventas para clientes")
    public void seEstableceElTokenDeSesionConRolVentasParaClientes() {
        dashboardPage.setSessionWithRole("Ventas");
    }

    // ── Acciones sobre la lista ──────────────────────────────────────────────

    @When("el usuario busca y abre el modal de edicion del cliente")
    public void elUsuarioBuscaYAbreElModalDeEdicionDelCliente() {
        clientesPage.searchClient(lastCedula1);
        clientesPage.clickFirstEditButton();
    }

    // ── Acciones dentro del modal de edición ─────────────────────────────────

    @And("el usuario actualiza el nombre del cliente a {string}")
    public void elUsuarioActualizaElNombreDelClienteA(String nuevoNombre) {
        clientesPage.setEditNombre(nuevoNombre);
    }

    @And("el Administrador cambia la cedula del cliente por un valor nuevo")
    public void elAdministradorCambiaLaCedulaDelClientePorUnValorNuevo() {
        newCedula = generateCedula();
        clientesPage.setEditCedula(newCedula);
    }

    @And("el usuario borra los campos obligatorios del modal de edicion del cliente")
    public void elUsuarioBorraLosCamposObligatoriosDelModalDeEdicionDelCliente() {
        clientesPage.clearEditRequiredFields();
    }

    @And("el usuario guarda los cambios del cliente")
    public void elUsuarioGuardaLosCambiosDelCliente() {
        clientesPage.submit();
    }

    // ── Duplicados ────────────────────────────────────────────────────────────

    @When("el usuario abre el modal del primer cliente e ingresa el correo del segundo cliente")
    public void elUsuarioAbreElModalDelPrimerClienteEIngresaElCorreoDelSegundoCliente() {
        clientesPage.searchClient(lastCedula1);
        clientesPage.clickFirstEditButton();
        clientesPage.setEditCorreo(lastCorreo2);
        clientesPage.submit();
    }

    @When("el usuario abre el modal del primer cliente e ingresa la cedula del segundo cliente")
    public void elUsuarioAbreElModalDelPrimerClienteEIngresaLaCedulaDelSegundoCliente() {
        clientesPage.searchClient(lastCedula1);
        clientesPage.clickFirstEditButton();
        clientesPage.setEditCedula(lastCedula2);
        clientesPage.submit();
    }

    // ── Assertions ────────────────────────────────────────────────────────────

    @Then("el sistema muestra el mensaje de exito de edicion de cliente {string}")
    public void elSistemaMuestraElMensajeDeExitoDeEdicionDeCliente(String mensajeEsperado) {
        String mensajeActual = clientesPage.getEditSuccessMessage();
        assertThat(normalizarTexto(mensajeActual))
            .as("El mensaje de exito de edicion del cliente debe coincidir")
            .containsIgnoringCase(normalizarTexto(mensajeEsperado));
    }

    @Then("el sistema muestra el mensaje de error de edicion de cliente {string}")
    public void elSistemaMuestraElMensajeDeErrorDeEdicionDeCliente(String mensajeEsperado) {
        String mensajeActual = clientesPage.getEditErrorMessage();
        assertThat(normalizarTexto(mensajeActual))
            .as("El mensaje de error de edicion del cliente debe coincidir")
            .containsIgnoringCase(normalizarTexto(mensajeEsperado));
    }

    @Then("el campo cedula o NIT esta deshabilitado en el modal de edicion del cliente")
    public void elCampoCedulaONITEstaDeshabilitadoEnElModalDeEdicionDelCliente() {
        assertThat(clientesPage.isEditCedulaDisabled())
            .as("El campo cédula/NIT debe estar deshabilitado para el rol Ventas")
            .isTrue();
    }

    private String normalizarTexto(String texto) {
        return texto
            .replace("á", "a").replace("é", "e").replace("í", "i")
            .replace("ó", "o").replace("ú", "u").replace("ñ", "n")
            .replace("Á", "A").replace("É", "E").replace("Í", "I")
            .replace("Ó", "O").replace("Ú", "U").replace("Ñ", "N");
    }
}
