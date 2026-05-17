package com.inventario.steps;

import com.inventario.pages.CrearUsuarioPage;
import com.inventario.pages.DashboardPage;
import com.inventario.pages.LoginPage;
import com.inventario.pages.RegistrarEmpleadoPage;
import com.inventario.pages.UsuariosPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.assertj.core.api.Assertions.assertThat;

public class AsignarRolStepDefinitions {

    LoginPage loginPage;
    DashboardPage dashboardPage;
    RegistrarEmpleadoPage registrarEmpleadoPage;
    CrearUsuarioPage crearUsuarioPage;
    UsuariosPage usuariosPage;

    // Username del usuario de prueba — se usa para apuntar al modal correcto
    // y evitar cambiar el rol del admin u otros usuarios de la lista
    private String createdUsername;

    private String generateCedula() {
        return String.valueOf(10_000_000L + Math.abs(System.nanoTime() % 89_999_999L));
    }

    // ── Precondicion: usuario existente ─────────────────────────────────────

    @Given("existe un usuario registrado para la prueba de asignacion de rol")
    public void existeUnUsuarioRegistradoParaLaPruebaDeAsignacionDeRol() {
        // 1. Crear empleado
        String cedula = generateCedula();
        registrarEmpleadoPage.open();
        registrarEmpleadoPage.fillCamposObligatorios(
            cedula, "Emp Rol " + cedula,
            "rol" + cedula + "@serenity.test",
            "Dir Rol", "Bogota", "3000000002");
        registrarEmpleadoPage.submit();
        registrarEmpleadoPage.getSuccessMessage();

        // 2. Crear usuario — el módulo RRHH ya está cargado del Background
        crearUsuarioPage.open();
        crearUsuarioPage.waitForFormToLoad();
        createdUsername = "usr" + cedula.substring(0, 7);
        crearUsuarioPage.fillUsuario(createdUsername);
        crearUsuarioPage.fillClave("clave1234");
        crearUsuarioPage.selectFirstRole();
        crearUsuarioPage.selectFirstEmployee();
        crearUsuarioPage.submit();
        crearUsuarioPage.getSuccessMessage();

        // 3. Navegar a la lista de usuarios
        usuariosPage.open();
        usuariosPage.waitForList();
    }

    // ── Acciones en la lista y modal ─────────────────────────────────────────

    @When("el usuario abre el modal de asignacion de rol del primer usuario")
    public void elUsuarioAbreElModalDeAsignacionDeRolDelPrimerUsuario() {
        // Apunta al usuario de prueba creado en el Given, no al admin u otros usuarios
        usuariosPage.clickEditButtonForUser(createdUsername);
        usuariosPage.waitForModal();
    }

    @And("el usuario selecciona un rol diferente al actual")
    public void elUsuarioSeleccionaUnRolDiferenteAlActual() {
        usuariosPage.selectDifferentRole();
    }

    @And("el usuario limpia la seleccion de rol en el modal")
    public void elUsuarioLimpiaLaSeleccionDeRolEnElModal() {
        usuariosPage.clearRoleSelection();
    }

    @And("el usuario confirma la asignacion de rol")
    public void elUsuarioConfirmaLaAsignacionDeRol() {
        usuariosPage.submitRoleAssignment();
    }

    // ── Acceso no autorizado ──────────────────────────────────────────────────

    @When("el usuario intenta acceder al modulo de gestion de usuarios")
    public void elUsuarioIntentaAccederAlModuloDeGestionDeUsuarios() {
        usuariosPage.open();
    }

    // ── Assertions ────────────────────────────────────────────────────────────

    @Then("el sistema muestra el mensaje de exito de asignacion {string}")
    public void elSistemaMuestraElMensajeDeExitoDeAsignacion(String mensajeEsperado) {
        String mensajeActual = usuariosPage.getModalSuccessMessage();
        assertThat(normalizarTexto(mensajeActual))
            .as("El mensaje de exito de asignacion de rol debe coincidir")
            .containsIgnoringCase(normalizarTexto(mensajeEsperado));
    }

    @Then("el sistema muestra el mensaje de error de asignacion {string}")
    public void elSistemaMuestraElMensajeDeErrorDeAsignacion(String mensajeEsperado) {
        String mensajeActual = usuariosPage.getModalErrorMessage();
        assertThat(normalizarTexto(mensajeActual))
            .as("El mensaje de error de asignacion de rol debe coincidir")
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
