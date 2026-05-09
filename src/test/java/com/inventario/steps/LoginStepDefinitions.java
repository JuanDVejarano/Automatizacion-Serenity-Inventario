package com.inventario.steps;

import com.inventario.pages.LoginPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.assertj.core.api.Assertions.assertThat;

public class LoginStepDefinitions {

    LoginPage loginPage;

    @Given("el usuario navega a la pagina de login")
    public void elUsuarioNavegaALaPaginaDeLogin() {
        loginPage.open();
    }

    @When("el usuario ingresa el nombre de usuario {string}")
    public void elUsuarioIngresaElNombreDeUsuario(String username) {
        loginPage.enterUsername(username);
    }

    @And("el usuario ingresa la contrasena {string}")
    public void elUsuarioIngresaLaContrasena(String password) {
        loginPage.enterPassword(password);
    }

    @When("el usuario hace clic en el boton iniciar sesion")
    public void elUsuarioHaceClicEnElBotonIniciarSesion() {
        loginPage.clickLoginButton();
    }

    @Then("el sistema redirige al dashboard del usuario")
    public void elSistemaRedirigeSAlDashboard() {
        String currentUrl = loginPage.getCurrentUrl();
        assertThat(currentUrl)
            .as("Debe redirigir al dashboard tras login exitoso")
            .contains("/dashboard");
    }

    @Then("el sistema muestra el mensaje de error {string}")
    public void elSistemaMuestraElMensajeDeError(String mensajeEsperado) {
        assertThat(loginPage.isErrorAlertVisible())
            .as("El mensaje de error debe ser visible en pantalla")
            .isTrue();

        String mensajeActual = loginPage.getErrorMessage();

        // Normalizar tildes para la comparacion (el feature usa texto sin tildes)
        String mensajeActualNorm = normalizarTexto(mensajeActual);
        String mensajeEsperadoNorm = normalizarTexto(mensajeEsperado);

        assertThat(mensajeActualNorm)
            .as("El mensaje de error debe coincidir con el esperado")
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
