package com.inventario.steps;

import com.inventario.pages.CrearUsuarioPage;
import com.inventario.pages.DashboardPage;
import com.inventario.pages.LoginPage;
import com.inventario.pages.RegistrarEmpleadoPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.assertj.core.api.Assertions.assertThat;

public class CrearUsuarioStepDefinitions {

    LoginPage loginPage;
    DashboardPage dashboardPage;
    RegistrarEmpleadoPage registrarEmpleadoPage;
    CrearUsuarioPage crearUsuarioPage;

    // Cedula del empleado creado para ser reutilizado en el mismo escenario
    private String employeeCedula;
    // Username generado para el escenario de duplicado
    private String lastUsername;

    private String generateCedula() {
        return String.valueOf(10_000_000L + Math.abs(System.nanoTime() % 89_999_999L));
    }

    private String generateUsername() {
        return "usr" + Math.abs(System.nanoTime() % 999_999L);
    }

    // ── Background ──────────────────────────────────────────────────────────

    @Given("el administrador ha iniciado sesion para gestion de usuarios")
    public void elAdministradorHaIniciadoSesionParaGestionDeUsuarios() {
        loginPage.open();
        loginPage.enterUsername("admin");
        loginPage.enterPassword("admin1412");
        loginPage.clickLoginButton();
        dashboardPage.waitForLogoutButton();
        // Navegar al módulo con token real ya en localStorage.
        // Pre-carga el lazy module de RRHH y ambas llamadas backend (roles + empleados)
        // para que los escenarios no esperen lazy-load desde cero con browser fresco.
        crearUsuarioPage.open();
        crearUsuarioPage.waitForFormToLoad();
    }

    // ── Precondicion: empleado activo ────────────────────────────────────────

    @Given("existe un empleado activo disponible para asignar usuario")
    public void existeUnEmpleadoActivoDisponibleParaAsignarUsuario() {
        employeeCedula = generateCedula();
        String correo = "empusr" + employeeCedula + "@serenity.test";
        registrarEmpleadoPage.open();
        registrarEmpleadoPage.fillCamposObligatorios(
            employeeCedula, "Empleado Usuario " + employeeCedula,
            correo, "Dir Test 1", "Bogota", "3001010101");
        registrarEmpleadoPage.submit();
        registrarEmpleadoPage.getSuccessMessage(); // espera confirmacion del backend
        // Navegar al formulario de usuario y esperar que los selects se pueblen
        crearUsuarioPage.open();
        crearUsuarioPage.waitForFormToLoad();
    }

    // ── Escenario 1: Creacion exitosa ────────────────────────────────────────

    @When("el usuario diligencia el formulario de creacion de usuario")
    public void elUsuarioDiligenciaElFormularioDeCreacionDeUsuario() {
        crearUsuarioPage.fillUsuario(generateUsername());
        crearUsuarioPage.fillClave("clave1234");
        crearUsuarioPage.selectFirstRole();
        crearUsuarioPage.selectFirstEmployee();
        crearUsuarioPage.submit();
    }

    // ── Escenario 2: Empleado con multiples usuarios ─────────────────────────

    @When("el usuario crea un primer usuario para ese empleado con rol {string}")
    public void elUsuarioCreaUnPrimerUsuarioParaEseEmpleadoConRol(String rol) {
        crearUsuarioPage.fillUsuario(generateUsername());
        crearUsuarioPage.fillClave("clave1234");
        crearUsuarioPage.selectRoleByName(rol);
        crearUsuarioPage.selectFirstEmployee();
        crearUsuarioPage.submit();
        // Esperar exito antes de proceder con el segundo usuario
        crearUsuarioPage.getSuccessMessage();
    }

    @And("el usuario crea un segundo usuario para el mismo empleado con rol {string}")
    public void elUsuarioCreaUnSegundoUsuarioParaElMismoEmpleadoConRol(String rol) {
        // El formulario se limpio tras el primer exito — rellenar de nuevo
        crearUsuarioPage.fillUsuario(generateUsername());
        crearUsuarioPage.fillClave("clave5678");
        crearUsuarioPage.selectRoleByName(rol);
        crearUsuarioPage.selectFirstEmployee();
        crearUsuarioPage.submit();
    }

    // ── Escenario 3: Nombre duplicado ────────────────────────────────────────

    @And("el usuario crea un usuario de prueba para verificar duplicado")
    public void elUsuarioCreaUnUsuarioDePruebaParaVerificarDuplicado() {
        lastUsername = generateUsername();
        crearUsuarioPage.fillUsuario(lastUsername);
        crearUsuarioPage.fillClave("clave1234");
        crearUsuarioPage.selectFirstRole();
        crearUsuarioPage.selectFirstEmployee();
        crearUsuarioPage.submit();
        crearUsuarioPage.getSuccessMessage(); // espera exito y limpieza del formulario
    }

    @When("el usuario intenta crear otro usuario con el mismo nombre de usuario")
    public void elUsuarioIntentaCrearOtroUsuarioConElMismoNombreDeUsuario() {
        crearUsuarioPage.fillUsuario(lastUsername); // mismo username del paso anterior
        crearUsuarioPage.fillClave("otraClave7890");
        crearUsuarioPage.selectFirstRole();
        crearUsuarioPage.selectFirstEmployee();
        crearUsuarioPage.submit();
    }

    // ── Escenario 4: Contrasena invalida ────────────────────────────────────

    @When("el usuario ingresa todos los campos con una contrasena de 6 caracteres")
    public void elUsuarioIngresaTodosLosCamposConUnaContrasenaDeSeisCaracteres() {
        crearUsuarioPage.fillUsuario(generateUsername());
        crearUsuarioPage.fillClave("abc12"); // 5 chars <= 6 → error de frontend
        crearUsuarioPage.selectFirstRole();
        crearUsuarioPage.selectFirstEmployee();
        crearUsuarioPage.submit();
    }

    // ── Escenario 5: Campos vacios ───────────────────────────────────────────

    @When("el usuario navega al formulario de creacion de usuarios")
    public void elUsuarioNavegaAlFormularioDeCreacionDeUsuarios() {
        crearUsuarioPage.open();
        crearUsuarioPage.waitForFormToLoad();
    }

    @And("el usuario intenta crear un usuario sin diligenciar los campos")
    public void elUsuarioIntentaCrearUnUsuarioSinDiligenciarLosCampos() {
        // Enviar formulario completamente vacio
        crearUsuarioPage.submit();
    }

    // ── Escenario 6: Acceso no autorizado ────────────────────────────────────

    @When("el usuario intenta acceder al modulo de creacion de usuarios")
    public void elUsuarioIntentaAccederAlModuloDeCreacionDeUsuarios() {
        // roleGuard deniega al rol Ventas → redirige a /unauthorized
        crearUsuarioPage.open();
    }

    // ── Assertions ───────────────────────────────────────────────────────────

    @Then("el sistema muestra el mensaje de exito de usuario {string}")
    public void elSistemaMuestraElMensajeDeExitoDeUsuario(String mensajeEsperado) {
        String mensajeActual = crearUsuarioPage.getSuccessMessage();
        assertThat(normalizarTexto(mensajeActual))
            .as("El mensaje de exito debe coincidir")
            .containsIgnoringCase(normalizarTexto(mensajeEsperado));
    }

    @Then("el sistema muestra el mensaje de error de usuario {string}")
    public void elSistemaMuestraElMensajeDeErrorDeUsuario(String mensajeEsperado) {
        String mensajeActual = crearUsuarioPage.getErrorMessage();
        assertThat(normalizarTexto(mensajeActual))
            .as("El mensaje de error debe coincidir")
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
