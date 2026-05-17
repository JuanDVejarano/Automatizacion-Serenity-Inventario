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

public class ActivarDesactivarUsuarioStepDefinitions {

    LoginPage loginPage;
    DashboardPage dashboardPage;
    RegistrarEmpleadoPage registrarEmpleadoPage;
    CrearUsuarioPage crearUsuarioPage;
    UsuariosPage usuariosPage;

    // Username del usuario de prueba — nunca se toca el admin
    private String testUsername;

    private String generateCedula() {
        return String.valueOf(10_000_000L + Math.abs(System.nanoTime() % 89_999_999L));
    }

    // Crea un empleado + usuario con rol Recursos Humanos y navega a /rrhh/usuarios
    private void crearUsuarioDePrueba() {
        String cedula = generateCedula();
        testUsername = "rh" + cedula.substring(0, 7);

        registrarEmpleadoPage.open();
        registrarEmpleadoPage.fillCamposObligatorios(
            cedula, "Emp Toggle " + cedula,
            "toggle" + cedula + "@serenity.test",
            "Dir Toggle", "Bogota", "3000000003");
        registrarEmpleadoPage.submit();
        registrarEmpleadoPage.getSuccessMessage();

        // Crear usuario con rol Recursos Humanos (nunca Administrador)
        crearUsuarioPage.open();
        crearUsuarioPage.waitForFormToLoad();
        crearUsuarioPage.fillUsuario(testUsername);
        crearUsuarioPage.fillClave("clave1234");
        crearUsuarioPage.selectRoleByName("Recursos Humanos");
        crearUsuarioPage.selectFirstEmployee();
        crearUsuarioPage.submit();
        crearUsuarioPage.getSuccessMessage();

        usuariosPage.open();
        usuariosPage.waitForList();
    }

    // ── Precondiciones ───────────────────────────────────────────────────────

    @Given("existe un usuario de prueba activo para la gestion de estado")
    public void existeUnUsuarioDePruebaActivoParaLaGestionDeEstado() {
        // El usuario recién creado es activo por defecto (esActivo = true)
        crearUsuarioDePrueba();
    }

    @Given("existe un usuario de prueba inactivo para la gestion de estado")
    public void existeUnUsuarioDePruebaInactivoParaLaGestionDeEstado() {
        crearUsuarioDePrueba();
        // Desactivar inmediatamente para que el escenario parta desde inactivo
        usuariosPage.clickToggleForUser(testUsername);
        assertThat(usuariosPage.waitForUserBadge(testUsername, "badge--inactivo"))
            .as("El usuario de prueba debe quedar inactivo antes del escenario")
            .isTrue();
    }

    // ── Acciones ─────────────────────────────────────────────────────────────

    @When("el administrador desactiva al usuario de prueba")
    public void elAdministradorDesactivaAlUsuarioDePrueba() {
        usuariosPage.clickToggleForUser(testUsername);
    }

    @When("el administrador activa al usuario de prueba")
    public void elAdministradorActivaAlUsuarioDePrueba() {
        usuariosPage.clickToggleForUser(testUsername);
    }

    // ── Assertions ────────────────────────────────────────────────────────────

    @Then("el usuario de prueba aparece como Inactivo en la lista de usuarios")
    public void elUsuarioDePruebaApareceComoInactivoEnLaListaDeUsuarios() {
        assertThat(usuariosPage.waitForUserBadge(testUsername, "badge--inactivo"))
            .as("El badge del usuario '" + testUsername + "' debe mostrar Inactivo tras desactivarlo")
            .isTrue();
    }

    @Then("el usuario de prueba aparece como Activo en la lista de usuarios")
    public void elUsuarioDePruebaApareceComoActivoEnLaListaDeUsuarios() {
        assertThat(usuariosPage.waitForUserBadge(testUsername, "badge--activo"))
            .as("El badge del usuario '" + testUsername + "' debe mostrar Activo tras activarlo")
            .isTrue();
    }
}
