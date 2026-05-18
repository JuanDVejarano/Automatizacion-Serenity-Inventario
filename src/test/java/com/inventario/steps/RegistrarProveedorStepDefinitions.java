package com.inventario.steps;

import com.inventario.pages.DashboardPage;
import com.inventario.pages.LoginPage;
import com.inventario.pages.RegistrarProveedorPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.assertj.core.api.Assertions.assertThat;

public class RegistrarProveedorStepDefinitions {

    LoginPage loginPage;
    DashboardPage dashboardPage;
    RegistrarProveedorPage registrarProveedorPage;

    private long   lastNit;
    private String lastNombre;
    private String lastCorreo;

    private long   generateNit()    { return Math.abs(System.nanoTime() % 9_999_999_999L); }
    private String generateNombre() { return "Prov " + Math.abs(System.nanoTime() % 999_999L); }
    private String generateCorreo() { return "prov" + Math.abs(System.nanoTime() % 999_999L) + "@test.com"; }

    // ── Background ──────────────────────────────────────────────────────────

    @Given("el usuario ha iniciado sesion en el modulo de proveedores")
    public void elUsuarioHaIniciadoSesionEnElModuloDeProveedores() {
        loginPage.open();
        loginPage.enterUsername("admin");
        loginPage.enterPassword("admin1412");
        loginPage.clickLoginButton();
        dashboardPage.waitForLogoutButton();
        registrarProveedorPage.open();
        registrarProveedorPage.waitForFormToLoad();
    }

    // ── Given: proveedor base para duplicados ────────────────────────────────

    @Given("existe un proveedor registrado en el sistema")
    public void existeUnProveedorRegistradoEnElSistema() {
        lastNit    = generateNit();
        lastNombre = generateNombre();
        lastCorreo = generateCorreo();

        registrarProveedorPage.fillNit(lastNit);
        registrarProveedorPage.fillNombre(lastNombre);
        registrarProveedorPage.fillDireccion("Calle 10 # 20-30");
        registrarProveedorPage.fillTelefono("3001234567");
        registrarProveedorPage.fillCorreo(lastCorreo);
        registrarProveedorPage.submit();
        registrarProveedorPage.getSuccessMessage();

        // El formulario se limpia tras el éxito; volver a esperar el botón
        registrarProveedorPage.waitForFormToLoad();
    }

    // ── Escenario 1: Registro exitoso ────────────────────────────────────────

    @When("el usuario registra un proveedor con todos los campos obligatorios")
    public void elUsuarioRegistraUnProveedorConTodosLosCamposObligatorios() {
        registrarProveedorPage.fillNit(generateNit());
        registrarProveedorPage.fillNombre(generateNombre());
        registrarProveedorPage.fillDireccion("Carrera 5 # 10-20");
        registrarProveedorPage.fillTelefono("3109876543");
        registrarProveedorPage.fillCorreo(generateCorreo());
        registrarProveedorPage.submit();
    }

    // ── Escenario 2: Registro con campos opcionales ──────────────────────────

    @When("el usuario registra un proveedor con todos los campos incluyendo opcionales")
    public void elUsuarioRegistraUnProveedorConTodosLosCamposIncluyendoOpcionales() {
        registrarProveedorPage.fillNit(generateNit());
        registrarProveedorPage.fillNombre(generateNombre());
        registrarProveedorPage.fillDireccion("Avenida 1 # 2-3");
        registrarProveedorPage.fillDireccion2("Piso 3, Oficina 301");
        registrarProveedorPage.fillTelefono("3201234567");
        registrarProveedorPage.fillTelefono2("6014567890");
        registrarProveedorPage.fillCorreo(generateCorreo());
        registrarProveedorPage.submit();
    }

    // ── Escenario 3: NIT duplicado ───────────────────────────────────────────

    @When("el usuario intenta registrar un proveedor con el mismo NIT")
    public void elUsuarioIntentaRegistrarUnProveedorConElMismoNIT() {
        registrarProveedorPage.fillNit(lastNit);           // mismo NIT
        registrarProveedorPage.fillNombre(generateNombre()); // nombre distinto
        registrarProveedorPage.fillDireccion("Calle 99");
        registrarProveedorPage.fillTelefono("3000000001");
        registrarProveedorPage.fillCorreo(generateCorreo()); // correo distinto
        registrarProveedorPage.submit();
    }

    // ── Escenario 4: Nombre duplicado ────────────────────────────────────────

    @When("el usuario intenta registrar un proveedor con el mismo nombre")
    public void elUsuarioIntentaRegistrarUnProveedorConElMismoNombre() {
        registrarProveedorPage.fillNit(generateNit());     // NIT distinto
        registrarProveedorPage.fillNombre(lastNombre);       // mismo nombre
        registrarProveedorPage.fillDireccion("Calle 99");
        registrarProveedorPage.fillTelefono("3000000002");
        registrarProveedorPage.fillCorreo(generateCorreo()); // correo distinto
        registrarProveedorPage.submit();
    }

    // ── Escenario 5: Correo duplicado ────────────────────────────────────────

    @When("el usuario intenta registrar un proveedor con el mismo correo")
    public void elUsuarioIntentaRegistrarUnProveedorConElMismoCorreo() {
        registrarProveedorPage.fillNit(generateNit());     // NIT distinto
        registrarProveedorPage.fillNombre(generateNombre()); // nombre distinto
        registrarProveedorPage.fillDireccion("Calle 99");
        registrarProveedorPage.fillTelefono("3000000003");
        registrarProveedorPage.fillCorreo(lastCorreo);       // mismo correo
        registrarProveedorPage.submit();
    }

    // ── Escenario 6: Campos vacíos ───────────────────────────────────────────

    @When("el usuario intenta registrar un proveedor sin diligenciar los campos obligatorios")
    public void elUsuarioIntentaRegistrarUnProveedorSinDiligenciarLosCamposObligatorios() {
        registrarProveedorPage.submit();
    }

    // ── Escenario 7: Acceso no autorizado ────────────────────────────────────

    @When("el usuario intenta acceder al modulo de registro de proveedores")
    public void elUsuarioIntentaAccederAlModuloDeRegistroDeProveedores() {
        registrarProveedorPage.open();
    }

    // ── Assertions ────────────────────────────────────────────────────────────

    @Then("el sistema muestra el mensaje de exito de proveedor {string}")
    public void elSistemaMuestraElMensajeDeExitoDeProveedor(String mensajeEsperado) {
        String mensajeActual = registrarProveedorPage.getSuccessMessage();
        assertThat(normalizarTexto(mensajeActual))
            .as("El mensaje de exito de proveedor debe coincidir")
            .containsIgnoringCase(normalizarTexto(mensajeEsperado));
    }

    @Then("el sistema muestra el mensaje de error de proveedor {string}")
    public void elSistemaMuestraElMensajeDeErrorDeProveedor(String mensajeEsperado) {
        String mensajeActual = registrarProveedorPage.getErrorMessage();
        assertThat(normalizarTexto(mensajeActual))
            .as("El mensaje de error de proveedor debe coincidir")
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
