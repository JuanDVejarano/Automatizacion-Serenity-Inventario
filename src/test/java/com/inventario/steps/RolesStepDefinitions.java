package com.inventario.steps;

import com.inventario.pages.DashboardPage;
import com.inventario.pages.UnauthorizedPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.assertj.core.api.Assertions.assertThat;

public class RolesStepDefinitions {

    DashboardPage dashboardPage;
    UnauthorizedPage unauthorizedPage;

    // Mapea nombres sin tilde (feature) al valor exacto del token JWT
    private String resolverRol(String rolNormalizado) {
        switch (rolNormalizado) {
            case "Tesoreria":       return "Tesorería";
            case "Produccion":      return "Producción";
            case "Recursos Humanos": return "Recursos Humanos";
            case "Administrador":   return "Administrador";
            case "Ventas":          return "Ventas";
            default:                return rolNormalizado;
        }
    }

    @Given("el usuario inicia sesion con el rol {string}")
    public void elUsuarioIniciaSesionConElRol(String rol) {
        String rolReal = resolverRol(rol);
        // open() navega a /dashboard → guard redirige a /login
        dashboardPage.open();
        // Inyectar token con el rol indicado mientras estamos en /login
        dashboardPage.setSessionWithRole(rolReal);
    }

    @When("el usuario accede al dashboard")
    public void elUsuarioAccedeAlDashboard() {
        dashboardPage.navigateToDashboard();
    }

    @Then("el modulo {string} esta {string} para ese rol")
    public void elModuloEstaParaEseRol(String modulo, String estado) {
        if ("habilitado".equals(estado)) {
            assertThat(dashboardPage.isModuleEnabled(modulo))
                .as("El modulo '" + modulo + "' debe aparecer habilitado (enlace activo) para este rol")
                .isTrue();
        } else {
            assertThat(dashboardPage.isModuleDisabled(modulo))
                .as("El modulo '" + modulo + "' debe aparecer deshabilitado (Sin acceso) para este rol")
                .isTrue();
        }
    }

    @Then("no existe una opcion de gestion de roles en el sistema")
    public void noExisteUnaOpcionDeGestionDeRolesEnElSistema() {
        assertThat(dashboardPage.hasRolesManagementModule())
            .as("No debe existir ningun modulo de gestion de roles en el dashboard")
            .isFalse();
    }

    @When("el usuario intenta acceder directamente a la ruta {string}")
    public void elUsuarioIntentaAccederDirectamenteALaRuta(String ruta) {
        dashboardPage.navigateTo(ruta);
    }

    @Then("el sistema muestra la pagina de acceso denegado")
    public void elSistemaMuestraLaPaginaDeAccesoDenegado() {
        assertThat(unauthorizedPage.getCurrentUrl())
            .as("El roleGuard debe redirigir a /unauthorized al acceder sin permisos")
            .contains("/unauthorized");
    }

    @And("el sistema muestra el mensaje {string}")
    public void elSistemaMuestraElMensaje(String mensajeEsperado) {
        assertThat(unauthorizedPage.isAccessDeniedMessageVisible())
            .as("El mensaje de acceso denegado debe ser visible")
            .isTrue();

        String mensajeActual = unauthorizedPage.getAccessDeniedMessage();
        String mensajeActualNorm = normalizarTexto(mensajeActual);
        String mensajeEsperadoNorm = normalizarTexto(mensajeEsperado);

        assertThat(mensajeActualNorm)
            .as("El mensaje de acceso denegado debe coincidir con el esperado")
            .containsIgnoringCase(mensajeEsperadoNorm);
    }

    private String normalizarTexto(String texto) {
        return texto
            .replace("á", "a").replace("é", "e").replace("í", "i")
            .replace("ó", "o").replace("ú", "u").replace("ñ", "n")
            .replace("Á", "A").replace("É", "E").replace("Í", "I")
            .replace("Ó", "O").replace("Ú", "U").replace("Ñ", "N");
    }
}
