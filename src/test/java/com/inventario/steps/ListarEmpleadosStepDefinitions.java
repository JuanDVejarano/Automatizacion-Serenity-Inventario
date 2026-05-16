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

public class ListarEmpleadosStepDefinitions {

    LoginPage loginPage;
    DashboardPage dashboardPage;
    RegistrarEmpleadoPage registrarEmpleadoPage;
    EmpleadosPage empleadosPage;

    // Cedula y nombre del empleado creado para los escenarios de busqueda
    private String searchCedula;
    private String searchNombre;

    private String generateCedula() {
        return String.valueOf(10_000_000L + Math.abs(System.nanoTime() % 89_999_999L));
    }

    private String generateCorreo(String cedula) {
        return "lista" + cedula + "@serenity.test";
    }

    private void createEmployee(String cedula, String nombre) {
        registrarEmpleadoPage.open();
        registrarEmpleadoPage.fillCamposObligatorios(
            cedula, nombre, generateCorreo(cedula), "Calle Lista 1", "Bogota", "3000000099");
        registrarEmpleadoPage.submit();
        registrarEmpleadoPage.getSuccessMessage();
    }

    // ── Background ──────────────────────────────────────────────────────────

    @Given("el administrador ha iniciado sesion para consulta de empleados")
    public void elAdministradorHaIniciadoSesionParaConsultaDeEmpleados() {
        loginPage.open();
        loginPage.enterUsername("admin");
        loginPage.enterPassword("admin1412");
        loginPage.clickLoginButton();
        dashboardPage.waitForLogoutButton();
    }

    // ── Precondiciones ───────────────────────────────────────────────────────

    @Given("existe al menos un empleado registrado en el sistema")
    public void existeAlMenosUnEmpleadoRegistradoEnElSistema() {
        searchCedula = generateCedula();
        createEmployee(searchCedula, "Empleado Lista " + searchCedula);
    }

    @Given("existe un empleado registrado para busqueda en la lista")
    public void existeUnEmpleadoRegistradoParaBusquedaEnLaLista() {
        searchCedula = generateCedula();
        // Nombre con prefijo unico para que la busqueda por nombre sea precisa
        searchNombre  = "BusqTest " + searchCedula;
        createEmployee(searchCedula, searchNombre);
    }

    // ── Navegacion ───────────────────────────────────────────────────────────

    @When("el usuario navega al modulo de listado de empleados")
    public void elUsuarioNavegaAlModuloDeListadoDeEmpleados() {
        empleadosPage.open();
        empleadosPage.waitForList();
    }

    // ── Busqueda ─────────────────────────────────────────────────────────────

    @When("el usuario busca el empleado por nombre en la lista")
    public void elUsuarioBuscaElEmpleadoPorNombreEnLaLista() {
        empleadosPage.open();
        empleadosPage.searchByText(searchNombre);
    }

    @When("el usuario busca el empleado por cedula en la lista")
    public void elUsuarioBuscaElEmpleadoPorCedulaEnLaLista() {
        empleadosPage.open();
        empleadosPage.searchByText(searchCedula);
    }

    @And("el usuario busca por {string} en la lista de empleados")
    public void elUsuarioBuscaPorEnLaListaDeEmpleados(String termino) {
        empleadosPage.searchByText(termino);
    }

    // ── Filtro por estado ────────────────────────────────────────────────────

    @And("el usuario filtra los empleados por estado {string}")
    public void elUsuarioFiltraLosEmpleadosPorEstado(String estado) {
        empleadosPage.filterByEstado(estado);
    }

    // ── Simulacion lista vacia ───────────────────────────────────────────────

    @And("se simula que no hay empleados en el sistema")
    public void seSimulaQueNoHayEmpleadosEnElSistema() {
        // Usa ng.getComponent() (disponible con ng serve en modo dev) para vaciar el signal
        empleadosPage.simulateEmptyList();
    }

    // ── Assertions ───────────────────────────────────────────────────────────

    @Then("el sistema muestra la tabla con las columnas de informacion de empleados")
    public void elSistemaMuestraLaTablaConLasColumnasDeInformacionDeEmpleados() {
        assertThat(empleadosPage.hasRequiredColumns())
            .as("La tabla debe mostrar las columnas: cedula, nombre, correo, ciudad, telefono, fecha, estado")
            .isTrue();
    }

    @And("la tabla contiene al menos un empleado registrado")
    public void laTablaContieneAlMenosUnEmpleadoRegistrado() {
        assertThat(empleadosPage.getVisibleRowCount())
            .as("La tabla debe tener al menos un empleado")
            .isGreaterThan(0);
    }

    @Then("el sistema muestra el empleado en los resultados de busqueda")
    public void elSistemaMuestraElEmpleadoEnLosResultadosDeBusqueda() {
        assertThat(empleadosPage.getVisibleRowCount())
            .as("Debe aparecer al menos un empleado en los resultados de busqueda")
            .isGreaterThan(0);
    }

    @Then("el sistema muestra exactamente un empleado en la tabla de resultados")
    public void elSistemaMuestraExactamenteUnEmpleadoEnLaTablaDeResultados() {
        assertThat(empleadosPage.getVisibleRowCount())
            .as("La busqueda por cedula debe mostrar exactamente un empleado")
            .isEqualTo(1);
    }

    @Then("el sistema muestra en la lista el mensaje {string}")
    public void elSistemaMuestraEnLaListaElMensaje(String mensajeEsperado) {
        String mensajeActual = empleadosPage.getEstadoVacioMessage();
        assertThat(normalizarTexto(mensajeActual))
            .as("El mensaje de lista vacia debe coincidir")
            .containsIgnoringCase(normalizarTexto(mensajeEsperado));
    }

    @Then("todos los empleados mostrados tienen el estado Activo")
    public void todosLosEmpleadosMostradosTienenElEstadoActivo() {
        assertThat(empleadosPage.allVisibleBadgesAreActive())
            .as("Al filtrar por 'activos' todos los badges visibles deben ser badge--activo")
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
