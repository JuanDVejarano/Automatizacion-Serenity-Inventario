package com.inventario.steps;

import com.inventario.pages.DashboardPage;
import com.inventario.pages.LoginPage;
import io.cucumber.java.After;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.assertj.core.api.Assertions.assertThat;

public class LogoutStepDefinitions {

    DashboardPage dashboardPage;
    LoginPage loginPage;

    @After
    public void cerrarPestanasExtra() {
        try {
            int tabs = dashboardPage.getOpenTabCount();
            for (int i = tabs - 1; i >= 1; i--) {
                dashboardPage.switchToTab(i);
                dashboardPage.closeCurrentTab();
            }
            if (tabs > 1) {
                dashboardPage.switchToTab(0);
            }
        } catch (Exception ignored) {
        }
    }

    @Given("el usuario tiene una sesion activa en el dashboard")
    public void elUsuarioTieneUnaSesionActivaEnElDashboard() {
        // open() navega a /dashboard → authGuard redirige a /login (sin token aún)
        dashboardPage.open();
        // Inyectar token válido mientras estamos en /login
        dashboardPage.setValidSession();
        // Navegar al dashboard de nuevo — ahora el guard pasa
        dashboardPage.navigateToDashboard();
    }

    @When("el usuario hace clic en el boton cerrar sesion")
    public void elUsuarioHaceClicEnElBotonCerrarSesion() {
        dashboardPage.clickLogoutButton();
    }

    @Then("el sistema elimina el token JWT del almacenamiento local")
    public void elSistemaEliminaElTokenJwtDelAlmacenamientoLocal() {
        assertThat(dashboardPage.isTokenPresent())
            .as("El token JWT debe haber sido eliminado del localStorage tras el logout")
            .isFalse();
    }

    @And("el sistema redirige al usuario a la pantalla de login")
    public void elSistemaRedirigeAlUsuarioALaPantallaDeLogin() {
        assertThat(dashboardPage.getCurrentUrl())
            .as("Debe redirigir a /login tras cerrar sesion")
            .contains("/login");
    }

    @And("el usuario no puede acceder al dashboard sin autenticacion")
    public void elUsuarioNoPuedeAccederAlDashboardSinAutenticacion() {
        dashboardPage.navigateToDashboard();
        assertThat(dashboardPage.getCurrentUrl())
            .as("Sin token valido, el guard debe redirigir al login al intentar acceder al dashboard")
            .contains("/login");
    }

    @Given("el usuario ha cerrado su sesion")
    public void elUsuarioHaCerradoSuSesion() {
        dashboardPage.clearSession();
    }

    @When("el usuario intenta acceder al dashboard directamente")
    public void elUsuarioIntentaAccederAlDashboardDirectamente() {
        dashboardPage.navigateToDashboard();
    }

    @Then("el sistema redirige automaticamente a la pantalla de login")
    public void elSistemaRedirigeSAutomaticamenteALaPantallaDeLogin() {
        assertThat(dashboardPage.getCurrentUrl())
            .as("Sin token valido, el guard debe redirigir automaticamente al login")
            .contains("/login");
    }

    @Given("el usuario tiene el sistema abierto en otra pestana")
    public void elUsuarioTieneElSistemaAbiertoEnOtraPestana() {
        // Abrir Tab 2 — el token ya está en localStorage (compartido entre pestañas)
        dashboardPage.openDashboardInNewTab();
        // Cambiar a Tab 2 y esperar que Angular cargue el dashboard
        dashboardPage.switchToTab(1);
        dashboardPage.waitForLogoutButton();
        // Volver a Tab 1
        dashboardPage.switchToTab(0);
    }

    @When("el usuario cierra sesion desde la pestana actual")
    public void elUsuarioCierraSesionDesdeLaPestanaActual() {
        dashboardPage.clickLogoutButton();
    }

    @Then("las demas pestanas son redirigidas al login con el mensaje {string}")
    public void lasDemasPestanasSonRedirigirAlLoginConElMensaje(String mensajeEsperado) {
        // Cambiar a Tab 2: el storage event ya disparó la redirección Angular
        dashboardPage.switchToTab(1);

        // getSessionWarningMessage() espera con waitFor() internamente
        String mensajeActual = loginPage.getSessionWarningMessage();

        assertThat(dashboardPage.getCurrentUrl())
            .as("La segunda pestana debe haber sido redirigida al login")
            .contains("/login");

        String mensajeActualNorm = normalizarTexto(mensajeActual);
        String mensajeEsperadoNorm = normalizarTexto(mensajeEsperado);

        assertThat(mensajeActualNorm)
            .as("El mensaje de sesion cerrada en otra pestana debe coincidir")
            .containsIgnoringCase(mensajeEsperadoNorm);

        // Cleanup: cerrar Tab 2 y devolver foco a Tab 1
        dashboardPage.closeCurrentTab();
        dashboardPage.switchToTab(0);
    }

    private String normalizarTexto(String texto) {
        return texto
            .replace("á", "a").replace("é", "e").replace("í", "i")
            .replace("ó", "o").replace("ú", "u").replace("ñ", "n")
            .replace("Á", "A").replace("É", "E").replace("Í", "I")
            .replace("Ó", "O").replace("Ú", "U").replace("Ñ", "N");
    }
}
