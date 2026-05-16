package com.inventario.steps;

import com.inventario.pages.DashboardPage;
import com.inventario.pages.LoginPage;
import com.inventario.pages.RegistrarEmpleadoPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.assertj.core.api.Assertions.assertThat;

public class RegistrarEmpleadoStepDefinitions {

    LoginPage loginPage;
    DashboardPage dashboardPage;
    RegistrarEmpleadoPage registrarEmpleadoPage;

    // Estado compartido entre pasos del mismo escenario (para duplicados)
    private String lastCedula;
    private String lastCorreo;

    // Genera una cedula de 8 digitos unica por ejecucion
    private String generarCedula() {
        return String.valueOf(10_000_000L + Math.abs(System.nanoTime() % 89_999_999L));
    }

    private String generarCorreo(String cedula) {
        return "test" + cedula + "@serenity.test";
    }

    // ── Background ──────────────────────────────────────────────────────────

    @Given("el usuario esta autenticado en el modulo de registro de empleados")
    public void elUsuarioEstaAutenticadoEnElModuloDeRegistroDeEmpleados() {
        // Login real para obtener un JWT valido firmado → necesario para peticiones al backend
        loginPage.open();
        loginPage.enterUsername("admin");
        loginPage.enterPassword("admin1412");
        loginPage.clickLoginButton();
        // Esperar que el dashboard cargue (confirma que el JWT real esta en localStorage)
        dashboardPage.waitForLogoutButton();
        // Navegar al modulo — Administrador tiene acceso a /rrhh
        registrarEmpleadoPage.open();
    }

    // ── Escenario 1: Registro exitoso ────────────────────────────────────────

    @When("el usuario registra un empleado con todos los campos obligatorios")
    public void elUsuarioRegistraUnEmpleadoConTodosLosCamposObligatorios() {
        String cedula = generarCedula();
        registrarEmpleadoPage.fillCamposObligatorios(
            cedula, "Juan Perez", generarCorreo(cedula),
            "Calle 123 #45-67", "Bogota", "3001234567");
        registrarEmpleadoPage.submit();
    }

    // ── Escenario 2: Registro con telefono secundario ────────────────────────

    @When("el usuario registra un empleado incluyendo el telefono secundario")
    public void elUsuarioRegistraUnEmpleadoIncluyendoElTelefonoSecundario() {
        String cedula = generarCedula();
        registrarEmpleadoPage.fillCamposObligatorios(
            cedula, "Maria Lopez", generarCorreo(cedula),
            "Carrera 10 #20-30", "Medellin", "3109876543");
        registrarEmpleadoPage.fillTelefono2("6012345678");
        registrarEmpleadoPage.submit();
    }

    // ── Escenario 3 y 4: Pre-creacion del empleado ───────────────────────────

    @Given("el usuario registra previamente un empleado en el sistema")
    public void elUsuarioRegistraPreviamenteUnEmpleadoEnElSistema() {
        lastCedula = generarCedula();
        lastCorreo = generarCorreo(lastCedula);
        registrarEmpleadoPage.fillCamposObligatorios(
            lastCedula, "Empleado Previo", lastCorreo,
            "Av. Siempre Viva 742", "Cali", "3201112233");
        registrarEmpleadoPage.submit();
        // Esperar confirmacion antes de continuar
        registrarEmpleadoPage.getSuccessMessage();
    }

    @When("el usuario intenta registrar otro empleado con la misma cedula")
    public void elUsuarioIntentaRegistrarOtroEmpleadoConLaMismaCedula() {
        // Misma cedula, correo diferente para no mezclar errores
        String otroCorreo = generarCorreo(generarCedula());
        registrarEmpleadoPage.fillCamposObligatorios(
            lastCedula, "Otro Empleado", otroCorreo,
            "Calle Falsa 456", "Barranquilla", "3154445566");
        registrarEmpleadoPage.submit();
    }

    @When("el usuario intenta registrar otro empleado con el mismo correo")
    public void elUsuarioIntentaRegistrarOtroEmpleadoConElMismoCorreo() {
        // Cedula diferente, mismo correo
        String otraCedula = generarCedula();
        registrarEmpleadoPage.fillCamposObligatorios(
            otraCedula, "Otro Empleado", lastCorreo,
            "Calle Falsa 789", "Cartagena", "3167778899");
        registrarEmpleadoPage.submit();
    }

    // ── Escenario 5: Campos vacios ───────────────────────────────────────────

    @When("el usuario intenta registrar un empleado sin diligenciar los campos obligatorios")
    public void elUsuarioIntentaRegistrarUnEmpleadoSinDiligenciarLosCamposObligatorios() {
        // Enviar el formulario completamente vacio
        registrarEmpleadoPage.submit();
    }

    // ── Escenario 6: Acceso no autorizado ────────────────────────────────────

    @When("el usuario intenta acceder al modulo de registro de empleados")
    public void elUsuarioIntentaAccederAlModuloDeRegistroDeEmpleados() {
        // open() usa @DefaultUrl → roleGuard deniega con rol Ventas → /unauthorized
        registrarEmpleadoPage.open();
    }

    // ── Assertions compartidas ───────────────────────────────────────────────

    @Then("el sistema muestra el mensaje de exito {string}")
    public void elSistemaMuestraElMensajeDeExito(String mensajeEsperado) {
        // getSuccessMessage() espera con waitFor() hasta que Angular renderice el @if
        String mensajeActual = registrarEmpleadoPage.getSuccessMessage();
        assertThat(normalizarTexto(mensajeActual))
            .as("El mensaje de exito debe coincidir con el esperado")
            .containsIgnoringCase(normalizarTexto(mensajeEsperado));
    }

    @Then("el sistema muestra el mensaje de error del empleado {string}")
    public void elSistemaMuestraElMensajeDeErrorDelEmpleado(String mensajeEsperado) {
        // getErrorMessage() espera con waitFor() — necesario para errores 409 del backend
        String mensajeActual = registrarEmpleadoPage.getErrorMessage();
        assertThat(normalizarTexto(mensajeActual))
            .as("El mensaje de error debe coincidir con el esperado")
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
