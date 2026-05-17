package com.inventario.steps;

import com.inventario.pages.DashboardPage;
import com.inventario.pages.LoginPage;
import com.inventario.pages.RegistrarMateriaPrimaPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.assertj.core.api.Assertions.assertThat;

public class RegistrarMateriaPrimaStepDefinitions {

    LoginPage loginPage;
    DashboardPage dashboardPage;
    RegistrarMateriaPrimaPage registrarMateriaPrimaPage;

    private String lastNombre;

    private String generateNombre() {
        return "MP " + Math.abs(System.nanoTime() % 999_999L);
    }

    // ── Background ──────────────────────────────────────────────────────────

    @Given("el usuario ha iniciado sesion en el modulo de materia prima")
    public void elUsuarioHaIniciadoSesionEnElModuloDeMateriaPrima() {
        loginPage.open();
        loginPage.enterUsername("admin");
        loginPage.enterPassword("admin1412");
        loginPage.clickLoginButton();
        dashboardPage.waitForLogoutButton();
        registrarMateriaPrimaPage.open();
        registrarMateriaPrimaPage.waitForFormToLoad();
    }

    // ── Escenario: Registro exitoso ──────────────────────────────────────────

    @When("el usuario registra una materia prima con nombre valido")
    public void elUsuarioRegistraUnaMateriaPrimaConNombreValido() {
        registrarMateriaPrimaPage.fillNombre(generateNombre());
        registrarMateriaPrimaPage.submit();
    }

    // ── Escenario: Registro con descripcion ─────────────────────────────────

    @When("el usuario registra una materia prima con nombre y descripcion")
    public void elUsuarioRegistraUnaMateriaPrimaConNombreYDescripcion() {
        registrarMateriaPrimaPage.fillNombre(generateNombre());
        registrarMateriaPrimaPage.fillDescripcion("Insumo de alta resistencia para uso quirurgico");
        registrarMateriaPrimaPage.submit();
    }

    // ── Escenario: Nombre duplicado ──────────────────────────────────────────

    @Given("existe una materia prima registrada en el sistema")
    public void existeUnaMateriaPrimaRegistradaEnElSistema() {
        lastNombre = generateNombre();
        registrarMateriaPrimaPage.fillNombre(lastNombre);
        registrarMateriaPrimaPage.submit();
        registrarMateriaPrimaPage.getSuccessMessage();
        registrarMateriaPrimaPage.open();
        registrarMateriaPrimaPage.waitForFormToLoad();
    }

    @When("el usuario intenta registrar una materia prima con el mismo nombre")
    public void elUsuarioIntentaRegistrarUnaMateriaPrimaConElMismoNombre() {
        registrarMateriaPrimaPage.fillNombre(lastNombre);
        registrarMateriaPrimaPage.submit();
    }

    // ── Escenario: Campo nombre vacio ────────────────────────────────────────

    @When("el usuario intenta registrar una materia prima sin diligenciar el nombre")
    public void elUsuarioIntentaRegistrarUnaMateriaPrimaSinDiligenciarElNombre() {
        registrarMateriaPrimaPage.submit();
    }

    // ── Escenario: Acceso no autorizado ─────────────────────────────────────

    @When("el usuario intenta acceder al modulo de registro de materia prima")
    public void elUsuarioIntentaAccederAlModuloDeRegistroDeMateriaPrima() {
        registrarMateriaPrimaPage.open();
    }

    // ── Assertions ────────────────────────────────────────────────────────────

    @Then("el sistema muestra el mensaje de exito de materia prima {string}")
    public void elSistemaMuestraElMensajeDeExitoDeMateriaPrima(String mensajeEsperado) {
        String mensajeActual = registrarMateriaPrimaPage.getSuccessMessage();
        assertThat(normalizarTexto(mensajeActual))
            .as("El mensaje de exito de materia prima debe coincidir")
            .containsIgnoringCase(normalizarTexto(mensajeEsperado));
    }

    @Then("el sistema muestra el mensaje de error de materia prima {string}")
    public void elSistemaMuestraElMensajeDeErrorDeMateriaPrima(String mensajeEsperado) {
        String mensajeActual = registrarMateriaPrimaPage.getErrorMessage();
        assertThat(normalizarTexto(mensajeActual))
            .as("El mensaje de error de materia prima debe coincidir")
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
