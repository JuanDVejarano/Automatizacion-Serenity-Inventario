package com.inventario.steps;

import com.inventario.pages.DashboardPage;
import com.inventario.pages.EmpleadosPage;
import com.inventario.pages.LoginPage;
import com.inventario.pages.RegistrarEmpleadoPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.assertj.core.api.Assertions.assertThat;

public class EditarEmpleadoStepDefinitions {

    LoginPage loginPage;
    DashboardPage dashboardPage;
    RegistrarEmpleadoPage registrarEmpleadoPage;
    EmpleadosPage empleadosPage;

    // Estado compartido entre pasos del mismo escenario
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

    private void createEmployee(String cedula, String nombre, String correo) {
        registrarEmpleadoPage.open();
        registrarEmpleadoPage.fillCamposObligatorios(
            cedula, nombre, correo, "Calle Test 100", "Bogota", "3000000001");
        registrarEmpleadoPage.submit();
        registrarEmpleadoPage.getSuccessMessage(); // espera confirmacion del backend
    }

    // ── Background ──────────────────────────────────────────────────────────

    @Given("el administrador ha iniciado sesion para edicion de empleados")
    public void elAdministradorHaIniciadoSesionParaEdicionDeEmpleados() {
        loginPage.open();
        loginPage.enterUsername("admin");
        loginPage.enterPassword("admin1412");
        loginPage.clickLoginButton();
        dashboardPage.waitForLogoutButton();
    }

    // ── Precondiciones ───────────────────────────────────────────────────────

    @Given("existe un empleado registrado para la prueba de edicion")
    public void existeUnEmpleadoRegistradoParaLaPruebaDeEdicion() {
        lastCedula1 = generateCedula();
        lastCorreo1 = generateCorreo(lastCedula1);
        createEmployee(lastCedula1, "Empleado Edicion", lastCorreo1);
        empleadosPage.open();
        empleadosPage.waitForList();
    }

    @Given("existen dos empleados registrados para la prueba de duplicados en edicion")
    public void existenDosEmpleadosRegistradosParaLaPruebaDeDuplicadosEnEdicion() {
        lastCedula1 = generateCedula();
        lastCorreo1 = generateCorreo(lastCedula1);
        createEmployee(lastCedula1, "Empleado Edicion Uno", lastCorreo1);

        lastCedula2 = generateCedula();
        lastCorreo2 = generateCorreo(lastCedula2);
        createEmployee(lastCedula2, "Empleado Edicion Dos", lastCorreo2);

        empleadosPage.open();
        empleadosPage.waitForList();
    }

    @And("se establece el token de sesion con rol Recursos Humanos")
    public void seEstableceElTokenDeSesionConRolRecursosHumanos() {
        // La lista ya cargó con el token real de admin.
        // Swapear ANTES de abrir el modal: esAdmin() se evalúa por primera vez
        // cuando @if(modalAbierto()) renderiza → leerá este token RRHH → disabled=true
        dashboardPage.setSessionWithRole("Recursos Humanos");
    }

    // ── Acciones sobre la lista ──────────────────────────────────────────────

    @When("el usuario busca y abre el modal de edicion del empleado")
    public void elUsuarioBuscaYAbreElModalDeEdicionDelEmpleado() {
        empleadosPage.searchEmployee(lastCedula1);
        empleadosPage.clickFirstEditButton();
        empleadosPage.waitForModal();
    }

    // ── Acciones dentro del modal ────────────────────────────────────────────

    @And("el usuario actualiza el nombre en el modal a {string}")
    public void elUsuarioActualizaElNombreEnElModal(String nuevoNombre) {
        empleadosPage.setEditNombre(nuevoNombre);
    }

    @And("el Administrador cambia la cedula del empleado por un valor nuevo")
    public void elAdministradorCambiaLaCedulaDelEmpleadoPorUnValorNuevo() {
        newCedula = generateCedula();
        empleadosPage.setEditCedula(newCedula);
    }

    @And("el usuario borra los campos obligatorios del modal de edicion")
    public void elUsuarioBorraLosCamposObligatoriosDelModal() {
        empleadosPage.clearRequiredFields();
    }

    @And("el usuario guarda los cambios en el modal")
    public void elUsuarioGuardaLosCambiosEnElModal() {
        empleadosPage.clickSave();
    }

    // ── Escenarios de duplicados ─────────────────────────────────────────────

    @When("el usuario abre el modal del primer empleado e ingresa el correo del segundo")
    public void elUsuarioAbreElModalDelPrimerEmpleadoEIngresaElCorreoDelSegundo() {
        empleadosPage.searchEmployee(lastCedula1);
        empleadosPage.clickFirstEditButton();
        empleadosPage.waitForModal();
        empleadosPage.setEditCorreo(lastCorreo2);
        empleadosPage.clickSave();
    }

    @When("el usuario abre el modal del primer empleado e ingresa la cedula del segundo")
    public void elUsuarioAbreElModalDelPrimerEmpleadoEIngresaLaCedulaDelSegundo() {
        empleadosPage.searchEmployee(lastCedula1);
        empleadosPage.clickFirstEditButton();
        empleadosPage.waitForModal();
        empleadosPage.setEditCedula(lastCedula2);
        empleadosPage.clickSave();
    }

    // ── Escenario acceso no autorizado ───────────────────────────────────────

    @When("el usuario intenta acceder al modulo de lista de empleados")
    public void elUsuarioIntentaAccederAlModuloDeListaDeEmpleados() {
        // roleGuard deniega al rol Ventas y redirige a /unauthorized
        empleadosPage.open();
    }

    // ── Assertions ───────────────────────────────────────────────────────────

    @Then("el sistema muestra el mensaje de exito de edicion {string}")
    public void elSistemaMuestraElMensajeDeExitoDeEdicion(String mensajeEsperado) {
        String mensajeActual = empleadosPage.getModalSuccessMessage();
        assertThat(normalizarTexto(mensajeActual))
            .as("El mensaje de exito del modal debe coincidir")
            .containsIgnoringCase(normalizarTexto(mensajeEsperado));
    }

    @Then("el sistema muestra el mensaje de error de edicion {string}")
    public void elSistemaMuestraElMensajeDeErrorDeEdicion(String mensajeEsperado) {
        String mensajeActual = empleadosPage.getModalErrorMessage();
        assertThat(normalizarTexto(mensajeActual))
            .as("El mensaje de error del modal debe coincidir")
            .containsIgnoringCase(normalizarTexto(mensajeEsperado));
    }

    @Then("el campo cedula esta deshabilitado en el modal de edicion")
    public void elCampoCedulaEstaDeshabilitadoEnElModalDeEdicion() {
        assertThat(empleadosPage.isCedulaFieldDisabled())
            .as("El campo cedula debe estar deshabilitado para el rol Recursos Humanos")
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
